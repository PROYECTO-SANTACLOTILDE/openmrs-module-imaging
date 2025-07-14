/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.dao.OrthancConfigurationDao;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
		return dao.getAll();
	}
	
	@Override
	public OrthancConfiguration getOrthancConfiguration(int id) {
		return dao.get(id);
	}
	
	@Override
	public void saveOrthancConfiguration(OrthancConfiguration config) {
		if (isOrthancReachable(config)) {
			dao.saveNew(config);
		} else {
			throw new IllegalArgumentException("The Orthanc instance is not reachable or credentials are invalid");
		}
	}
	
	@Override
	public void removeOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		dao.remove(orthancConfiguration);
	}
	
	@Override
	public void updateOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		dao.updateExisting(orthancConfiguration);
	}

	/**
	 *
	 * @param config
	 * @return
	 */
	private boolean isOrthancReachable (OrthancConfiguration config) {
		try {
			URL url = new URL(config.getOrthancBaseUrl() + "/system"); // `/system` is a common endpoint in Orthanc
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000); // 3 seconds timeout
			connection.setReadTimeout(3000);

			String auth = config.getOrthancUsername() + ":" + config.getOrthancPassword();
			String encodeAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
			connection.setRequestProperty("Authorization", "Basic " + encodeAuth);

			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		}
		catch (IOException e) {
			return false;
		}
	}
}
