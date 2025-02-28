/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.imaging.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.module.imaging.api.worklist.WorkTask;
import org.openmrs.module.imaging.api.worklist.WorkList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("imaging.WorkItemDao")
public class WorkItemDao {
	
	private static final Logger log = LoggerFactory.getLogger(WorkItemDao.class);
	
	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public WorkList get(int id) {
		return (WorkList) getSession().get(WorkList.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkTask> getAll() {
		return getSession().createCriteria(WorkList.class).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkTask> getByPatient(Patient patient) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DicomStudy.class);
		return criteria.add(Restrictions.eq("mrsPatient", patient)).list();
	}
	
	public void save(WorkTask item) {
		getSession().saveOrUpdate(item);
	}
	
	public void remove(WorkTask item) {
		getSession().delete(item);
	}
	
	public boolean hasWorkItem(String pathToFolder) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(WorkTask.class);
		return !criteria.add(Restrictions.eq("pathToFolder", pathToFolder)).list().isEmpty();
	}
}
