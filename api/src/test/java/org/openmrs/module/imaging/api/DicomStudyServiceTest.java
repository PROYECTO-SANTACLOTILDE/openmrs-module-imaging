package org.openmrs.module.imaging.api;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.dao.DicomStudyDao;
import org.openmrs.module.imaging.api.impl.DicomStudyServiceImpl;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.openmrs.module.imaging.api.ClientConnectionPair.setupMockClientWithStatus;

public class DicomStudyServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String DICOMSTUDY_TEST_DATASET = "testDicomStudyDataset.xml";

	private DicomStudyService dicomStudyService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private DicomStudyDao dicomStudyDao;

	@Mock
	private OutputStream outputStream;

	@Before
	public void runBeforeAllTests() throws Exception {
		if (dicomStudyService == null) {
			dicomStudyService = Context.getService(DicomStudyService.class);
		}
		dicomStudyDao = Context.getRegisteredComponent("imaging.DicomStudyDao", DicomStudyDao.class);
		executeDataSet(DICOMSTUDY_TEST_DATASET);
	}
	
	@Test
	public void testGetStudiesOfPatient_shouldGetAllStudyByPatient() throws Exception {
		Patient patientWithStudies = Context.getPatientService().getPatient(1);
		List<DicomStudy> studies = dicomStudyService.getStudiesOfPatient(patientWithStudies);
		assertNotNull(studies);
		assertEquals(2, studies.size());
		for (DicomStudy study : studies) {
			assertEquals(patientWithStudies, study.getMrsPatient());
		}
		
		Patient patientWithoutStudies = Context.getPatientService().getPatient(2);
		List<DicomStudy> noStudies = dicomStudyService.getStudiesOfPatient(patientWithoutStudies);
		assertNotNull(noStudies);
		assertEquals(0, noStudies.size());
	}
	
	@Test
	public void testFetchAllStudies_shouldThrowIOException_whenHttpStatusNotOk() throws Exception {

		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		ClientConnectionPair pair = setupMockClientWithStatus(500, "POST", "/tools/find", "Internal Server Error", config);

		DicomStudyServiceImpl service = new DicomStudyServiceImpl();
		service.setHttpClient(pair.getClient());

		IOException thrown = assertThrows(IOException.class, () -> service.fetchAllStudies(config));
		assertTrue(thrown.getMessage().contains("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error"));
	}
	
	@Test
	public void testFetchAllStudies_successfulResponseCallsCreateOrUpdate() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		ClientConnectionPair pair = setupMockClientWithStatus(200, "POST", "/tools/find", "", config);

		doNothing().when(pair.getClient()).sendOrthancQuery(any(), anyString());
		when(pair.getConnection().getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
		
		String mockJson = "[{ \"PatientID\": \"123\", \"StudyInstanceUID\": \"abc\" }]";
		InputStream inputStream = new ByteArrayInputStream(mockJson.getBytes());
		when(pair.getConnection().getInputStream()).thenReturn(inputStream);
		
		DicomStudyServiceImpl serverImpl = new DicomStudyServiceImpl();
		DicomStudyServiceImpl spyService = Mockito.spy(serverImpl);
		doNothing().when(spyService).createOrUpdateStudy(any(), anyObject());
		
		spyService.fetchAllStudies(config);
		verify(spyService, times(5)).createOrUpdateStudy(eq(config), anyObject());
	}
	
	@Test
	public void testFetchAllStudies_nonOkResponseThrowException() throws IOException {
		OrthancConfiguration config = new OrthancConfiguration();
		config.setOrthancBaseUrl("http://localhost:8042");
		config.setOrthancUsername("user");
		config.setOrthancPassword("pass");

		ClientConnectionPair pair = setupMockClientWithStatus(
				HttpURLConnection.HTTP_UNAVAILABLE,
				"POST",
				"/tools/find",
				"Service Unavailable",
				config
		);

		DicomStudyServiceImpl dicomStudyService = new DicomStudyServiceImpl();
		dicomStudyService.setHttpClient(pair.getClient());

		// Now call and assert
		IOException ex = assertThrows(IOException.class, () ->
				dicomStudyService.fetchAllStudies(config)
		);
		assertTrue(ex.getMessage().contains("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error"));
	}

	@Test
	public void testUpload_success() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		byte[] data = "dummy DICOM data".getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

		ClientConnectionPair pair = setupMockClientWithStatus(200, "POST", "/instances", "", config);
		HttpURLConnection mockConnection = pair.getConnection();

		when(pair.getConnection().getOutputStream()).thenReturn(outputStream);

		// Inject mock client into service
		dicomStudyService.setHttpClient(pair.getClient()); // Make sure this sets the client

		int result = dicomStudyService.uploadFile(config, inputStream);

		assertEquals(200, result);

		// Verifications
		verify(mockConnection).setRequestProperty("Content-Type", "application/dicom");
		verify(mockConnection).setDoOutput(true);
		verify(outputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
	}

	@Test
	public void testCreateOrUpdateStudy_createsNewStudyWhenNoneExists() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		String jsonString = "{\n" +
				"  \"ID\": \"OrthanUID123\",\n" +
				"  \"MainDicomTags\": {\n" +
				"    \"StudyInstanceUID\": \"testStudyUID123\",\n" +
				"    \"StudyDate\": \"20250701\",\n" +
				"    \"StudyTime\": \"123456\",\n" +
				"    \"StudyDescription\": \"Test new or update study description\"\n" +
				"  },\n" +
				"  \"PatientMainDicomTags\": {\n" +
				"    \"PatientName\": \"TestOrthancPatient\",\n" +
				"    \"Gender\": \"M\"\n" +
				"  }\n" +
				"}";

		JsonNode jsonData = objectMapper.readTree(jsonString);

		DicomStudy foundStudy = dicomStudyDao.getByStudyInstanceUID(config, "testStudyUID123");
		assertNull(foundStudy);

		dicomStudyService.createOrUpdateStudy(config, jsonData);

		DicomStudy savedStudy = dicomStudyDao.getByStudyInstanceUID(config, "testStudyUID123");
		assertNotNull(savedStudy);
		assertEquals("OrthanUID123", savedStudy.getOrthancStudyUID());
		assertEquals("TestOrthancPatient", savedStudy.getPatientName());
		assertEquals("Test new or update study description", savedStudy.getStudyDescription());
	}

	@Test
	public void testCreateOrUpdateStudy_updatesExistingStudy() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);
		Patient patient = Context.getPatientService().getPatient(1);

		String jsonString = "{\n" +
				"  \"ID\": \"orthancUID123\",\n" +
				"  \"MainDicomTags\": {\n" +
				"    \"StudyInstanceUID\": \"studyInstanceUID444\",\n" +
				"    \"StudyDate\": \"2025-07-11\",\n" +
				"    \"StudyTime\": \"14:35:00\",\n" +
				"    \"StudyDescription\": \"CT Head without contrast\"\n" +
				"  },\n" +
				"  \"PatientMainDicomTags\": {\n" +
				"    \"PatientName\": \"Test Imaging\",\n" +
				"    \"Gender\": \"F\"\n" +
				"  }\n" +
				"}";
		JsonNode studyData = objectMapper.readTree(jsonString);

		DicomStudy existingStudy = new DicomStudy("studyInstanceUID444", "orthancUID444", patient, config,
				"Test Imaging", "2025-07-11", "14:35:00", "CT Head without contrast", "F");

		DicomStudy foundStudy = dicomStudyDao.getByStudyInstanceUID(config,existingStudy.getStudyInstanceUID());
		assertNotNull(foundStudy);
		assertEquals("orthancUID444", existingStudy.getOrthancStudyUID());

		dicomStudyService.createOrUpdateStudy(config, studyData);
		DicomStudy updatedStudy = dicomStudyDao.getByStudyInstanceUID(config, "studyInstanceUID444");
		assertNotNull(updatedStudy);

		DicomStudy foundUpdateStudy = dicomStudyDao.getByPatient(patient).get(1);
		assertEquals("orthancUID123", foundUpdateStudy.getOrthancStudyUID());
		assertEquals("studyInstanceUID444", foundUpdateStudy.getStudyInstanceUID());
	}

	@Test
	public void testGetStudiesOfPatient() {
		Patient patient = Context.getPatientService().getPatient(1);
		List<DicomStudy> result = dicomStudyService.getStudiesOfPatient(patient);
		assertNotNull(result);
	}

	@Test
	public void testGetStudiesByConfiguration() {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config1 = orthancConfigurationService.getOrthancConfiguration(1);

		List<DicomStudy> studies1 = dicomStudyService.getStudiesByConfiguration(config1);
		assertNotNull(studies1);

		OrthancConfiguration config2 = orthancConfigurationService.getOrthancConfiguration(2);
		List<DicomStudy> studies2 = dicomStudyService.getStudiesByConfiguration(config2);
		assertTrue(studies2.isEmpty());
	}
}

