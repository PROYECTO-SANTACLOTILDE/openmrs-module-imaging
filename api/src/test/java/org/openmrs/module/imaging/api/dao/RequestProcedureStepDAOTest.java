package org.openmrs.module.imaging.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.imaging.api.worklist.RequestProcedureStep;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests methods in {@link RequestProcedureStepDao}
 */
public class RequestProcedureStepDAOTest extends BaseModuleContextSensitiveTest {
	
	private RequestProcedureStepDao requestProcedureStepDao;
	
	private RequestProcedureDao requestProcedureDao;
	
	private static final String REQUEST_PROCEDURE_STEP_TEST_DATASET = "testRequestProcedureStepDataset.xml";
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(REQUEST_PROCEDURE_STEP_TEST_DATASET);
		assertNotNull(applicationContext);
		requestProcedureStepDao = (RequestProcedureStepDao) applicationContext.getBean("imaging.RequestProcedureStepDao");
	}
	
	@Test
	public void testGetAll_shouldReturnAllSteps() {
		List<RequestProcedureStep> steps = requestProcedureStepDao.getAll();
		assertEquals(3, steps.size());
	}
	
	//	@Test
	//	public void testGetAllStepByRequestProcedure_shouldReturnAssociatedSteps() {
	//		RequestProcedure procedure = requestProcedureDao.get(1);
	//		List<RequestProcedureStep> steps = requestProcedureStepDao.getAllStepByRequestProcedure(procedure);
	//		assertEquals(1, steps.size());
	//		assertEquals("scheduled", steps.get(0).getPerformedProcedureStepStatus());
	//	}
	//
	//	@Test
	//	public void testGetById_shouldReturnCorrectProcedure() {
	//		RequestProcedureStep step = requestProcedureStepDao.get(3001);
	//		assertNotNull(step);
	//		assertEquals("CT", step.getModality());
	//		assertEquals("Check for gallstones", step.getRequestedProcedureDescription());
	//		assertEquals("Station1", step.getStationName());
	//	}
	//
	//	@Test
	//	public void testSave_shouldAddNewStep() {
	//		RequestProcedure procedure = requestProcedureDao.get(1);
	//		RequestProcedureStep newStep = new RequestProcedureStep();
	//		newStep.setRequestProcedure(procedure);
	//		newStep.setModality("CT");
	//		newStep.setRequestedProcedureDescription("CT Scan Chest");
	//		newStep.setAetTitle("AET2");
	//		newStep.setStepStartDate("2025-07-03");
	//		newStep.setStepStartTime("10:00");
	//		newStep.setPerformedProcedureStepStatus("PLANNED");
	//		newStep.setStationName("CT Room");
	//		newStep.setProcedureStepLocation("Radiology");
	//
	//		requestProcedureStepDao.save(newStep);
	//		assertNotNull(newStep.getId());
	//	}
	//
	//	@Test
	//	public void testRemove_shouldDeleteStep() {
	//		RequestProcedureStep step = requestProcedureStepDao.get(1);
	//		assertNotNull(step);
	//		requestProcedureStepDao.remove(step);
	//
	//		RequestProcedureStep deleted = requestProcedureStepDao.get(1);
	//		assertNull(deleted);
	//	}
}
