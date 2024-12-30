package org.openmrs.module.imaging.api.study;

import java.util.Date;
import java.util.List;

public class DicomStudy {
	
	private String studyInstanceUID;
	
	private String patientID;
	
	private String patientName;
	
	private Date studyDate;
	
	private String studyDescription;
	
	private List<DicomSeries> seriesList;
	
	// Getters and Setters
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	
	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public String getPatientID() {
		return patientID;
	}
	
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public Date getStudyDate() {
		return studyDate;
	}
	
	public void setStudyDate(Date studyDate) {
		this.studyDate = studyDate;
	}
	
	public String getStudyDescription() {
		return studyDescription;
	}
	
	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}
	
	public List<DicomSeries> getSeriesList() {
		return seriesList;
	}
	
	public void setSeriesList(List<DicomSeries> seriesList) {
		this.seriesList = seriesList;
	}
}
