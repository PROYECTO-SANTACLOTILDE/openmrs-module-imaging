package org.openmrs.module.imaging;

import org.openmrs.BaseOpenmrsData;
import java.io.Serializable;

public class OrthancConfiguration extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer id;
	
	private String orthancBaseUrl;
	
	private String orthancUsername;
	
	private String orthancPassword;
	
	public OrthancConfiguration() {
	}
	
	public OrthancConfiguration(String orthancBaseUrl, String orthancUsername, String orthancPassword) {
		this.orthancBaseUrl = orthancBaseUrl;
		this.orthancUsername = orthancUsername;
		this.orthancPassword = orthancPassword;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public String getOrthancBaseUrl() {
		return orthancBaseUrl;
	}
	
	public void setOrthancBaseUrl(String url) {
		this.orthancBaseUrl = url;
	}
	
	public void setOrthancUsername(String orthancUsername) {
		this.orthancUsername = orthancUsername;
	}
	
	public String getOrthancUsername() {
		return this.orthancUsername;
	}
	
	public void setOrthancPassword(String password) {
		this.orthancPassword = password;
	}
	
	public String getOrthancPassword() {
		return this.orthancPassword;
	}
	
}
