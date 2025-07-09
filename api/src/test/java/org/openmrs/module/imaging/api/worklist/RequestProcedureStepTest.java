package org.openmrs.module.imaging.api.worklist;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RequestProcedureStepTest {
	
	@Test
	public void testConstructorAndGetters() {
		RequestProcedureStep step = new RequestProcedureStep();
		RequestProcedure procedure = new RequestProcedure();
		
		step.setRequestProcedure(procedure);
		step.setModality("CT");
		step.setAetTitle("AET123");
		step.setScheduledReferringPhysician("Dr. House");
		step.setRequestedProcedureDescription("CT Chest");
		step.setStepStartDate("2025-07-03");
		step.setStepStartTime("14:30");
		step.setPerformedProcedureStepStatus("Scheduled");
		step.setStationName("Station A");
		step.setProcedureStepLocation("Room 5");
		
		assertEquals(procedure, step.getRequestProcedure());
		assertEquals("CT", step.getModality());
		assertEquals("AET123", step.getAetTitle());
		assertEquals("Dr. House", step.getScheduledReferringPhysician());
		assertEquals("CT Chest", step.getRequestedProcedureDescription());
		assertEquals("2025-07-03", step.getStepStartDate());
		assertEquals("14:30", step.getStepStartTime());
		assertEquals("Scheduled", step.getPerformedProcedureStepStatus());
		assertEquals("Station A", step.getStationName());
		assertEquals("Room 5", step.getProcedureStepLocation());
	}
	
	@Test
	public void testAllArgsConstructor() {
		RequestProcedure procedure = new RequestProcedure();
		RequestProcedureStep step = new RequestProcedureStep(procedure, "MRI", "Dr. Cuddy", "MRI Brain", "AET456",
		        "2025-07-04", "10:00", "In Progress", "Station B", "Room 2");
		
		assertEquals(procedure, step.getRequestProcedure());
		assertEquals("MRI", step.getModality());
		assertEquals("Dr. Cuddy", step.getScheduledReferringPhysician());
		assertEquals("MRI Brain", step.getRequestedProcedureDescription());
		assertEquals("AET456", step.getAetTitle());
		assertEquals("2025-07-04", step.getStepStartDate());
		assertEquals("10:00", step.getStepStartTime());
		assertEquals("In Progress", step.getPerformedProcedureStepStatus());
		assertEquals("Station B", step.getStationName());
		assertEquals("Room 2", step.getProcedureStepLocation());
	}
}
