package org.openmrs.module.imaging.api.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.OrthancConfigurationService;
import org.openmrs.module.imaging.api.dao.DicomStudyDao;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
public class DicomStudyServiceImpl extends BaseOpenmrsService implements DicomStudyService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DicomStudyDao dao;
	
	public void setDao(DicomStudyDao dao) {
		this.dao = dao;
	}
	
	public DicomStudyDao getDao() {
		return dao;
	}
	
	private HttpURLConnection getOrthancConnection(String method, String url, String path, String username, String password)
	        throws IOException {
		String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		URL serverURL = URI.create(url).resolve(path).toURL();
		HttpURLConnection con = (HttpURLConnection) serverURL.openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("Authorization", "Basic " + encoding);
		con.setUseCaches(false);
		return con;
	}
	
	private void sendOrthancQuery(HttpURLConnection con, String query) throws IOException {
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestProperty( "charset", "utf-8");
		con.setDoOutput(true);
		byte[] data = query.getBytes(StandardCharsets.UTF_8);
		con.setRequestProperty( "Content-Length", Integer.toString(data.length));
		try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
			wr.write(data);
		}
	}
	
	@Override
	public int testOrthancConnection(String url, String username, String password) throws IOException {
		HttpURLConnection con = getOrthancConnection("GET", url, "/system", username, password);
		int status = con.getResponseCode();
		con.disconnect();
		return status;
	}
	
	@Override
	public void fetchStudies() throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		List<OrthancConfiguration> configs = orthancConfigurationService.getAllOrthancConfigurations();
		for (OrthancConfiguration config : configs) {
			fetchStudies(config);
		}
	}
	
	@Override
	public void fetchStudies(OrthancConfiguration config) throws IOException {
		log.info("Fetching studies from orthanc server " + config.getOrthancBaseUrl());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		HttpURLConnection con = getOrthancConnection("POST", config.getOrthancBaseUrl(), "/tools/find",
		    config.getOrthancUsername(), config.getOrthancPassword());
		sendOrthancQuery(con, "{" + "\"Level\": \"Studies\"," + " \"Expand\": true," + " \"Query\": {}" + " }");
		int status = con.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			JsonNode studiesData = new ObjectMapper().readTree(con.getInputStream());
			for (JsonNode studyData : studiesData) {
				log.info("Parsing study data received from orthanc server");
				String studyInstanceUID = studyData.path("MainDicomTags").path("StudyInstanceUID").getTextValue();
				String orthancStudyUID = studyData.path("ID").getTextValue();
				String patientName = studyData.path("PatientMainDicomTags").path("PatientName").getTextValue();
				Date studyDate;
				try {
					studyDate = dateFormat.parse(studyData.path("MainDicomTags").path("StudyDate").getTextValue());
				}
				catch (ParseException e) {
					studyDate = null;
				}
				String studyDescription = studyData.path("MainDicomTags").path("StudyDescription").getTextValue();
				String gender = studyData.path("PatientMainDicomTags").path("Gender").getTextValue();
				
				DicomStudy study = new DicomStudy(studyInstanceUID, orthancStudyUID, null, config, patientName, studyDate,
				        studyDescription, gender);
				
				// only save if the study does not already exist
				if (getDicomStudy(studyInstanceUID) == null) {
					dao.saveDicomStudy(study);
				}
			}
		} else {
			throw new IOException("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error "
			        + con.getResponseCode() + " " + con.getResponseMessage());
		}
	}
	
	@Override
	public int uploadFile(OrthancConfiguration config, InputStream is) throws IOException {
		HttpURLConnection con = getOrthancConnection("POST", config.getOrthancBaseUrl(), "/instances",
		    config.getOrthancUsername(), config.getOrthancPassword());
		con.setRequestProperty("Content-Type", "application/dicom");
		con.setDoOutput(true);
		IOUtils.copy(is, con.getOutputStream());
		return con.getResponseCode();
	}
	
	@Override
	public List<DicomStudy> getStudies(Patient pt) {
		return dao.getAllDicomStudiesByPatient(pt);
	}
	
	@Override
	public List<DicomStudy> getAllStudies() {
		return dao.getAllDicomStudies();
	}
	
	@Override
	public DicomStudy getDicomStudy(String studyInstanceUID) {
		return dao.getDicomStudy(studyInstanceUID);
	}
	
	@Override
	public void setPatient(DicomStudy study, Patient patient) {
		study.setMrsPatient(patient);
		dao.saveDicomStudy(study);
	}
	
	@Override
	public void deleteStudy(DicomStudy dicomStudy) {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(dicomStudy
		        .getOrthancConfiguration().getId());
		try {
			HttpURLConnection con = getOrthancConnection("DELETE", config.getOrthancBaseUrl(),
			    "/studies/" + dicomStudy.getOrthancStudyUID(), config.getOrthancUsername(), config.getOrthancPassword());
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				dao.removeDicomStudy(dicomStudy);
			} else {
				throw new RuntimeException("Failed to delete DICOM study. Response Code: " + responseCode + ", Study UID: "
				        + dicomStudy.getOrthancStudyUID());
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Error while communicating with Orthanc server.", e);
		}
	}
	
	@Override
	public int deleteSeries(String seriesOrthancUID, DicomStudy seriesStudy) {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(seriesStudy
		        .getOrthancConfiguration().getId());
		int responseCode = 0;
		try {
			HttpURLConnection con = getOrthancConnection("DELETE", config.getOrthancBaseUrl(),
			    "/series/" + seriesOrthancUID, config.getOrthancUsername(), config.getOrthancPassword());
			responseCode = con.getResponseCode();
		}
		catch (IOException e) {
			throw new RuntimeException("Error while communicating with Orthanc server.", e);
		}
		return responseCode;
	}
	
	@Override
	public List<DicomSeries> fetchSeries(String studyInstanceUID) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		List<OrthancConfiguration> configs = orthancConfigurationService.getAllOrthancConfigurations();
		List<DicomSeries> seriesList = new ArrayList<>();
		for (OrthancConfiguration config : configs) {
			log.info("Fetching series of the selected study from orthanc server " + config.getOrthancBaseUrl());
			HttpURLConnection con = getOrthancConnection("POST", config.getOrthancBaseUrl(), "/tools/find",
					config.getOrthancUsername(), config.getOrthancPassword());
			sendOrthancQuery(con, "{" + "\"Level\": \"Series\"," + " \"Expand\": true," + " \"Query\": {\"StudyInstanceUID\":\"" + studyInstanceUID + "\"}" + " }");
			int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				JsonNode seriesesData = new ObjectMapper().readTree(con.getInputStream());
				for (JsonNode seriesData : seriesesData) {
					String seriesInstanceUID = seriesData.path("MainDicomTags").path("SeriesInstanceUID").getTextValue();
					String orthancSeriesUID = seriesData.path("ID").getTextValue();
					String seriesDescription = seriesData.path("MainDicomTags").path("SeriesDescription").getTextValue();
					String seriesNumber = seriesData.path("MainDicomTags").path("SeriesNumber").getTextValue();
					String modality = seriesData.path("MainDicomTags").path("Modality").getTextValue();
					Date seriesDate;
					try {
						seriesDate = dateFormat.parse(seriesData.path("MainDicomTags").path("SeriesDate").getTextValue());
					} catch (ParseException e) {
						seriesDate = null;
					}
					DicomSeries series = new DicomSeries(seriesInstanceUID, orthancSeriesUID, seriesDescription, seriesNumber, modality, seriesDate);
					seriesList.add(series);
				}
			} else {
				throw new IOException("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error "
						+ con.getResponseCode() + " " + con.getResponseMessage());
			}
		}
		return seriesList;
	}
	
	@Override
	public List<DicomInstance> fetchInstances(String seriesInstanceUID) throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		List<OrthancConfiguration> configs = orthancConfigurationService.getAllOrthancConfigurations();
		List<DicomInstance> instanceList = new ArrayList<>();
		for (OrthancConfiguration config : configs) {
			log.info("Fetching instances of the selected series from orthanc server " + config.getOrthancBaseUrl());
			HttpURLConnection con = getOrthancConnection("POST", config.getOrthancBaseUrl(), "/tools/find",
					config.getOrthancUsername(), config.getOrthancPassword());
			sendOrthancQuery(con, "{" + "\"Level\": \"Instance\"," + " \"Expand\": true," + " \"Query\": {\"SeriesInstanceUID\":\"" + seriesInstanceUID + "\"}" + " }");
			int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				JsonNode instancesData = new ObjectMapper().readTree(con.getInputStream());
				for (JsonNode instanceData : instancesData) {
					String sopInstanceUID = instanceData.path("MainDicomTags").path("SOPInstanceUID").getTextValue();
					String orthancInstanceUID = instanceData.path("ID").getTextValue();
					String instanceNumber = instanceData.path("MainDicomTags").path("InstanceNumber").getTextValue();
					String imagePositionPatient = instanceData.path("MainDicomTags").path("ImagePositionPatient").getTextValue();
					DicomInstance instance = new DicomInstance(sopInstanceUID, orthancInstanceUID, instanceNumber, imagePositionPatient);
					instanceList.add(instance);
				}
			} else {
				throw new IOException("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error "
						+ con.getResponseCode() + " " + con.getResponseMessage());
			}
		}
		return instanceList;
	}
	
	@Override
	public PreviewResult fetchInstancePreview(String orthancInstanceUID, DicomStudy study) throws IOException {
		OrthancConfigurationService orthancConfigurationService = Context.getService(OrthancConfigurationService.class);
		OrthancConfiguration config = orthancConfigurationService.getOrthancConfiguration(study.getOrthancConfiguration()
		        .getId());
		
		HttpURLConnection con = getOrthancConnection("GET", config.getOrthancBaseUrl(), "/instances/" + orthancInstanceUID
		        + "/preview", config.getOrthancUsername(), config.getOrthancPassword());
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// read image
			InputStream inputStream = con.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			
			PreviewResult result = new PreviewResult();
			result.data = outputStream.toByteArray();
			result.contentType = con.getContentType();
			return result;
		} else {
			throw new IOException("Request to Orthanc server " + config.getOrthancBaseUrl() + " failed with error "
			        + con.getResponseCode() + " " + con.getResponseMessage());
		}
	}
}
