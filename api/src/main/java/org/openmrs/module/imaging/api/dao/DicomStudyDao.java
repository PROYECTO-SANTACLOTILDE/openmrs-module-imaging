package org.openmrs.module.imaging.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("imaging.DicomStudyDao")
public class DicomStudyDao {
	
	private static final Logger log = LoggerFactory.getLogger(DicomStudyDao.class);
	
	private List<DicomStudy> createTestDicomStudies(Patient patient, OrthancConfiguration config) {
		
		List<DicomStudy> list = new ArrayList<>();
			list.add(new DicomStudy(
				"1.2.840.113745.101000.1008000.38179.6792.6324567",
				"6b9e19d9-62094390-5f9ddb01-4a191ae7-9766b715",
				patient,
				new OrthancConfiguration("http://localhost:8042/", "test", "test"),
				"John Doe",
				new Date(1672531200000L), // Example date: Jan 1, 2023
				"Thorax^1WB_PETCT",
				"M"));

		list.add(new DicomStudy(
				"1.2.840.113619.2.5.1762383673.29411.978943026.2",
				"6b9e19d9-62094390-5f9ddb01-4a191ae7-9766b716",
				patient,
				new OrthancConfiguration("remote", "Orthanc2", "test"),
				"Jane Smith",
				new Date(1688112000000L), // Example date: July 1, 2023
				"Abdominal CT",
				"F"
				)
		);
		return list;
	}
	
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
		criteria.add(Restrictions.eq("patientName", patient.getGivenName()));
		
		return createTestDicomStudies(patient, null);
		//		return criteria.list();
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
