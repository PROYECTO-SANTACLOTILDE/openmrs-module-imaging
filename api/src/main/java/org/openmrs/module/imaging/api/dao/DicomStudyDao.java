package org.openmrs.module.imaging.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("imaging.DicomStudyDao")
public class DicomStudyDao {

    @Autowired
    DbSessionFactory sessionFactory;

    private DbSession getSession() { return sessionFactory.getCurrentSession(); }

    public void setSessionFactory(DbSessionFactory sessionFactory) { this.sessionFactory = sessionFactory; }

    @SuppressWarnings("unchecked")
    public Studies getAllDicomStudies() {
        return (Studies) getSession().createCriteria(DicomStudy.class).list();
    }

    @SuppressWarnings("unchecked")
    public Studies getAllDicomStudiesByPatient(Patient patient) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DicomStudy.class);
        criteria.add(Restrictions.eq("mrsPatient", patient));
        return (Studies) criteria.list();
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
