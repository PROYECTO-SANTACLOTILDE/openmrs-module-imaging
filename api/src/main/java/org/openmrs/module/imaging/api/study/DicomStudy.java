package org.openmrs.module.imaging.api.study;

import org.openmrs.Patient;
import org.openmrs.module.imaging.OrthancConfiguration;

import java.util.Date;

public class DicomStudy {
	
	private String studyInstanceUID;
	
	private Patient mrsPatient;
	
	private OrthancConfiguration orthancConfiguration;
	
	private String patientName;
	
	private Date studyDate;
	
	private String studyDescription;
	
	private String gender;
	
	// Getters and Setters
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	
	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
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
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public OrthancConfiguration getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
	
	public Patient getMrsPatient() {
		return mrsPatient;
	}
	
	public void setMrsPatient(Patient mrsPatient) {
		this.mrsPatient = mrsPatient;
	}
}
