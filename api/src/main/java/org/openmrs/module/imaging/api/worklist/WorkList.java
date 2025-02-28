package org.openmrs.module.imaging.api.worklist;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.imaging.OrthancConfiguration;

public class WorkList extends BaseOpenmrsData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1;
	
	private Integer id;
	
	private OrthancConfiguration orthancConfiguration;
	
	private String createdDate;
	
	private String status; //e.g. active, completed
	
	private String modalityId; //Foreign key to modality table
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer integer) {
	}
	
	public String getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getModalityId() {
		return modalityId;
	}
	
	public void setModalityId(String modalityId) {
		this.modalityId = modalityId;
	}
	
	public OrthancConfiguration getOrthancConfiguration() {
		return orthancConfiguration;
	}
	
	public void setOrthancConfiguration(OrthancConfiguration orthancConfiguration) {
		this.orthancConfiguration = orthancConfiguration;
	}
}
