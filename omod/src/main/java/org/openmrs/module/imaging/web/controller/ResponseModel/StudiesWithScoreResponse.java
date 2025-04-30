package org.openmrs.module.imaging.web.controller.ResponseModel;

import java.util.List;
import java.util.Map;

public class StudiesWithScoreResponse {
	
	public List<DicomStudyResponse> studies;
	
	public Map<String, Integer> scores;
}
