/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.imaging;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.imaging.api.study.DicomStudy;

public class DicomWorklist extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer worklistID;
	
	private String accessionNumber;
	
	private OrthancConfiguration orthancConfiguration;
	
	private DicomStudy study;
	
	private Patient mrsPatient;
	
	private String referringPhysician;
	
	private String studyDescription;
	
	private String stepStartDate;
	
	private String stepStartTime;
	
	private String modality;
	
	private String reqProcedureID;
	
	private String reqProcedureDescription;
	
	private String stationAETitle;
	
	private String performingPhysician;
	
	private String procedureStepLocation;
	
	private String preMedication;
	
	private String specialNeeds;
	
	public DicomWorklist() {
	}
	
	public DicomWorklist(int worklistID, String accessionNumber, OrthancConfiguration orthancConfiguration,
	    DicomStudy study, Patient mrPatient, String referringPhysician, String studyDescription, String stepStartDate,
	    String stepStartTime, String modality, String reqProcedureID, String reqProcedureDescription, String stationAETitle,
	    String performingPhysician, String procedureStepLocation, String preMedication, String specialNeeds) {
		this.worklistID = worklistID;
		this.accessionNumber = accessionNumber;
		this.orthancConfiguration = orthancConfiguration;
		this.study = study;
		this.mrsPatient = mrPatient;
		this.referringPhysician = referringPhysician;
		this.studyDescription = studyDescription;
		this.stepStartDate = stepStartDate;
		this.stepStartTime = stepStartTime;
		this.modality = modality;
		this.reqProcedureID = reqProcedureID;
		this.reqProcedureDescription = reqProcedureDescription;
		this.stationAETitle = stationAETitle;
		this.performingPhysician = performingPhysician;
		this.procedureStepLocation = procedureStepLocation;
		this.preMedication = preMedication;
		this.specialNeeds = specialNeeds;
	}
	
	@Override
	public Integer getId() {
		return 0;
	}
	
	@Override
	public void setId(Integer integer) {
		
	}
	
	public Integer getWorklistID() {
		return worklistID;
	}
	
	public void setWorklistID(Integer worklistID) {
		this.worklistID = worklistID;
	}
	
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	public OrthancConfiguration getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
	
	public DicomStudy getStudy() {
		return study;
	}
	
	public void setStudy(DicomStudy dicomStudy) {
		this.study = dicomStudy;
	}
	
	public Patient getMrsPatient() {
		return mrsPatient;
	}
	
	public void setMrsPatient(Patient mrsPatient) {
		this.mrsPatient = mrsPatient;
	}
	
	public String getReferringPhysician() {
		return referringPhysician;
	}
	
	public void setReferringPhysician(String referringPhysician) {
		this.referringPhysician = referringPhysician;
	}
	
	public String getStudyDescription() {
		return studyDescription;
	}
	
	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
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
	
	public String getModality() {
		return modality;
	}
	
	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public String getReqProcedureID() {
		return reqProcedureID;
	}
	
	public void setReqProcedureID(String reqProcedureID) {
		this.reqProcedureID = reqProcedureID;
	}
	
	public String getReqProcedureDescription() {
		return reqProcedureDescription;
	}
	
	public void setReqProcedureDescription(String reqProcedureDescription) {
		this.reqProcedureDescription = reqProcedureDescription;
	}
	
	public String getStationAETitle() {
		return stationAETitle;
	}
	
	public void setStationAETitle(String stationAETitle) {
		this.stationAETitle = stationAETitle;
	}
	
	public String getPerformingPhysician() {
		return performingPhysician;
	}
	
	public void setPerformingPhysician(String performingPhysician) {
		performingPhysician = performingPhysician;
	}
	
	public String getProcedureStepLocation() {
		return procedureStepLocation;
	}
	
	public void setProcedureStepLocation(String procedureStepLocation) {
		procedureStepLocation = procedureStepLocation;
	}
	
	public String getPreMedication() {
		return preMedication;
	}
	
	public void setPreMedication(String preMedication) {
		this.preMedication = preMedication;
	}
	
	public String getSpecialNeeds() {
		return specialNeeds;
	}
	
	public void setSpecialNeeds(String specialNeeds) {
		this.specialNeeds = specialNeeds;
	}
}
