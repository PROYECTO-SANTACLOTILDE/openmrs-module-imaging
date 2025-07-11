package org.openmrs.module.imaging.api;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.Patient;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DicomStudyServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final String DICOMSTUDY_TEST_DATASET = "testDicomStudyDataset.xml";
	
	private DicomStudyService dicomStudyService;
	
	@Before
	public void runBeforeAllTests() throws Exception {
		if (dicomStudyService == null) {
			dicomStudyService = Context.getService(DicomStudyService.class);
		}
		executeDataSet(DICOMSTUDY_TEST_DATASET);
	}
	
	@Test
	public void getStudiesOfPatient_shouldGetAllStudyByPatient() throws Exception {
		Patient patient = Context.getPatientService().getPatient(1);
		List<DicomStudy> studies = dicomStudyService.getStudiesOfPatient(patient);
		assertEquals(patient, studies.get(0).getMrsPatient());
		assertEquals(1, studies.size());
		
		// get a patient without dicom study
		patient = Context.getPatientService().getPatient(2);
		studies = dicomStudyService.getStudiesOfPatient(patient);
		assertEquals(0, studies.size());
	}
	
}
