package org.openmrs.module.imaging.api.study;

import org.junit.jupiter.api.Test;
import org.openmrs.module.imaging.OrthancConfiguration;

import static org.junit.jupiter.api.Assertions.*;

public class DicomInstanceTest {
	
	@Test
	public void testConstructorAndGetters() {
		
		OrthancConfiguration config = new OrthancConfiguration();
		
		DicomInstance dicomInstance = new DicomInstance("sop_instanceUID_123", "orthanc123", "5", "10\\20\\30", "30", config);
		
		assertEquals("sop_instanceUID_123", dicomInstance.getSopInstanceUID());
		assertEquals("orthanc123", dicomInstance.getOrthancInstanceUID());
		assertEquals("5", dicomInstance.getInstanceNumber());
		assertEquals("10\\20\\30", dicomInstance.getImagePositionPatient());
		assertEquals("30", dicomInstance.getNumberOfFrames());
		assertEquals(config, dicomInstance.getOrthancConfiguration());
	}
	
	@Test
	public void testSettersAndGetters() {
		DicomInstance instance = new DicomInstance();
		OrthancConfiguration config = new OrthancConfiguration();
		
		instance.setSopInstanceUID("sop_test_instanceUID_123");
		instance.setOrthancInstanceUID("orth_test_UID_456");
		instance.setInstanceNumber("10");
		instance.setImagePositionPatient("0\\0\\1");
		instance.setOrthancConfiguration(config);
		
		assertEquals("sop_test_instanceUID_123", instance.getSopInstanceUID());
		assertEquals("orth_test_UID_456", instance.getOrthancInstanceUID());
		assertEquals("10", instance.getInstanceNumber());
		assertEquals("0\\0\\1", instance.getImagePositionPatient());
		assertEquals(config, instance.getOrthancConfiguration());
		
		// Default null unless set via constructor
		assertNull(instance.getNumberOfFrames());
	}
}
