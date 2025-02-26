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

package org.openmrs.module.imaging.api.worklist;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomStudy;

public class WorkItem extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer id;
	
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
	
	public WorkItem() {
	}
	
	public WorkItem(String accessionNumber, OrthancConfiguration orthancConfiguration, DicomStudy study, Patient mrPatient,
	    String referringPhysician, String studyDescription, String stepStartDate, String stepStartTime, String modality,
	    String reqProcedureID, String reqProcedureDescription, String stationAETitle, String performingPhysician,
	    String procedureStepLocation, String preMedication, String specialNeeds) {
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
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAccessionNumber() {
		return this.accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	public OrthancConfiguration getOrthancConfiguration() {
		return this.orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
	
	public DicomStudy getStudy() {
		return this.study;
	}
	
	public void setStudy(DicomStudy dicomStudy) {
		this.study = dicomStudy;
	}
	
	public Patient getMrsPatient() {
		return this.mrsPatient;
	}
	
	public void setMrsPatient(Patient mrsPatient) {
		this.mrsPatient = mrsPatient;
	}
	
	public String getReferringPhysician() {
		return this.referringPhysician;
	}
	
	public void setReferringPhysician(String referringPhysician) {
		this.referringPhysician = referringPhysician;
	}
	
	public String getStudyDescription() {
		return this.studyDescription;
	}
	
	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}
	
	public String getStepStartDate() {
		return this.stepStartDate;
	}
	
	public void setStepStartDate(String stepStartDate) {
		this.stepStartDate = stepStartDate;
	}
	
	public String getStepStartTime() {
		return this.stepStartTime;
	}
	
	public void setStepStartTime(String stepStartTime) {
		this.stepStartTime = stepStartTime;
	}
	
	public String getModality() {
		return this.modality;
	}
	
	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public String getReqProcedureID() {
		return this.reqProcedureID;
	}
	
	public void setReqProcedureID(String reqProcedureID) {
		this.reqProcedureID = reqProcedureID;
	}
	
	public String getReqProcedureDescription() {
		return this.reqProcedureDescription;
	}
	
	public void setReqProcedureDescription(String reqProcedureDescription) {
		this.reqProcedureDescription = reqProcedureDescription;
	}
	
	public String getStationAETitle() {
		return this.stationAETitle;
	}
	
	public void setStationAETitle(String stationAETitle) {
		this.stationAETitle = stationAETitle;
	}
	
	public String getPerformingPhysician() {
		return this.performingPhysician;
	}
	
	public void setPerformingPhysician(String performingPhysician) {
		this.performingPhysician = performingPhysician;
	}
	
	public String getProcedureStepLocation() {
		return this.procedureStepLocation;
	}
	
	public void setProcedureStepLocation(String procedureStepLocation) {
		this.procedureStepLocation = procedureStepLocation;
	}
	
	public String getPreMedication() {
		return this.preMedication;
	}
	
	public void setPreMedication(String preMedication) {
		this.preMedication = preMedication;
	}
	
	public String getSpecialNeeds() {
		return this.specialNeeds;
	}
	
	public void setSpecialNeeds(String specialNeeds) {
		this.specialNeeds = specialNeeds;
	}
}
