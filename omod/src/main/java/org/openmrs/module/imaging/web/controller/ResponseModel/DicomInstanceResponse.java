package org.openmrs.module.imaging.web.controller.ResponseModel;

import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomInstance;

public class DicomInstanceResponse {
	
	private String sopInstanceUID;
	
	private String instanceNumber;
	
	private String imagePositionPatient;
	
	private String numberOfFrames;
	
	private OrthancConfigurationResponse orthancConfiguration;
	
	public static DicomInstanceResponse createResponse(DicomInstance dicomInstance) {
		DicomInstanceResponse response = new DicomInstanceResponse();
		response.setSopInstanceUID(dicomInstance.getSopInstanceUID());
		response.setInstanceNumber(dicomInstance.getInstanceNumber());
		response.setImagePositionPatient(dicomInstance.getImagePositionPatient());
		response.setNumberOfFrames(dicomInstance.getNumberOfFrames());
		response.setOrthancConfiguration(OrthancConfigurationResponse.createResponse(dicomInstance.getOrthancConfiguration()));
		return response;
	}
	
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
	
	public String getImagePositionPatient() {
		return imagePositionPatient;
	}
	
	public void setImagePositionPatient(String imagePositionPatient) {
		this.imagePositionPatient = imagePositionPatient;
	}
	
	public String getNumberOfFrames() {
		return numberOfFrames;
	}
	
	public void setNumberOfFrames(String numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}
	
	public OrthancConfigurationResponse getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfigurationResponse orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
}
