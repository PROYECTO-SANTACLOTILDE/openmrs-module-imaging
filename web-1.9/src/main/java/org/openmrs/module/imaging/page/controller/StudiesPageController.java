package org.openmrs.module.imaging.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.module.imaging.Studies;
import org.openmrs.module.imaging.api.study.DicomStudy;
import org.openmrs.module.imaging.extension.html.StudyComparator;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.util.ByFormattedObjectComparator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.openmrs.module.imaging.api.DicomStudyService;
import org.apache.commons.lang.StringUtils;
import org.openmrs.module.appframework.domain.Extension;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.openmrs.module.imaging.ImagingConstants.STUDIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT;

public class StudiesPageController {

    protected Log log = LogFactory.getLog(this.getClass());

    public void controller(@RequestParam("patientId") Patient patient,
                           @RequestParam(value="returnUrl", required = false) String returnUrl,
                           PageModel model, UiUtils ui,
                           @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
                           @SpringBean("studyService") DicomStudyService DicomStudyService) {

        Studies studies = DicomStudyService.getAllStudiesByPatient(patient);
        Comparator<DicomStudy> comparator = new StudyComparator(new ByFormattedObjectComparator(ui));
        studies.sort(comparator);

        if(StringUtils.isBlank(returnUrl)) {
            returnUrl = ui.pageLink("coreapps", "clinicianfacing/patient", Collections.singletonMap("patientId", (Object) patient.getId()));
        }

        List<Extension> includeFragments = appFrameworkService.getExtensionsForCurrentUser(STUDIES_PAGE_INCLUDE_FRAGMENT_EXTENSION_POINT);
        Collections.sort(includeFragments);
        model.addAttribute("includeFragments", includeFragments);

        model.addAttribute("patient", patient);
        model.addAttribute("studies", studies);
        model.addAttribute("returnUrl", returnUrl);
    }

    public String post(@RequestParam("patientId") Patient patient,
                       @RequestParam(value="action", required = false) String action,
                       @RequestParam(value = "studyInstanceUID", required = false) String studyInstanceUID,
                       @RequestParam(value="returnUrl", required = false) String returnUrl,
                       PageModel model, UiUtils ui,
                       HttpSession session, @SpringBean("studyService") DicomStudyService DicomStudyService) {

        if (StringUtils.isNotBlank(action)) {
            try {
                Studies studies = null;
                if ("deleteStudy".equals(action)){
                    studies = DicomStudyService.getAllStudiesByPatient(patient);
                    studies.remove(studies.getStudy(studyInstanceUID));
                }
                DicomStudyService.setStudies(patient, studies);

                InfoErrorMessageUtil.flashErrorMessage(session, "imaging.message.success");

                return "redirect:imaging/studies.page?patientId=" + patient.getPatientId() + "&returnUrl=" + ui.urlEncode(returnUrl);
            } catch (Exception e) {
                session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "imaging.message.fail");
            }
        }
        model.addAttribute("studies", DicomStudyService.getAllStudiesByPatient(patient));
        model.addAttribute("returnUrl", returnUrl);

        return null;
    }

    @RequestMapping(value = "/module/imaging/deleteStudy.form", method = RequestMethod.POST)
    public void deleteStudy(HttpServletResponse response, @RequestParam(value = "dicomStudy") DicomStudy dicomStudy,
                            @RequestParam(value = "username") String username,
                            @RequestParam(value = "password") String password) {
        try {
            String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            try {
                String url = dicomStudy.getOrthancConfiguration().getOrthancBaseUrl();
                URL serverURL = new URL(url + "/studies/" + dicomStudy.getStudyInstanceUID());
                HttpURLConnection con = (HttpURLConnection) serverURL.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "Basic " + encoding);
                con.setRequestProperty("Content-Type", "application/json");
                // Check response code
                int responseCode = con.getResponseCode();
                System.out.println("Response Code: " + responseCode);

            } catch (MalformedURLException e) {
                response.getOutputStream().print("The URL is not well formed.");
            } catch (UnknownHostException e) {
                response.getOutputStream().print("The server could not be reached.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
