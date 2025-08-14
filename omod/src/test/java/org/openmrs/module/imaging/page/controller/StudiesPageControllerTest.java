package org.openmrs.module.imaging.page.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.io.IOException;

import static org.junit.Assert.*;

public class StudiesPageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private StudiesPageController controller;
	
	private Patient testPatient;
	
	private DicomStudyService dicomStudyService;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet("testDicomStudyDataset.xml");
		testPatient = Context.getPatientService().getPatient(1);
		
		Context.getAdministrationService().setGlobalProperty(ImagingConstants.GP_MAX_UPLOAD_IMAGEDATA_SIZE, "200000000");
		controller = (StudiesPageController) applicationContext.getBean("studiesPageController");
	}
	
	@Test
	public void testGet_shouldPopulateModelOnGet() {
		Model model = new PageModel();
		controller.get(model, testPatient);
		
		assertNotNull(model.getAttribute("studies"));
		assertNotNull(model.getAttribute("orthancConfigurations"));
		assertTrue((Boolean) model.getAttribute("privilegeModifyImageData"));
		assertNotNull(model.getAttribute("maxUploadImageDataSize"));
	}
	
	@Test
    public void testHandleMaxSizeException_shouldRedirectWithMessage() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(200_000_000);

        String redirectUrl = controller.handleMaxSizeException(ex, redirectAttributes, testPatient);

        assertEquals("redirect:/imaging/studies.page", redirectUrl);
        assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
        assertEquals("File size exceeds maximum upload limit. Please upload a smaller file.", redirectAttributes.getAttribute("message"));
    }
	
	@Test
	public void testUploadStudy_shouldUploadFilesAndRedirect() throws Exception {
		
		OrthancConfigurationService orthancService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancService.getOrthancConfiguration(1);
		
		MultipartFile file = new org.springframework.mock.web.MockMultipartFile("file", "dummy.dcm", "application/dicom",
		        "dummy data".getBytes());
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		// Todo: Add more detailed tests. Currently only verifying that the function is called.
		String redirectUrl = controller.uploadStudy(redirectAttributes, null, config.getId(), new MultipartFile[] { file },
		    testPatient);
		assertEquals("redirect:/imaging/studies.page", redirectUrl);
		assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
		assertNotNull(redirectAttributes.getAttribute("message"));
	}
	
	@Test
	public void syncStudy_allStudiesWithNoConfig_shouldFetchAll() throws IOException {
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		String view = controller.syncStudy(redirectAttributes, -1, "all", testPatient);
		
		// Assert redirect URL
		assertEquals("redirect:/imaging/syncStudies.page", view);
		assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
		assertEquals("Not all studies could be downloaded successfully. The server might be unavailable.",
		    redirectAttributes.getAttribute("message"));
	}
	
	@Test
	public void syncStudy_mixedServers_shouldHandleBothCases() {
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		
		// --- Case 1: Valid server (8052) ---
		String viewValid = controller.syncStudy(redirectAttributes, 1, "all", testPatient);
		
		assertEquals("redirect:/imaging/syncStudies.page", viewValid);
		assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
		assertEquals("Studies successfully fetched", redirectAttributes.getAttribute("message"));
		
		// --- Case 2: Invalid server (8062) ---
		redirectAttributes = new RedirectAttributesModelMap(); // resets
		String viewInvalid = controller.syncStudy(redirectAttributes, 2, "all", testPatient);
		
		assertEquals("redirect:/imaging/syncStudies.page", viewInvalid);
		assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
		assertEquals("Not all studies could be downloaded successfully. The server might be unavailable.",
		    redirectAttributes.getAttribute("message"));
	}
	
	@Test
	public void deleteStudy_withPrivilege_shouldDeny() {
		User user = Context.getUserService().getUser(1);
		assertNotNull(user);
		
		boolean hasPrivilege = user.hasPrivilege(ImagingConstants.PRIVILEGE_MODIFY_IMAGE_DATA);
		assertTrue(hasPrivilege);
		
		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
		dicomStudyService = Context.getService(DicomStudyService.class);
		DicomStudy study = dicomStudyService.getDicomStudy(1);
		
		String result = controller.deleteStudy(redirectAttributes, study.getId(), testPatient, "openmrs");
		
		assertEquals("redirect:/imaging/studies.page", result);
		assertEquals("Study successfully deleted", redirectAttributes.getAttribute("message"));
		assertEquals(testPatient.getId().toString(), redirectAttributes.getAttribute("patientId"));
	}
	
	//	@Test
	//	public void deleteStudy_withoutPrivilege_shouldDeny() throws Exception {
	// TODO: Need to find method how to update super user.
	//		User user = Context.getUserService().getUser(3);
	//		assertNotNull(user);
	
	//		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
	//
	//		// Get a study and patient from your service
	//		dicomStudyService = Context.getService(DicomStudyService.class);
	//		DicomStudy study = dicomStudyService.getDicomStudy(2); // study ID from your dataset
	//
	//		// Call the method
	//		String result = controller.deleteStudy(redirectAttributes, study.getId(), testPatient, "openmrs");
	//
	//		// Verify redirect and message
	//		assertEquals("redirect:/imaging/studies.page", result);
	//		assertEquals("Permission denied (you don't have the necessary privileges)",
	//		    redirectAttributes.getAttribute("message"));
	//		assertEquals(testPatient.getId(), redirectAttributes.getAttribute("patientId"));
	//	}
}
