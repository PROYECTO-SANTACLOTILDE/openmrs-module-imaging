package org.openmrs.module.imaging.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.imaging.OrthancConfiguration;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.api.dao.DicomStudyDao;
import org.openmrs.module.imaging.api.study.DicomInstance;
import org.openmrs.module.imaging.api.study.DicomSeries;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	@Override
	@Transactional
	public List<DicomStudy> getAllStudiesByPatient(Patient pt) {
		return dao.getAllDicomStudiesByPatient(pt);
	}
	
	@Override
	public void setStudies(Patient pt, Studies retrievedStudies) {
	}
	
	@Override
	public void deleteStudy(DicomStudy dicomStudy) {
		dao.removeDicomStudy(dicomStudy);
	}
	
	@Override
	public List<DicomSeries> getSerieses(String studyInstanceUID) {

		List<DicomSeries> list = new ArrayList<>();
		list.add(new DicomSeries(
				"1.3.12.2.1107.5.99.1.24063.4.0.446793548272429",
				"1de00990-03680ef4-0be6bd5b-73a7d350-fb46abfa",
				1,
				"CT"
				));

		list.add(new DicomSeries(
				"1.3.12.2.1107.5.1.4.36085.2.0.517714246252254",
				"a69a10d7-068c5263-8aab53fe-de7af5f2-373a74bd",
				2,
				"PT"
				));

		list.add(new DicomSeries(
				"1.3.12.2.1107.5.1.4.36085.2.0.517109821292363",
				"b4b79447-c5c2a0c2-89985adf-9656920f-cb0db5de",
				3,
				"PT"
		));
//		return new ArrayList<DicomSeries>();
		return list;
	}
	
	@Override
	public List<DicomInstance> getInstances(String seriesInstanceUID) {
		List<DicomInstance> list = new ArrayList<>();
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.447989428888616",
				"0092ce4b-9d4b0966-f5fd8c6a-beb6daa7-2e6bcda9",
				1,
				"CT"
		));
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.447898729501248",
				"00cc35c5-f7e8bbe1-aa784413-87e00620-001104cc",
				2,
				"CT"
		));
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.448041919901416",
				"02184370-1bdd3c2e-5d14690c-4f7d6173-62c47724",
				3,
				"CT"
		));
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.447984711563260",
				"0363a0de-f443d8f8-1a9790fe-d79740f2-00c0724e",
				4,
				"CT"
		));
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.447894028101981",
				"079d5889-85cb07a3-d3608b8c-d5863ea4-39bdcd37",
				5,
				"CT"
		));
		list.add(new DicomInstance(
				"1.3.12.2.1107.5.99.1.24063.4.0.448037246263114",
				"08575e68-7d723709-c4590d0c-950bb164-096f8585",
				6,
				"CT"
		));
		return list;
	}
}
