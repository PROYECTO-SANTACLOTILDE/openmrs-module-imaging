package org.openmrs.module.imaging.api.study;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.imaging.OrthancConfiguration;

public class DicomSeries extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private String seriesInstanceUID;
	
	private String orthancSeriesUID;
	
	private OrthancConfiguration orthancConfiguration;
	
	private String seriesDescription;
	
	private String seriesNumber;
	
	private String seriesDate;
	
	private String seriesTime;
	
	private String modality;
	
	public DicomSeries() {
	}
	
	public DicomSeries(String seriesInstanceUID, String orthancSeriesUID, OrthancConfiguration config,
	    String seriesDescription, String seriesNumber, String modality, String seriesDate, String seriesTime) {
		this.seriesInstanceUID = seriesInstanceUID;
		this.seriesDescription = seriesDescription;
		this.orthancSeriesUID = orthancSeriesUID;
		this.orthancConfiguration = config;
		this.seriesNumber = seriesNumber;
		this.modality = modality;
		this.seriesDate = seriesDate;
		this.seriesTime = seriesTime;
	}
	
	// Getters and Setters
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	
	public void setSeriesInstanceUID(String seriesOrthancUID) {
		this.seriesInstanceUID = seriesOrthancUID;
	}
	
	public String getSeriesNumber() {
		return seriesNumber;
	}
	
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	
	public String getModality() {
		return modality;
	}
	
	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public String getOrthancSeriesUID() {
		return orthancSeriesUID;
	}
	
	public void setOrthancSeriesUID(String orthancSeriesUID) {
		this.orthancSeriesUID = orthancSeriesUID;
	}
	
	public String getSeriesDescription() {
		return seriesDescription;
	}
	
	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}
	
	public String getSeriesDate() {
		return seriesDate;
	}
	
	public void setSeriesDate(String seriesDate) {
		this.seriesDate = seriesDate;
	}
	
	@Override
	public Integer getId() {
		return 0;
	}
	
	@Override
	public void setId(Integer integer) {
		
	}
	
	public String getSeriesTime() {
		return seriesTime;
	}
	
	public void setSeriesTime(String seriesTime) {
		this.seriesTime = seriesTime;
	}
	
	public OrthancConfiguration getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
}
