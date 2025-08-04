package org.openmrs.module.imaging.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.module.imaging.api.client.OrthancHttpClient;
import org.openmrs.module.imaging.api.impl.RequestProcedureServiceImpl;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.module.imaging.api.worklist.RequestProcedure;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.client.OrthancHttpClient;
import org.openmrs.test.BaseModuleContextSensitiveTest;


import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RequestProcedureServiceTest extends BaseModuleContextSensitiveTest {

    private static final String REQUEST_PROCEDURE_DATASET = "testRequestProcedureDataset.xml";

    private RequestProcedureService requestProcedureService;

    private OrthancConfigurationService orthancConfigurationService;

    @Mock
    private OrthancHttpClient mockHttpClient;

    @Before
    public void setUp() throws Exception {
        if (requestProcedureService == null) {
            requestProcedureService = Context.getService(RequestProcedureService.class);
        }
        executeDataSet(REQUEST_PROCEDURE_DATASET);
    }

    @Test
    public void getAllRequestProcedures_shouldReturnAllProceduresFromDatabase() {
        List<RequestProcedure> requestProcedureList = requestProcedureService.getAllRequestProcedures();

        assertNotNull(requestProcedureList);
        assertEquals(3, requestProcedureList.size());
    }

    @Test
    public void getRequestProcedureByPatient_shouldReturnProceduresForPatient() {
        Patient patient = Context.getPatientService().getPatient(1);

        List<RequestProcedure> requestProcedureList = requestProcedureService.getRequestProcedureByPatient(patient);
        assertNotNull(requestProcedureList);
        assertEquals(3, requestProcedureList.size());
    }

    @Test
    public void getRequestProcedureByPatient_shouldReturnEmptyListIfNoProcedures() {
        Patient patient = Context.getPatientService().getPatient(7); // patient with no entries
        List<RequestProcedure> results = requestProcedureService.getRequestProcedureByPatient(patient);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void getAllByStudyInstanceUID_shouldReturnProceduresForStudy() {
        DicomStudyService studyService = Context.getService(DicomStudyService.class);
        DicomStudy study = studyService.getDicomStudy(1);
        assertNotNull(study);
        assertEquals("testInstanceUID888", study.getStudyInstanceUID());

        List<RequestProcedure> procedureList = requestProcedureService.getAllByStudyInstanceUID(study.getStudyInstanceUID());
        assertNotNull(procedureList);
        assertEquals("ACC1001", procedureList.get(0).getAccessionNumber());
        assertEquals("complete", procedureList.get(0).getStatus());
        assertEquals("testInstanceUID888", procedureList.get(0).getStudyInstanceUID());
    }


}
