package org.openmrs.module.imaging.api.worklist;

public class Modality {
	
	private static final long serialVersionUID = 1;
	
	private Integer id;
	
	private String modalityName;
	
	private String location;
	
	private String status;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getModalityName() {
		return modalityName;
	}
	
	public void setModalityName(String modalityName) {
		this.modalityName = modalityName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
