package org.openmrs.module.imaging.api.study;

import java.util.List;

public class DicomSeries {
	
	private String seriesInstanceUID;
	
	private String seriesDescription;
	
	private int seriesNumber;
	
	private List<DicomInstance> instanceList;
	
	// Getters and Setters
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	
	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
	}
	
	public String getSeriesDescription() {
		return seriesDescription;
	}
	
	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}
	
	public int getSeriesNumber() {
		return seriesNumber;
	}
	
	public void setSeriesNumber(int seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	
	public List<DicomInstance> getInstanceList() {
		return instanceList;
	}
	
	public void setInstanceList(List<DicomInstance> instanceList) {
		this.instanceList = instanceList;
	}
}
