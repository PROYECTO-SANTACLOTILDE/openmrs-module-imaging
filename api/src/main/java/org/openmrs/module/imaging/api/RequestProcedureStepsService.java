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
