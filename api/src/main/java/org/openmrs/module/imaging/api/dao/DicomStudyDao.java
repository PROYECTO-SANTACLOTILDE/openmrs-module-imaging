package org.openmrs.module.imaging.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("imaging.DicomStudyDao")
public class DicomStudyDao {
	
	private static final Logger log = LoggerFactory.getLogger(DicomStudyDao.class);
	
	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<DicomStudy> getAllDicomStudies() {
		return getSession().createCriteria(DicomStudy.class).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DicomStudy> getAllDicomStudiesByPatient(Patient patient) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DicomStudy.class);
		return criteria.add(Restrictions.eq("mrsPatient", patient)).list();
	}
	
	public DicomStudy getDicomStudy(String studyInstanceUID) {
		return (DicomStudy) getSession().createCriteria(DicomStudy.class)
		        .add(Restrictions.eq("studyInstanceUID", studyInstanceUID)).uniqueResult();
	}
	
	public DicomStudy saveDicomStudy(DicomStudy dicomStudy) {
		getSession().saveOrUpdate(dicomStudy);
		return dicomStudy;
	}
	
	public void removeDicomStudy(DicomStudy dicomStudy) {
		getSession().delete(dicomStudy);
	}
}
