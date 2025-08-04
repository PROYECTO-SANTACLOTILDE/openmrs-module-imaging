package org.openmrs.module.imaging.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.client.OrthancHttpClient;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrthancConfigurationServiceTest extends BaseModuleContextSensitiveTest {

	private OrthancConfigurationService orthancConfigurationService;

	@Mock
	private OrthancHttpClient mockHttpClient;

	private static final String ORTHANC_CONFIGURATION_TEST_DATASET = "testOrthancConfigurationDataset.xml";

	@Before
	public void setUp() throws Exception {
		if (orthancConfigurationService == null) {
			orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		}
		orthancConfigurationService.setHttpClient(mockHttpClient);
		executeDataSet(ORTHANC_CONFIGURATION_TEST_DATASET);
	}

	@Test
	public void getAllOrthancConfigurations_shouldReturnFromDatabase() {
		OrthancConfigurationService service = Context.getService(OrthancConfigurationService.class);
		List<OrthancConfiguration> configs = service.getAllOrthancConfigurations();
		assertNotNull(configs);
		assertFalse(configs.isEmpty());
		assertEquals(1, configs.size());
		assertEquals("http://localhost:8072", configs.get(0).getOrthancBaseUrl());
	}
	
	@Test
	public void getOrthancConfigurationByID_shouldReturnSingleConfig() {
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(1);
		assertNotNull(config);
		assertEquals(Integer.valueOf(1), config.getId());
	}

	@Test
    public void saveOrthancConfiguration_shouldSaveWhenReachable() {
        OrthancConfiguration config = new OrthancConfiguration();
        config.setId(1);
        config.setOrthancBaseUrl("http://localhost:8052");
        config.setOrthancUsername("testUser");
        config.setOrthancPassword("testPassword");
        config.setOrthancProxyUrl("");

        when(mockHttpClient.isOrthancReachable(config)).thenReturn(true);

        orthancConfigurationService.saveOrthancConfiguration(config);
        List<OrthancConfiguration> configs = orthancConfigurationService.getAllOrthancConfigurations();
        assertTrue(configs.stream().anyMatch(c -> "http://localhost:8052".equals(c.getOrthancBaseUrl())));
    }

	@Test(expected = IllegalArgumentException.class)
	public void saveOrthancConfiguration_shouldThrowWhenNotReachable() {
		OrthancConfiguration config = new OrthancConfiguration();
		config.setOrthancBaseUrl("http://localhost:8062");
		config.setOrthancUsername("errorUser");
		config.setOrthancPassword("errorUser");
		config.setOrthancProxyUrl("");

		when(mockHttpClient.isOrthancReachable(config)).thenReturn(false);
		orthancConfigurationService.saveOrthancConfiguration(config);
	}
	
}
