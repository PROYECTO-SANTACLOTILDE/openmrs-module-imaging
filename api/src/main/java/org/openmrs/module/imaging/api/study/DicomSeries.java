package org.openmrs.module.imaging.api.study;

import java.util.List;

public class DicomSeries {
	
	private String seriesInstanceUID;

	private int seriesNumber;

	private String modality;

	// Getters and Setters
	public String getSeriesInstanceUID() {
		return seriesInstanceUID;
	}
	
	public void setSeriesInstanceUID(String seriesInstanceUID) {
		this.seriesInstanceUID = seriesInstanceUID;
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
}
