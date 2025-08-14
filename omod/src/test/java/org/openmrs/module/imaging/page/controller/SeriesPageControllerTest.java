package org.openmrs.module.imaging.page.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.ImagingConstants;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SeriesPageControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private DicomStudyService dicomStudyService;
	
	private SeriesPageController controller;
	
	private OrthancConfigurationService orthancConfigurationService;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet("testDicomStudyDataset.xml");
		controller = (SeriesPageController) applicationContext.getBean("seriesPageController");
	}
	
	@Test
	public void testGet_shouldPopulateModelWithSeriesList() throws IOException {
		
		SeriesPageController controller = new SeriesPageController();
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);

		dicomStudyService = Context.getService(DicomStudyService.class);
		
		String jsonResponse = "[{\"MainDicomTags\": {\"SeriesInstanceUID\": \"testSeriesUID123\","
		        + "\"SeriesDescription\": \"Test Series\"," + "\"SeriesNumber\": \"1\"," + "\"Modality\": \"CT\","
		        + "\"SeriesDate\": \"20250717\"," + "\"SeriesTime\": \"123000\"}," + "\"ID\": \"abcd1\"}]";

		ClientConnectionPair mockPair = ClientConnectionPair.setupMockClientWithStatus(HttpURLConnection.HTTP_OK, "POST",
		    "/tools/find", "", config);

		InputStream responseStream = new ByteArrayInputStream(jsonResponse.getBytes(StandardCharsets.UTF_8));
		when(mockPair.getConnection().getInputStream()).thenReturn(responseStream);

		dicomStudyService.setHttpClient(mockPair.getClient());
		DicomStudy study = dicomStudyService.getDicomStudy(1);
		
		Model model = new PageModel();
		controller.get(model, study.getId());

		assertNotNull(model.getAttribute("serieses"));
		assertNotNull(model.getAttribute("studyId"));
		assertNotNull(model.getAttribute("studyInstanceUID"));
		assertNotNull(model.getAttribute("privilegeModifyImageData"));


		// Verify serieses list has exactly 1 item
		Object seriesObj = model.getAttribute("serieses");
		assertTrue(seriesObj instanceof List<?>);
		List<?> seriesList = (List<?>) seriesObj;
		assertEquals(1, seriesList.size());

		Object firstSeries = seriesList.get(0);
		assertNotNull(firstSeries);
		if (firstSeries instanceof DicomSeries) {
			DicomSeries series = (DicomSeries) firstSeries;
			assertEquals("testSeriesUID123", series.getSeriesInstanceUID());
		}
	}
}
