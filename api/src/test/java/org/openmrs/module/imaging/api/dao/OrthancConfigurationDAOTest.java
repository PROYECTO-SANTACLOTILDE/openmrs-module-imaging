package org.openmrs.module.imaging.api.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests methods in {@link OrthancConfigurationDao}
 */
public class OrthancConfigurationDAOTest extends BaseModuleContextSensitiveTest {
	
	private OrthancConfigurationDao orthancConfigurationDao;
	
	private static final String ORTHANC_CONFIGURATION_TEST_DATASET = "testOrthancConfigurationDataset.xml";
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(ORTHANC_CONFIGURATION_TEST_DATASET);
		assertNotNull(applicationContext);
		orthancConfigurationDao = (OrthancConfigurationDao) applicationContext.getBean("imaging.OrthancConfigurationDao");
	}
	
	@Test
	public void testGetAll_shouldReturnAllConfigurations() {
		List<OrthancConfiguration> configs = orthancConfigurationDao.getAll();
		assertEquals(2, configs.size());
		assertFalse(configs.isEmpty());
	}
	
	@Test
	public void testGetById_shouldReturnCorrectConfiguration() {
		OrthancConfiguration config = orthancConfigurationDao.get(1);
		assertNotNull(config);
		assertEquals("http://localhost:8052", config.getOrthancBaseUrl());
	}

	@Test
	public void testSaveNew_shouldPersistNewConfiguration() {
		OrthancConfiguration config = new OrthancConfiguration();
		config.setOrthancBaseUrl("http://localhost:8072");
		config.setOrthancUsername("orthanc");
		config.setOrthancPassword("orthanc");

		orthancConfigurationDao.saveNew(config);
		List<OrthancConfiguration> all = orthancConfigurationDao.getAll();
		assertEquals(3, all.size());
	}

	@Test
	public void testSaveNew_whenDuplicateExists_shouldThrowException() {
		OrthancConfiguration config = new OrthancConfiguration();
		config.setOrthancBaseUrl("http://localhost:8052"); // already in XML data
		config.setOrthancUsername("orthanc");
		config.setOrthancPassword("orthanc");

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> orthancConfigurationDao.saveNew(config));
		assertEquals("A configuration with the same base URL already exists", ex.getMessage());

		// Verify it was saved
		List<OrthancConfiguration> all = orthancConfigurationDao.getAll();
		assertEquals(3, all.size());

		// Check HTTP connection to Orthanc
		assertTrue(isOrthancReachable(config.getOrthancBaseUrl()));
	}

	@Test
	public void testUpdateExisting_shouldModifyConfiguration() {
		OrthancConfiguration config = orthancConfigurationDao.get(1);
		config.setOrthancUsername("testedUser");
		config.setOrthancPassword("testedUser");

		orthancConfigurationDao.updateExisting(config);
		OrthancConfiguration updated = orthancConfigurationDao.get(1);
		assertEquals("testedUser", updated.getOrthancUsername());
	}

	@Test
	public void testRemove_shouldDeleteConfiguration() {
		OrthancConfiguration config = orthancConfigurationDao.get(2);
		orthancConfigurationDao.remove(config);

		assertNull(orthancConfigurationDao.get(2));
		assertEquals(1, orthancConfigurationDao.getAll().size());
	}

	private boolean isOrthancReachable(String baseUrl) {
		try {
			URL url = new URL(baseUrl + "/system"); // `/system` is a common endpoint in Orthanc
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000); // 3 seconds timeout
			connection.setReadTimeout(3000);
			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		} catch (IOException e) {
			return false;
		}
	}
}
