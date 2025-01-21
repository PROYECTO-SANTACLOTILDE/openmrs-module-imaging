package org.openmrs.module.imaging.api;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Transactional
public interface DicomStudyService extends OpenmrsService {
	
	List<DicomStudy> getStudies(Patient pt);
	
	DicomStudy getDicomStudy(String studyInstanceUID);
	
	int testOrthancConnection(String url, String username, String password) throws IOException;
	
	void fetchStudies() throws IOException;
	
	void setStudies(Patient pt, Studies retrievedStudies);
	
	void deleteStudy(DicomStudy dicomStudy);
	
	List<DicomSeries> fetchSeries(String studyInstanceUID) throws IOException;
	
	List<DicomInstance> fetchInstances(String sopInstanceUID) throws IOException;
	
}
