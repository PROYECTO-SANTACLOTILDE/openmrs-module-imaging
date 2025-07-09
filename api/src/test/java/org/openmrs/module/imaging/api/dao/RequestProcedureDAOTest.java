package org.openmrs.module.imaging.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//import org.junit.Assert.*;

/**
 * Tests methods in {@link RequestProcedureDao}
 */
public class RequestProcedureDAOTest extends BaseModuleContextSensitiveTest {
	
	private RequestProcedureDao requestProcedureDao;
	
	private static final String REQUEST_PROCEDURE_TEST_DATASET = "testRequestProcedureDataset.xml";
	
	private RequestProcedure requestProcedure;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(REQUEST_PROCEDURE_TEST_DATASET);
		
		// Create a new request procedure object to save
		requestProcedure = new RequestProcedure();
		requestProcedure.setAccessionNumber("ACC-NEW");
		requestProcedure.setStudyInstanceUID("testInstanceUID8888");
		
		Patient patient = Context.getPatientService().getPatient(1);
		requestProcedure.setMrsPatient(patient);
	}
	
	@Test
	public void testGetAll_shouldReturnDataFromRealDB() {
		List<RequestProcedure> allRequests = requestProcedureDao.getAll();
		assertNotNull(allRequests);
		assertEquals(2, allRequests.size());
	}
	
	@Test
	public void testGetAllByStudyInstanceUID_shouldReturnMatchingRecords() {
		String studyInstanceUID = "testInstanceUID8888";
		
		List<RequestProcedure> results = requestProcedureDao.getAllByStudyInstanceUID(studyInstanceUID);
		
		assertNotNull(results);
		assertFalse(results.isEmpty());
		
		// Verify all results have the requested studyInstanceUID
		for (RequestProcedure rp : results) {
			assertEquals(studyInstanceUID, rp.getStudyInstanceUID());
		}
	}
	
	@Test
	public void testGetByPatient_shouldReturnCorrectRequests() {
		Patient patient = Context.getPatientService().getPatient(1); // assuming patient ID 1 exists in test dataset
		List<RequestProcedure> requests = requestProcedureDao.getByPatient(patient);
		assertEquals(2, requests.size());
		for (RequestProcedure rp : requests) {
			assertEquals(patient.getPatientId(), rp.getMrsPatient().getPatientId());
		}
	}
	
	@Test
	public void testGetByAccessNumber_shouldReturnRequestProcedure() {
		String accessNumber = "ACC-001";
		RequestProcedure result = requestProcedureDao.getByAccessionNumber(accessNumber);
		
		assertNotNull(result, "RequestProcedure should not be null");
		assertEquals(accessNumber, result.getAccessionNumber(), "Accession number should match");
	}
	
	@Test
	public void testSave_shouldPersistRequestProcedure() {
		requestProcedureDao.save(requestProcedure);
		
		RequestProcedure fromDb = requestProcedureDao.getByAccessionNumber("ACC-NEW");
		assertNotNull(fromDb);
		assertEquals("ACC-NEW", fromDb.getAccessionNumber());
	}
	
	@Test
	public void testUpdate_shouldModifyExistingRequestProcedure() {
		requestProcedureDao.save(requestProcedure);
		
		RequestProcedure fromDb = requestProcedureDao.getByAccessionNumber("ACC-NEW");
		assertNotNull(fromDb);
		
		// update patient
		fromDb.setMrsPatient(null);
		requestProcedureDao.update(fromDb);
		
		RequestProcedure updated = requestProcedureDao.getByAccessionNumber("ACC-NEW");
		assertEquals("9.8.7.6", updated.getStudyInstanceUID());
	}
	
	@Test
	public void testRemove_shouldDeleteRequestProcedure() {
		requestProcedureDao.save(requestProcedure);
		
		RequestProcedure fromDb = requestProcedureDao.getByAccessionNumber("ACC-NEW");
		assertNotNull(fromDb);
		
		requestProcedureDao.remove(fromDb);
		
		RequestProcedure deleted = requestProcedureDao.getByAccessionNumber("ACC-NEW");
		assertNull(deleted);
	}
}
