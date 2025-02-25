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

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Database methods for {@link OrthancConfiguration}.
 */
@Repository("imaging.OrthancConfigurationDao")
public class OrthancConfigurationDao {
	
	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrthancConfiguration> getAllOrthancConfigurations() {
		return getSession().createCriteria(OrthancConfiguration.class).list();
	}
	
	/**
	 * @param id the configuration ID
	 */
	public OrthancConfiguration getOrthancConfiguration(int id) {
		return (OrthancConfiguration) getSession().createCriteria(OrthancConfiguration.class).add(Restrictions.eq("id", id))
		        .uniqueResult();
	}
	
	/**
	 * @ return Orthanc configuration
	 */
	public OrthancConfiguration saveOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		if (!getSession().createCriteria(OrthancConfiguration.class)
		        .add(Restrictions.eq("orthancBaseUrl", orthancConfiguration.getOrthancBaseUrl())).list().isEmpty()) {
			throw new IllegalArgumentException("A configuration with the same base URL already exists");
		}
		getSession().saveOrUpdate(orthancConfiguration);
		return orthancConfiguration;
	}
	
	/**
	 * @param orthancConfiguration: Orthanc Configuration
	 */
	public void removeOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		getSession().delete(orthancConfiguration);
	}
	
	public OrthancConfiguration updateOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		getSession().update(orthancConfiguration);
		return orthancConfiguration;
	}
}
