package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.dao.DicomStudyDao;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DicomStudyServiceImpl extends BaseOpenmrsService implements DicomStudyService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DicomStudyDao dao;
	
	public void setDao(DicomStudyDao dao) {
		this.dao = dao;
	}
	
	public DicomStudyDao getDao() {
		return dao;
	}
	
	@Override
	@Transactional
	public Studies getAllStudiesByPatient(Patient pt) {
		return (Studies) dao.getAllDicomStudiesByPatient(pt);
	}
	
	@Override
	public void setStudies(Patient pt, Studies retrievedStudies) {
	}
	
	@Override
	public void deleteStudy(DicomStudy dicomStudy) {
		dao.removeDicomStudy(dicomStudy);
	}
}
