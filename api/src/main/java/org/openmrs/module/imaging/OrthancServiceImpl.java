package org.openmrs.module.imaging;

import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OrthancServiceImpl implements OrthancService {
	
	@Value("${orthanc.base.url}")
	private String orthancBaseUrl;
	
	@Value("${orthanc.username}")
	private String username;
	
	@Value("${orthanc.password}")
	private String password;
	
	@Override
	public List<DicomStudy> getPatientStudies(String patientId) {
		String url = orthancBaseUrl + "/patients/" + patientId + "/studies";
		// Use RestTemplate or HttpClient to make HTTP calls
		// Parse and return results
		
		DicomStudy testStudy = new DicomStudy();
		testStudy.setStudyInstanceUID("123");
		testStudy.setStudyDescription("test");
		testStudy.setStudyDate(new Date());
		return new ArrayList<>(Arrays.asList(testStudy));
	}
	
	@Override
	public List<DicomSeries> getStudySeries(String studyId) {
		String url = orthancBaseUrl + "/studies/" + studyId;
		// Fetch study details
		return new ArrayList<>();
	}
}
