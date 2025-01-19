package org.openmrs.module.imaging.api.study;

public class DicomSeries {
	
	private String seriesInstanceUID;
	
	private String orthancSeriesUID;
	
	private int seriesNumber;
	
	private String modality;
	
	public DicomSeries(String seriesInstanceUID, String orthancSeriesUID, int seriesNumber, String modality) {
		this.seriesInstanceUID = seriesInstanceUID;
		this.orthancSeriesUID = orthancSeriesUID;
		this.seriesNumber = seriesNumber;
		this.modality = modality;
	}
	
	// Getters and Setters
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	
	public void setSeriesInstanceUID(String seriesOrthancUID) {
		this.seriesInstanceUID = seriesOrthancUID;
	}
	
	public int getSeriesNumber() {
		return seriesNumber;
	}
	
	public void setSeriesNumber(int seriesNumber) {
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
}
