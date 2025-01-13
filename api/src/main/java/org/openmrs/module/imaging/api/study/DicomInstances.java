package org.openmrs.module.imaging.api.study;

public class DicomInstances {
	
	private String sopInstanceUID;
	
	private int instanceNumber;
	
	private String instanceModality;

	// Getters and Setters
	public String getSopInstanceUID() {
		return sopInstanceUID;
	}
	
	public void setSopInstanceUID(String sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}
	
	public int getInstanceNumber() {
		return instanceNumber;
	}
	
	public void setInstanceNumber(int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	
	public String getInstanceModality() {
		return instanceModality;
	}
	
	public void setInstanceModality(String instanceModality) {
		this.instanceModality = instanceModality;
	}
}
