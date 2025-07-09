package org.openmrs.module.imaging.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.hibernate.SessionFactory;

/**
 * Tests methods in {@link DicomStudyDao}
 */
public class DicomStudyDAOTest extends BaseModuleContextSensitiveTest {
	
	private static final String DICOMSTUDY_TEST_DATASET = "testDicomStudyDataset.xml";
	
	DicomStudyService dicomStudyService = null;
	
	DicomStudyDao dicomStudyDao = null;
	
	//private DicomStudy dicomStudy;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(DICOMSTUDY_TEST_DATASET);
		dicomStudyService = Context.getService(DicomStudyService.class);
	}
	
	@Test
	public void testGetById_shouldReturnStudy() throws Exception {
		Patient patient = Context.getPatientService().getPatient(1);
		//		DicomStudy study = dicomStudyDao.get(1);
		//		assertNotNull(study);
		//		assertEquals(1, study.getId().intValue());
	}
	
	//	@Test
	//	public void testGetAll_shouldReturnAllStudies() throws Exception {
	//		List<DicomStudy> allStudies = dicomStudyDao.getAll();
	//		assertNotNull(allStudies);
	//		assertFalse(allStudies.isEmpty());
	//	}
	//
	//	@Test
	//	public void testGetByPatient_shouldReturnStudiesForPatient() throws Exception {
	//		Patient patient = Context.getPatientService().getPatient(1);
	//		List<DicomStudy> studies = dicomStudyDao.getByPatient(patient);
	//		assertEquals(1, studies.size());
	//		assertNotNull(studies);
	//	}
	//
	//	@Test
	//	public void testGetByConfiguration_shouldReturnStudies() throws Exception {
	//		OrthancConfigurationService configService = Context.getService(OrthancConfigurationService.class);
	//		OrthancConfiguration config = configService.getOrthancConfiguration(1);
	//
	//		List<DicomStudy> result = dicomStudyDao.getByConfiguration(config);
	//		assertNotNull(result);
	//		assertEquals("studyInstanceUID999", result.get(0).getStudyInstanceUID());
	//		assertEquals("orthancUID888", result.get(0).getOrthancStudyUID());
	//	}
	//
	//	@Test
	//	public void testGetByStudyInstanceUID_shouldReturnCorrectStudy() throws Exception {
	//		OrthancConfigurationService configService = Context.getService(OrthancConfigurationService.class);
	//		OrthancConfiguration config = configService.getOrthancConfiguration(1);
	//
	//		DicomStudy study = dicomStudyDao.getByStudyInstanceUID(config, "studyInstanceUID999");
	//		assertNotNull(study);
	//		assertEquals("studyInstanceUID999", study.getStudyInstanceUID());
	//		assertEquals("orthancUID888", study.getOrthancStudyUID());
	//	}
	
	//	@Test
	//	public void testSave_shouldPersistStudy() throws Exception {
	//		OrthancConfigurationService configService =
	//				Context.getService(OrthancConfigurationService.class);
	//		OrthancConfiguration config = configService.getOrthancConfiguration(1);
	//
	//		Patient patient = Context.getPatientService().getPatient(1);
	//
	//		DicomStudy newStudy = new DicomStudy();
	//		newStudy.setMrsPatient(patient);
	//		newStudy.setStudyInstanceUID("studyInstanceUID777");
	//		newStudy.setOrthancStudyUID("newOrthancUID222");
	//		newStudy.setStudyDescription("Test_New_Study");
	//		newStudy.setOrthancConfiguration(config);
	//
	//		dicomStudyDao.save(newStudy);
	//
	//		DicomStudy retrieved = dicomStudyDao.getByStudyInstanceUID(config, "studyInstanceUID777");
	//		assertNotNull(retrieved);
	//		assertEquals("Test_New_Study", retrieved.getStudyDescription());
	//		assertEquals("newOrthancUID222", retrieved.getOrthancStudyUID());
	//	}
	
	//	@Test
	//	public void testRemove_shouldDeleteStudy() throws Exception {
	//		OrthancConfigurationService configService =
	//				Context.getService(OrthancConfigurationService.class);
	//		OrthancConfiguration config = configService.getOrthancConfiguration(1);
	//
	//		DicomStudy study = dicomStudyDao.getByStudyInstanceUID(config, "testInstanceUID9999");
	//
	//		assertNotNull(study);
	//		dicomStudyDao.remove(study);
	//
	//		DicomStudy deleted = dicomStudyDao.getByStudyInstanceUID(config, "testInstanceUID9999");
	//		assertNull(deleted);
	//	}
	
	//	@Test
	//	public void testHasStudyTrue_shouldReturnTrueIfStudyExists() {
	//		OrthancConfiguration config = Context.getService(OrthancConfigurationService.class).getOrthancConfiguration(1);
	//		assertTrue(dicomStudyDao.hasStudy(config));
	//	}
	
	//	@Test
	//	public void testHasStudyFalse_shouldReturnFalseIfNoStudyExists() {
	//
	//		// Create and save a new OrthancConfiguration
	//		OrthancConfiguration config = new OrthancConfiguration();
	//		config.setOrthancBaseUrl("http://localhost:8062");
	//		config.setOrthancProxyUrl("http://proxy:8062");
	//		config.setOrthancUsername("user1");
	//		config.setOrthancPassword("user1");
	//		config.setLastChangedIndex(-1);
	//
	//		// Persist it
	//		OrthancConfigurationService configService = Context.getService(OrthancConfigurationService.class);
	//		configService.saveOrthancConfiguration(config);
	//
	//		// Test that no studies exist for this new configuration
	//		assertFalse(dicomStudyDao.hasStudy(config));
	//
	//		// try to fetch a study and expect null
	//		DicomStudy study = dicomStudyDao.getByStudyInstanceUID(config, "nonexistentUID");
	//		assertNull(study);
	//	}
}
