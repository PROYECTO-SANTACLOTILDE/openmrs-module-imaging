package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.dao.OrthancConfigurationDao;

import java.util.List;

public class OrthancConfigurationServiceImpl extends BaseOpenmrsService implements OrthancConfigurationService {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private OrthancConfigurationDao dao;
	
	public void setDao(OrthancConfigurationDao dao) {
		this.dao = dao;
	}
	
	public OrthancConfigurationDao getDao() {
		return dao;
	}
	
	@Override
	public List<OrthancConfiguration> getAllOrthancConfigurations() {
		return dao.getAllOrthancConfigurations();
	}
	
	@Override
	public OrthancConfiguration getOrthancConfiguration(int id) {
		return dao.getOrthancConfiguration(id);
	}
	
	@Override
	public OrthancConfiguration saveOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		return dao.saveOrthancConfiguration(orthancConfiguration);
	}
	
	@Override
	public void removeOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		dao.removeOrthancConfiguration(orthancConfiguration);
	}
}
