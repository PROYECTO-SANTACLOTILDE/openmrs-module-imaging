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
	
	public OrthancConfiguration getOrthancConfiguration(int id) {
		return (OrthancConfiguration) getSession().createCriteria(OrthancConfiguration.class).add(Restrictions.eq("id", id))
		        .uniqueResult();
	}
	
	public OrthancConfiguration saveOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		getSession().saveOrUpdate(orthancConfiguration);
		return orthancConfiguration;
	}
	
	public void removeOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		getSession().delete(orthancConfiguration);
	}
}
