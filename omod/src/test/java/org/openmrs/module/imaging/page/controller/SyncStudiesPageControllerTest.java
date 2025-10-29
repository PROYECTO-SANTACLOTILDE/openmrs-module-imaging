package org.openmrs.module.imaging.page.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class SyncStudiesPageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private SyncStudiesPageController controller;
	
	private DicomStudyService dicomStudyService;
	
	private Patient patient;
	
	@Before
	public void setUp() {
		executeDataSet("testDicomStudyDataset.xml"); // include some test studies
		
		controller = (SyncStudiesPageController) applicationContext.getBean("syncStudiesPageController");
		dicomStudyService = Context.getService(DicomStudyService.class);
		
		patient = Context.getPatientService().getPatient(1);
	}
	
	@Test
	public void testGet_shouldPopulateModel() {
		Model model = new PageModel();
		controller.get(model, patient, "Test message");
		
		// Check that studies are added
		@SuppressWarnings("unchecked")
		List<DicomStudy> studies = (List<DicomStudy>) model.getAttribute("studies");
		assertNotNull(studies);
		assertFalse(studies.isEmpty());
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> match = (Map<String, Integer>) model.getAttribute("match");
		assertNotNull(match);
		assertEquals(studies.size(), match.size());
		
		Boolean hasPrivilege = (Boolean) model.getAttribute("privilegeModifyImageData");
		assertNotNull(hasPrivilege);
	}
	
	@Test
	public void testAssignStudy_shouldAssignAndRemoveStudy() {
		DicomStudy study = dicomStudyService.getAllStudies().get(0);
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		String redirect = controller.assignStudy(redirectAttributes, patient, study.getId(), true);
		assertEquals("redirect:/imaging/syncStudies.page", redirect);
		assertEquals(patient.getId().toString(), redirectAttributes.getAttribute("patientId"));
		assertEquals("Study assigned to patient", redirectAttributes.getAttribute("message"));
		assertEquals(patient, dicomStudyService.getDicomStudy(study.getId()).getMrsPatient());
		
		redirectAttributes = new RedirectAttributesModelMap();
		redirect = controller.assignStudy(redirectAttributes, patient, study.getId(), false);
		assertEquals("redirect:/imaging/syncStudies.page", redirect);
		assertEquals("Study assignment removed", redirectAttributes.getAttribute("message"));
		assertNull(dicomStudyService.getDicomStudy(study.getId()).getMrsPatient());
	}
}
