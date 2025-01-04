package org.openmrs.module.imaging;

import org.openmrs.BaseOpenmrsData;
import java.io.Serializable;

public class OrthancConfiguration extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer orthancId;
	
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
	
	public void setOrthancId(Integer orthancId) {
		this.orthancId = orthancId;
	}
	
	public Integer getOrthancId() {
		return orthancId;
	}
	
	@Override
	public void setId(Integer id) {
		setOrthancId(id);
	}
	
	public Integer getId() {
		return getOrthancId();
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
