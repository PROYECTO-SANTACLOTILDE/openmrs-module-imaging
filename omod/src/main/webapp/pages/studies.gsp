<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.imageStudies.title") ])
    ui.includeCss("imaging", "general.css")
    ui.includeCss("imaging", "studies.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.studies") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}
<% ui.includeJavascript("imaging", "sortable.min.js") %>

<h2>
    ${ ui.message("imaging.studies") }
</h2>

<div style="color:red;">
${param["message"]?.getAt(0) ?: ""}
</div>

<script>
    function togglePopupUpload() {
        const overlay = document.getElementById('popupOverlayUpload');
        overlay.classList.toggle('show');
    }

    function toggleSynchronizeStudies() {
        const overlay = document.getElementById('popupOverlaySynchronization');
        overlay.classList.toggle('show');
    }

    function togglePopupDeleteStudy(studyInstanceUID, patient) {
        const overlay = document.getElementById('popupOverlayDeleteStudy');
        overlay.classList.toggle('show');
        document.deleteStudyForm.action = "/openmrs/module/imaging/deleteStudy.form?studyInstanceUID="
                                             + studyInstanceUID
                                             + "&patientId=" + patient;
    }
</script>

<div>
    <% if (orthancConfigurations.size() == 0) { %>
        No Orthanc server configured
    <% } else { %>
        <% if (privilegeModifyImageData) { %>
            <button class="btn-open-popup-upload" onclick="togglePopupUpload()">Upload Study</button>
        <% } %>
        <button class="btn-open-popup-sync" onclick="toggleSynchronizeStudies()">Get latest studies</button>
     <% } %>
</div>

<div id="table-scroll">
    <table id="studies" class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl" data-sortable>
        <thead>
            <tr>
                <th>${ ui.message("imaging.app.studyInstanceUid.label")}</th>
                <th>${ ui.message("imaging.app.patientName.label")}</th>
                <th>${ ui.message("imaging.app.date.label")}</th>
                <th>${ ui.message("imaging.app.description.label")}</th>
                <th>${ ui.message("imaging.app.server.label")}</th>
                <th>${ ui.message("coreapps.actions") }</th>
            </tr>
        </thead>
        <tbody>
            <% if (studies.size() == 0) { %>
                <tr>
                    <td colspan="6" align="center">${ui.message("imaging.studies.none")}</td>
                </tr>
            <% } %>
            <% studies.each { study -> %>
                <tr>
                    <td class="uid-td">
                        <a href="${ui.pageLink("imaging", "series", [patientId: patient.id, studyInstanceUID: study.studyInstanceUID])}">${ui.format(study.studyInstanceUID)}</a>
                    </td>
                    <td>${ui.format(study.patientName)}</td>
                    <td>${ui.format(study.studyDate)}</td>
                    <td class="description-td">${ui.format(study.studyDescription)}</td>
                    <td>${ui.format(study.orthancConfiguration.orthancBaseUrl)}</td>
                     <td>
                        <a class="delete-study"
                            <% if (privilegeModifyImageData) { %>
                                 onclick="togglePopupDeleteStudy('${ui.format(study.studyInstanceUID)}', '${patient.id}')"><i class="icon-remove delete-action"></i>
                            <% } %>
                        </a>
                        <div style="display: flex">
                           <a href="http://localhost:8042/stone-webviewer/index.html?study=${ui.format(study.studyInstanceUID)}" title="${ ui.message("imaging.app.openStoneView.label") }">
                                <img class="stone-img" alt="Show image in stone viewer" src="${ ui.resourceLink("imaging", "images/stoneViewer.png")}"/></a>
                           <a href="http://localhost:8042/ohif/viewer?StudyInstanceUIDs=${ui.format(study.studyInstanceUID)}" title="${ ui.message("imaging.app.openOHIFView.label") }">
                               <img class="ohif-img" alt="Show image in OHIF viewer" src="${ ui.resourceLink("imaging", "images/ohifViewer.png")}"/></a>
                       </div>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
<div id="popupOverlayUpload" class="overlay-container">
    <div class="popup-box">
        <h2 style="color: #009384;">Upload study</h2>
        <form class="form-container" enctype='multipart/form-data' method='POST' action='/openmrs/module/imaging/uploadStudy.form?patientId=${patient.id}'>
            <label class="form-label" for="server">Select Orthanc server</label>
            <select class="select-config" id="orthancConfigurationId" name="orthancConfigurationId">
                <% orthancConfigurations.each { config -> %>
                    <option value="${config.id}">${ui.format(config.orthancBaseUrl)}</option>
                <% } %>
            </select>
            <label class="form-label" for="files">Select files to upload</label>
            <input class="form-input" type='file' name='files' multiple>
            <div class="popup-box-btn">
                <button class="btn-submit" type="submit">Upload</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupUpload()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="popupOverlaySynchronization" class="overlay-container">
    <div class="popup-box">
        <h2 style="color: #009384;">Fetch studies from providers</h2>
        <form class="form-container" method='POST' action='/openmrs/module/imaging/syncStudies.form?patientId=${patient.id}'>
            <label class="form-label" for="server">Select Orthanc server</label>
            <select class="select-config" id="orthancConfigurationId" name="orthancConfigurationId">
                <option value="-1">All servers</option>
                <% orthancConfigurations.each { config -> %>
                    <option value="${config.id}">${ui.format(config.orthancBaseUrl)}</option>
                <% } %>
            </select>
            <div class="popup-box-btn">
                <button class="btn-submit" type="submit">Start</button>
                <button class="btn-close-popup" type="button" onclick="toggleSynchronizeStudies()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="popupOverlayDeleteStudy" class="overlay-container">
    <div class="popup-box" style="width: 65%>
        <h2>Delete study</h2>
        <form name="deleteStudyForm" class="form-container" method='POST'>
            <h3 id="deleteStudyMessage">${ ui.message("imaging.deleteStudy.message") }</h3>
            <div class="popup-box-btn">
                <button class="btn-submit" type="submit">${ ui.message("general.yes") }</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupDeleteStudy()">Cancel</button>
            </div>
        </form>
    </div>
</div>

















