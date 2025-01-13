package org.openmrs.module.imaging.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.openmrs.module.imaging.extension.html.StudyComparator;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.util.ByFormattedObjectComparator;

import java.util.Comparator;

public class StudiesFragmentController {

    public void controller(FragmentModel model, @FragmentParam("patientId") Patient patient, UiUtils ui,
                           @SpringBean("studyService") DicomStudyService DicomStudyService) {

        Studies studies = DicomStudyService.getAllStudiesByPatient(patient);
        Comparator comparator = new StudyComparator(new ByFormattedObjectComparator(ui));
        studies.sort(comparator);

        model.addAttribute("studies", studies);
    }
}
