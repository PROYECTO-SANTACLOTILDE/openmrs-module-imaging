package org.openmrs.module.imaging.api.study;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.imaging.OrthancConfiguration;

import java.util.Date;

public class DicomStudy extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private String studyInstanceUID;
	
	private String orthancStudyUID;
	
	private Patient mrsPatient;
	
	private OrthancConfiguration orthancConfiguration;
	
	private String patientName;
	
	private Date studyDate;
	
	private String studyDescription;
	
	private String gender;
	
	public DicomStudy() {
	}
	
	public DicomStudy(String studyInstanceUID, String orthancStudyUID, Patient patient, OrthancConfiguration config,
	    String patientName, Date studyDate, String studyDescription, String gender) {
		this.studyInstanceUID = studyInstanceUID;
		this.orthancStudyUID = orthancStudyUID;
		this.mrsPatient = patient;
		this.orthancConfiguration = config;
		this.patientName = patientName;
		this.studyDate = studyDate;
		this.studyDescription = studyDescription;
		this.gender = gender;
	}
	
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
	
	@Override
	public Integer getId() {
		return 0;
	}
	
	@Override
	public void setId(Integer integer) {
		
	}
	
	public String getOrthancStudyUID() {
		return orthancStudyUID;
	}
	
	public void setOrthancStudyUID(String orthancStudyUID) {
		this.orthancStudyUID = orthancStudyUID;
	}
}
