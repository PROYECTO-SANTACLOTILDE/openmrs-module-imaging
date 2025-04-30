package org.openmrs.module.imaging.web.controller.ResponseModel;

import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureStep;

public class ProcedureStepResponse {
	
	private Integer id;
	
	private Integer requestProcedureId;
	
	private String modality;
	
	private String aetTitle;
	
	private String scheduledReferringPhysician;
	
	private String requestedProcedureDescription;
	
	private String stepStartDate;
	
	private String stepStartTime;
	
	private String performedProcedureStepStatus;
	
	private String stationName;
	
	private String procedureStepLocation;
	
	/**
	 * @param step
	 * @return
	 */
	public static ProcedureStepResponse createResponse(RequestProcedureStep step) {
		ProcedureStepResponse response = new ProcedureStepResponse();
		
		response.setId(step.getId());
		response.setRequestProcedureId(step.getRequestProcedure().getId());
		response.setModality(step.getModality());
		response.setAetTitle(step.getAetTitle());
		response.setScheduledReferringPhysician(step.getScheduledReferringPhysician());
		response.setRequestedProcedureDescription(step.getRequestedProcedureDescription());
		response.setStepStartDate(step.getStepStartDate());
		response.setStepStartTime(step.getStepStartTime());
		response.setPerformedProcedureStepStatus(step.getPerformedProcedureStepStatus());
		response.setStationName(step.getStationName());
		response.setProcedureStepLocation(step.getProcedureStepLocation());
		return response;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getModality() {
		return modality;
	}
	
	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public String getAetTitle() {
		return aetTitle;
	}
	
	public void setAetTitle(String aetTitle) {
		this.aetTitle = aetTitle;
	}
	
	public String getScheduledReferringPhysician() {
		return scheduledReferringPhysician;
	}
	
	public void setScheduledReferringPhysician(String scheduledReferringPhysician) {
		this.scheduledReferringPhysician = scheduledReferringPhysician;
	}
	
	public String getRequestedProcedureDescription() {
		return requestedProcedureDescription;
	}
	
	public void setRequestedProcedureDescription(String requestedProcedureDescription) {
		this.requestedProcedureDescription = requestedProcedureDescription;
	}
	
	public String getStepStartDate() {
		return stepStartDate;
	}
	
	public void setStepStartDate(String stepStartDate) {
		this.stepStartDate = stepStartDate;
	}
	
	public String getStepStartTime() {
		return stepStartTime;
	}
	
	public void setStepStartTime(String stepStartTime) {
		this.stepStartTime = stepStartTime;
	}
	
	public String getPerformedProcedureStepStatus() {
		return performedProcedureStepStatus;
	}
	
	public void setPerformedProcedureStepStatus(String performedProcedureStepStatus) {
		this.performedProcedureStepStatus = performedProcedureStepStatus;
	}
	
	public String getStationName() {
		return stationName;
	}
	
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	public String getProcedureStepLocation() {
		return procedureStepLocation;
	}
	
	public void setProcedureStepLocation(String procedureStepLocation) {
		this.procedureStepLocation = procedureStepLocation;
	}
	
	public Integer getRequestProcedureId() {
		return requestProcedureId;
	}
	
	public void setRequestProcedureId(Integer requestProcedureId) {
		this.requestProcedureId = requestProcedureId;
	}
}
