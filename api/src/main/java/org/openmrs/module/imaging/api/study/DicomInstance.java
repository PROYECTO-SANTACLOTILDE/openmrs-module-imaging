package org.openmrs.module.imaging.api.study;

public class DicomInstance {
	
	private String sopInstanceUID;
	
	private String orthancInstanceUID;
	
	private int instanceNumber;
	
	private String instanceModality;
	
	public DicomInstance(String sopInstanceUID, String orthancInstanceUID, int instanceNumber, String instanceModality) {
		this.sopInstanceUID = sopInstanceUID;
		this.orthancInstanceUID = orthancInstanceUID;
		this.instanceNumber = instanceNumber;
		this.instanceModality = instanceModality;
	}
	
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
	
	public String getOrthancInstanceUID() {
		return orthancInstanceUID;
	}
	
	public void setOrthancInstanceUID(String orthancInstanceUID) {
		this.orthancInstanceUID = orthancInstanceUID;
	}
}
