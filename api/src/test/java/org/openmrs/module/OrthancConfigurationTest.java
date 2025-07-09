package org.openmrs.module.imaging.api;

import org.junit.jupiter.api.Test;
import org.openmrs.module.imaging.OrthancConfiguration;

import static org.junit.jupiter.api.Assertions.*;

public class OrthancConfigurationTest {
	
	@Test
	public void testConstructorAndGetters() {
		
		OrthancConfiguration config = new OrthancConfiguration("http://localhost:8052", "http://proxy:8052", "admin",
		        "secret", 2);
		
		assertEquals("http://localhost:8052", config.getOrthancBaseUrl());
		assertEquals("http://proxy:8052", config.getOrthancProxyUrl());
		assertEquals("admin", config.getOrthancUsername());
		assertEquals("secret", config.getOrthancPassword());
		assertEquals(2, config.getLastChangedIndex());
	}
	
	@Test
	public void testSettersAndGetters() {
		
		OrthancConfiguration config = new OrthancConfiguration();
		config.setOrthancBaseUrl("http://localhost:8062");
		config.setOrthancProxyUrl("http://proxy:8062");
		config.setOrthancUsername("user1");
		config.setOrthancPassword("user1");
		config.setLastChangedIndex(-1);
		
		assertEquals("http://localhost:8062", config.getOrthancBaseUrl());
		assertEquals("http://proxy:8062", config.getOrthancProxyUrl());
		assertEquals("user1", config.getOrthancUsername());
		assertEquals("user1", config.getOrthancPassword());
		assertEquals(-1, config.getLastChangedIndex());
	}
	
	@Test
	public void testDefaultLastChangedIndexValue() {
		OrthancConfiguration config = new OrthancConfiguration();
		assertEquals(-1, config.getLastChangedIndex(), "Default value for lastChangedIndex should be -1");
	}
}
