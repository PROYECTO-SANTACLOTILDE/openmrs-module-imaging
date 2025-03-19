package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.api.RequestProcedureService;
import org.openmrs.module.imaging.api.dao.RequestProcedureDao;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class RequestProcedureServiceImpl extends BaseOpenmrsService implements RequestProcedureService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private RequestProcedureDao dao;
	
	public void setDao(RequestProcedureDao dao) {
		this.dao = dao;
	}
	
	public RequestProcedureDao getDao() {
		return dao;
	}
	
	@Override
	public List<RequestProcedure> getRequestProcedureByPatient(Patient pt) {
		return dao.getByPatient(pt);
	}
	
	@Override
	public RequestProcedure getRequestProcedure(int requestProcedureId) {
		return dao.get(requestProcedureId);
	}
	
	@Override
	public void deleteRequestProcedure(RequestProcedure requestProcedure) throws IOException {
		dao.remove(requestProcedure);
	}
	
	@Override
	public void newRequest(RequestProcedure requestProcedure) throws IOException {
		dao.save(requestProcedure);
	}
	
	@Override
	public void updateRequstStatus(RequestProcedure requestProcedure) {
		dao.update(requestProcedure);
	}
}
