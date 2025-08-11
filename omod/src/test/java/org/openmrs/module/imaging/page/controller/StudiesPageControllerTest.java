package org.openmrs.module.imaging.page.controller;

import static org.mockito.Mockito.*;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.*;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.module.imaging.ImagingProperties;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class StudiesPageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@InjectMocks
	private StudiesPageController controller;
	
	@Mock
	private PageModel model;
	
	@Mock
	private HttpServletResponse response;
	
	@Mock
	private MultipartFile multipartFile1;
	
	@Mock
	private MultipartFile multipartFile2;
	
	@Mock
	private DicomStudyService dicomStudyService;
	
	@Mock
	private OrthancConfigurationService orthancConfigurationService;
	
	@Mock
	private OrthancConfiguration orthancConfiguration;
	
	@Mock
	private DicomStudy dicomStudy;
	
	Patient patient;
	
	@Before
	public void setUp() {
		executeDataSet("testDicomStudyDataset.xml");
		patient = Context.getPatientService().getPatient(1);
		when(orthancConfigurationService.getAllOrthancConfigurations()).thenReturn(
		    Collections.singletonList(orthancConfiguration));
		when(dicomStudyService.getStudiesOfPatient(patient)).thenReturn(Collections.singletonList(dicomStudy));
	}
	
	@Test
	public void get_shouldAddAttributesToModel() {
		controller.get(model, patient);
		verify(model).addAttribute(eq("studies"), anyList());
		verify(model).addAttribute(eq("orthancConfigurations"), anyList());
		verify(model).addAttribute(eq("privilegeModifyImageData"), anyBoolean());
		verify(model).addAttribute(eq("maxUploadImageDataSize"), eq(10L));
	}
}
