package org.openmrs.module.imaging.api;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DicomStudyService extends OpenmrsService {
	
	List<DicomStudy> getAllStudiesByPatient(Patient pt);
	
	void setStudies(Patient pt, Studies retrievedStudies);
	
	void deleteStudy(DicomStudy dicomStudy);
	
	List<DicomSeries> getSerieses(String studyInstanceUID);
	
	List<DicomInstance> getInstances(String sopInstanceUID);
	
}
