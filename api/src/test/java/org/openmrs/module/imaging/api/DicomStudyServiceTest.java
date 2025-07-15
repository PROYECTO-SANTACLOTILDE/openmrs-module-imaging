package org.openmrs.module.imaging.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.context.Context;
import org.openmrs.Patient;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.client.OrthancHttpClient;
import org.openmrs.module.imaging.api.impl.DicomStudyServiceImpl;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DicomStudyServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String DICOMSTUDY_TEST_DATASET = "testDicomStudyDataset.xml";

//	@InjectMocks
	private DicomStudyService dicomStudyService;

	@Mock
	private HttpURLConnection connection;

	@Mock
	private OrthancConfiguration configuration;

	@Mock
	private OrthancConfigurationService orthancConfigurationService;

	
	@Before
	public void runBeforeAllTests() throws Exception {
		if (dicomStudyService == null) {
			dicomStudyService = Context.getService(DicomStudyService.class);
		}
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

		OrthancHttpClient mockClient = setupMockClientWithStatus(500, "Internal Server Error", config);

		DicomStudyServiceImpl service = new DicomStudyServiceImpl();
		service.setHttpClient(mockClient);

		IOException thrown = assertThrows(IOException.class, () -> service.fetchAllStudies(config));
		assertTrue(thrown.getMessage().contains("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error"));
	}

	@Test
	public void testFetchAllStudies_successfulResponseCallsCreateOrUpdate() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		OrthancHttpClient mockClient = setupMockClientWithStatus(200, null, config);

		doNothing().when(mockClient).sendOrthancQuery(any(), anyString());
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

		String mockJson = "[{ \"PatientID\": \"123\", \"StudyInstanceUID\": \"abc\" }]";
		InputStream inputStream = new ByteArrayInputStream(mockJson.getBytes());
		when(connection.getInputStream()).thenReturn(inputStream);

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

		OrthancHttpClient mockClient = setupMockClientWithStatus(
				HttpURLConnection.HTTP_UNAVAILABLE,
				"Service Unavailable",
				config
		);

		DicomStudyServiceImpl dicomStudyService = new DicomStudyServiceImpl();
		dicomStudyService.setHttpClient(mockClient);

		// Now call and assert
		IOException ex = assertThrows(IOException.class, () ->
				dicomStudyService.fetchAllStudies(config)
		);
		assertTrue(ex.getMessage().contains("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error"));
	}

	@Test

	/**
	 *
	 * @param statusCode
	 * @param errorMessage
	 * @return
	 * @throws IOException
	 */
	private OrthancHttpClient setupMockClientWithStatus(int statusCode, String errorMessage, OrthancConfiguration config) throws IOException {
		OrthancHttpClient mockClient = mock(OrthancHttpClient.class);
		HttpURLConnection mockCon = mock(HttpURLConnection.class);
		when(mockClient.createConnection(
				eq("POST"),
				eq(config.getOrthancBaseUrl()),
				eq("/tools/find"),
				eq(config.getOrthancUsername()),
				eq(config.getOrthancPassword())
		)).thenReturn(mockCon);

		when(mockClient.getStatus(mockCon)).thenReturn(statusCode);
		when(mockClient.getErrorMessage(mockCon)).thenReturn(errorMessage);
		return mockClient;
	}
}
