package org.openmrs.module.imaging.api.worklist;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.imaging.OrthancConfiguration;

public class RequestProcedure extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer id;
	
	private String createdDate;
	
	private String status; //e.g. active, completed
	
	private OrthancConfiguration orthancConfiguration;
	
	private Patient mrsPatient;
	
	private String accessionNumber;
	
	private String studyInstanceUID;
	
	public RequestProcedure() {
	}
	
	public RequestProcedure(String createdDate, String status, Patient mrsPatient, OrthancConfiguration config,
	    String accessionNumber, String studyInstanceUID) {
		this.createdDate = createdDate;
		this.status = status;
		this.mrsPatient = mrsPatient;
		this.orthancConfiguration = config;
		this.accessionNumber = accessionNumber;
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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
	
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	
	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}
}
