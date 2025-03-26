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
package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.api.RequestProcedureStepsService;
import org.openmrs.module.imaging.api.dao.RequestProcedureStepsDao;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureSteps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service("requestProcedureStepsServiceImpl")
@Transactional
public class RequestProcedureStepsServiceImpl extends BaseOpenmrsService implements RequestProcedureStepsService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RequestProcedureStepsDao dao;

	/**
	 * @param dao the dao to set
	 */
	public void setDao(RequestProcedureStepsDao dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public RequestProcedureStepsDao getDao() {
		return dao;
	}

	/**
	 * @param requestProcedure The request procedure object
	 * */
	@Override
	public List<RequestProcedureSteps> getAllStepsByRequestProcedure(RequestProcedure requestProcedure) {
		return dao.getAllStepsByRequestProcedure(requestProcedure);
	}

	/**
	 * @param id The procedure steps ID
	 * */
	@Override
	public RequestProcedureSteps getProcedureSteps(int id) {
		return dao.get(id);
	}

	/**
	 * @param requestProcedureSteps The request procedure steps object
	 * */
	@Override
	public void newProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException {
		dao.save(requestProcedureSteps);
	}

	/**
	 * @param requestProcedureSteps The request procedure steps object
	 * */
	@Override
	public void deleteProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException {
		dao.remove(requestProcedureSteps);
	}
	
}
