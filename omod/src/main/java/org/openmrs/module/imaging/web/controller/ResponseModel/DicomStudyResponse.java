package org.openmrs.module.imaging.web.controller.ResponseModel;

import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomStudy;

import java.util.List;
import java.util.stream.Collectors;

public class DicomStudyResponse {
	
	private Integer id;
	
	private String studyInstanceUID;
	
	private String orthancStudyUID;
	
	private String mrsPatientUuid;
	
	private OrthancConfigurationResponse orthancConfiguration;
	
	private String patientName;
	
	private String studyDate;
	
	private String studyDescription;
	
	private String gender;
	
	public static DicomStudyResponse createResponse(DicomStudy study) {
		DicomStudyResponse response = new DicomStudyResponse();
		response.setId(study.getId());
		response.setStudyInstanceUID(study.getStudyInstanceUID());
		response.setMrsPatientUuid(study.getMrsPatient() == null ? null : study.getMrsPatient().getUuid());
		response.setOrthancStudyUID(study.getOrthancStudyUID());
		response.setPatientName(study.getPatientName());
		response.setStudyDate(study.getStudyDate());
		response.setStudyDescription(study.getStudyDescription());
		response.setGender(study.getGender());
		response.setOrthancConfiguration(OrthancConfigurationResponse.createResponse(study.getOrthancConfiguration()));
		return response;
	}
	
	public static List<DicomStudyResponse> createResponse(List<DicomStudy> studies) {
        return studies.stream().map(DicomStudyResponse::createResponse).collect(Collectors.toList());
    }
	
	public OrthancConfigurationResponse getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfigurationResponse orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStudyInstanceUID() {
		return studyInstanceUID;
	}
	
	public void setStudyInstanceUID(String studyInstanceUID) {
		this.studyInstanceUID = studyInstanceUID;
	}
	
	public String getOrthancStudyUID() {
		return orthancStudyUID;
	}
	
	public void setOrthancStudyUID(String orthancStudyUID) {
		this.orthancStudyUID = orthancStudyUID;
	}
	
	public String getPatientName() {
		return patientName;
	}
	
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	public String getStudyDate() {
		return studyDate;
	}
	
	public void setStudyDate(String studyDate) {
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
	
	public void setMrsPatientUuid(String patientUuid) {
		this.mrsPatientUuid = patientUuid;
	}
	
	public String getMrsPatientUuid() {
		return mrsPatientUuid;
	}
}
