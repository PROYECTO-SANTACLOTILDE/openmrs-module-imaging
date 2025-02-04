package org.openmrs.module.imaging.api;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Transactional
public interface DicomStudyService extends OpenmrsService {
	
	List<DicomStudy> getStudies(Patient pt);
	
	List<DicomStudy> getAllStudies();
	
	DicomStudy getDicomStudy(String studyInstanceUID);
	
	int testOrthancConnection(String url, String username, String password) throws IOException;
	
	boolean hasStudy(OrthancConfiguration orthancConfiguration);
	
	void fetchAllStudies() throws IOException;
	
	void fetchAllStudies(OrthancConfiguration orthancConfiguration) throws IOException;
	
	void fetchNewChangedStudies() throws IOException;
	
	void fetchNewChangedStudies(OrthancConfiguration orthancConfiguration) throws IOException;
	
	int uploadFile(OrthancConfiguration config, InputStream is) throws IOException;
	
	void setPatient(DicomStudy study, Patient patient);
	
	void deleteStudy(DicomStudy dicomStudy);
	
	int deleteSeries(String seriesOrthancUID, DicomStudy study);
	
	List<DicomSeries> fetchSeries(String studyInstanceUID) throws IOException;
	
	List<DicomInstance> fetchInstances(String sopInstanceUID) throws IOException;
	
	public class PreviewResult {
		
		public byte[] data;
		
		public String contentType;
	}
	
	PreviewResult fetchInstancePreview(String orthancInstanceUID, DicomStudy study) throws IOException;
}
