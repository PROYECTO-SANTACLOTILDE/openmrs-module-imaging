package org.openmrs.module.imaging.web.controller.ResponseModel;

import org.openmrs.module.imaging.api.worklist.RequestProcedure;

public class RequestProcedureResponse {
	
	private Integer id;
	
	private String status; //e.g. active, completed
	
	private OrthancConfigurationResponse orthancConfigurationResponse;
	
	private String mrsPatientUuid;
	
	private String accessionNumber;
	
	private String studyInstanceUID;
	
	private String requestingPhysician;
	
	private String requestDescription;
	
	private String priority;
	
	/**
	 * @param requestProcedure
	 * @return
	 */
	public static RequestProcedureResponse createResponse(RequestProcedure requestProcedure) {
		RequestProcedureResponse response = new RequestProcedureResponse();
		response.setId(requestProcedure.getId());
		response.setStatus(requestProcedure.getStatus());
		response.setMrsPatientUuid(requestProcedure.getMrsPatient().getUuid());
		response.setAccessionNumber(requestProcedure.getAccessionNumber());
		response.setStudyInstanceUID(requestProcedure.getStudyInstanceUID());
		response.setRequestingPhysician(requestProcedure.getRequestingPhysician());
		response.setRequestDescription(requestProcedure.getRequestingPhysician());
		response.setPriority(requestProcedure.getPriority());
		response.setOrthancConfiguration(OrthancConfigurationResponse.createResponse(requestProcedure
		        .getOrthancConfiguration()));
		return response;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public OrthancConfigurationResponse getOrthancConfiguration() {
		return this.orthancConfigurationResponse;
	}
	
	public void setOrthancConfiguration(OrthancConfigurationResponse orthancConfigurationResponse) {
		this.orthancConfigurationResponse = orthancConfigurationResponse;
	}
	
	public String getMrsPatientUuid() {
		return mrsPatientUuid;
	}
	
	public void setMrsPatientUuid(String mrsPatientUuid) {
		this.mrsPatientUuid = mrsPatientUuid;
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
	
	public String getRequestingPhysician() {
		return requestingPhysician;
	}
	
	public void setRequestingPhysician(String requestingPhysician) {
		this.requestingPhysician = requestingPhysician;
	}
	
	public String getRequestDescription() {
		return requestDescription;
	}
	
	public void setRequestDescription(String requestDescription) {
		this.requestDescription = requestDescription;
	}
	
	public String getPriority() {
		return priority;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}
}
