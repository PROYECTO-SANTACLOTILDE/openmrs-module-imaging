package org.openmrs.module.imaging.api;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public interface RequestProcedureService extends OpenmrsService {
	
	List<RequestProcedure> getRequestProcedureByPatient(Patient pt);
	
	RequestProcedure getRequestProcedure(int requestProcedureId);
	
	void deleteRequestProcedure(RequestProcedure requestProcedure) throws IOException;
	
	void newRequest(RequestProcedure requestProcedure) throws IOException;
	
	void updateRequstStatus(RequestProcedure requestProcedure);
}
