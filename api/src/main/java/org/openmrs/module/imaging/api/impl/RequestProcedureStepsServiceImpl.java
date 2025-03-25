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
	
	public void setDao(RequestProcedureStepsDao dao) {
		this.dao = dao;
	}
	
	public RequestProcedureStepsDao getDao() {
		return dao;
	}
	
	@Override
	public List<RequestProcedureSteps> getAllStepsByRequestProcedure(RequestProcedure requestProcedure) {
		return dao.getAllStepsByRequestProcedure(requestProcedure);
	}
	
	@Override
	public RequestProcedureSteps getProcedureSteps(int id) {
		return dao.get(id);
	}
	
	@Override
	public void newProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException {
		dao.save(requestProcedureSteps);
	}
	
	@Override
	public void deleteProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException {
		dao.remove(requestProcedureSteps);
	}
	
}
