package org.openmrs.module.imaging;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrthancService {
	
	@Transactional(readOnly = true)
	@Authorized(ImagingConstants.VIEW_STUDIES)
	List<DicomStudy> getPatientStudies(String patientId);
	
	@Transactional(readOnly = true)
	@Authorized(ImagingConstants.VIEW_STUDIES)
	List<DicomSeries> getStudySeries(String studyId);
}
