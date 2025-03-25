package org.openmrs.module.imaging.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.module.imaging.api.worklist.RequestProcedureSteps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public interface RequestProcedureStepsService extends OpenmrsService {
	
	List<RequestProcedureSteps> getAllStepsByRequestProcedure(RequestProcedure requestProcedure);
	
	RequestProcedureSteps getProcedureSteps(int Id);
	
	void newProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException;
	
	void deleteProcedureSteps(RequestProcedureSteps requestProcedureSteps) throws IOException;
}
