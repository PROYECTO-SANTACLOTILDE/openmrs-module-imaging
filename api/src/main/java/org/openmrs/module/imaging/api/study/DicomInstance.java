package org.openmrs.module.imaging.api.study;

public class DicomInstance {
	
	private String sopInstanceUID;
	
	private int instanceNumber;
	
	private String instanceModality;
	
	private byte[] pixelData; // Stores raw pixel data (optional, if needed)
	
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
	
	public void setImageType(String instanceModality) {
		this.instanceModality = instanceModality;
	}
	
	public byte[] getPixelData() {
		return pixelData;
	}
	
	public void setPixelData(byte[] pixelData) {
		this.pixelData = pixelData;
	}
}
