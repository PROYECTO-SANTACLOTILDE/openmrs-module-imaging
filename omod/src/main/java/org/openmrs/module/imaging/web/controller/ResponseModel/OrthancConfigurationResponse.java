package org.openmrs.module.imaging.web.controller.ResponseModel;

import org.openmrs.module.imaging.OrthancConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class OrthancConfigurationResponse {
	
	private Integer id;
	
	private String orthancBaseUrl;
	
	private String orthancProxyUrl;
	
	public static OrthancConfigurationResponse createResponse(OrthancConfiguration config) {
		OrthancConfigurationResponse response = new OrthancConfigurationResponse();
		response.setId(config.getId());
		response.setOrthancProxyUrl(config.getOrthancProxyUrl());
		response.setOrthancBaseUrl(config.getOrthancBaseUrl());
		return response;
	}
	
	public static List<OrthancConfigurationResponse> configurationResponseList(List<OrthancConfiguration> configs) {
		return configs.stream().map(OrthancConfigurationResponse::createResponse).collect(Collectors.toList());
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getOrthancBaseUrl() {
		return orthancBaseUrl;
	}
	
	public void setOrthancBaseUrl(String orthancBaseUrl) {
		this.orthancBaseUrl = orthancBaseUrl;
	}
	
	public String getOrthancProxyUrl() {
		return orthancProxyUrl;
	}
	
	public void setOrthancProxyUrl(String orthancProxyUrl) {
		this.orthancProxyUrl = orthancProxyUrl;
	}
}
