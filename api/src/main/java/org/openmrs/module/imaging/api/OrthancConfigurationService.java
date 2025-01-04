package org.openmrs.module.imaging.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface OrthancConfigurationService extends OpenmrsService {
	
	@Transactional(readOnly = true)
	List<OrthancConfiguration> getAllOrthancConfigurations();
	
	@Transactional(readOnly = true)
	OrthancConfiguration getOrthancConfiguration(Integer orthancId);
	
	OrthancConfiguration saveOrthancConfiguration(OrthancConfiguration orthancConfiguration);
	
	void removeOrthancConfiguration(OrthancConfiguration orthancConfiguration);
}
