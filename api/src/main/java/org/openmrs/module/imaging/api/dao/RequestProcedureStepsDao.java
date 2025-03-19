package org.openmrs.module.imaging.api.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("imaging.RequestProcedureStepsDao")
public class RequestProcedureStepsDao {
	
	private static final Logger log = LoggerFactory.getLogger(RequestProcedureStepsDao.class);
	
	//	@Autowired
	DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public RequestProcedureSteps get(int id) {
		return (RequestProcedureSteps) getSession().get(RequestProcedureSteps.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<RequestProcedureSteps> getAll() {
		return getSession().createCriteria(RequestProcedureSteps.class).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<RequestProcedureSteps> getAllStepsByRequestProcedure(RequestProcedure requestProcedure) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RequestProcedureSteps.class);
		return criteria.add(Restrictions.eq("requestProcedure", requestProcedure)).list();
	}
	
	public void save(RequestProcedureSteps requestProcedureSteps) {
		getSession().saveOrUpdate(requestProcedureSteps);
	}
	
	public void remove(RequestProcedureSteps requestProcedureSteps) {
		getSession().delete(requestProcedureSteps);
	}
}
