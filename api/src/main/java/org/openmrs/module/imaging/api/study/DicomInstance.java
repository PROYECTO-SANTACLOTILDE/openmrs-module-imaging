package org.openmrs.module.imaging.api.study;

import org.openmrs.BaseOpenmrsData;

public class DicomInstance extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	private String sopInstanceUID;
	private String orthancInstanceUID;
	private String instanceNumber;
	private String imagePositionPatient;
	
	public DicomInstance() {
	}
	
	public DicomInstance(String sopInstanceUID, String orthancInstanceUID, String instanceNumber, String imagePositionPatient) {
		this.sopInstanceUID = sopInstanceUID;
		this.orthancInstanceUID = orthancInstanceUID;
		this.instanceNumber = instanceNumber;
		this.imagePositionPatient = imagePositionPatient;
	}
	
	// Getters and Setters
	public String getSopInstanceUID() {
		return sopInstanceUID;
	}
	
	public void setSopInstanceUID(String sopInstanceUID) {
		this.sopInstanceUID = sopInstanceUID;
	}
	
	public String getInstanceNumber() {
		return instanceNumber;
	}
	
	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}
	
	public String getOrthancInstanceUID() {
		return orthancInstanceUID;
	}
	
	public void setOrthancInstanceUID(String orthancInstanceUID) {
		this.orthancInstanceUID = orthancInstanceUID;
	}
	
	public String getImagePositionPatient() {
		return imagePositionPatient;
	}
	
	public void setImagePositionPatient(String imagePositionPatient) {
		this.imagePositionPatient = imagePositionPatient;
	}
	
	@Override
	public Integer getId() {
		return 0;
	}
	
	@Override
	public void setId(Integer integer) {
	}
}
