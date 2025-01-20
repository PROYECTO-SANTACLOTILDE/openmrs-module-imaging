<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.imageStudies.title") ])
    ui.includeCss("imaging", "studies.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }", link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.studies") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("imaging", "deleteStudyDialog") }
${ ui.includeJavascript("imaging", "studies.js")}
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.studies") }
</h2>

<script>
    function toggleUploadStudy() {
        console.log("Function upload study!")
    }
    function toggleFullSynchronizeStudies() {
        console.log("Function full synchronization studies!")
    }
    function toggleGetNewStudies() {
        console.log("Function get new studies!")
    }
</script>

<div class="form-container">
    <button class="btn-open-popup-upload" onclick="toggleUploadStudy()">Upload Study</button>
    <button class="btn-open-popup-sync" onclick="toggleFullSynchronizeStudies()">Synchronize Studies</button>
    <button class="btn-open-popup-newStudies" onclick="toggleGetNewStudies()">Get new Studies</button>
</div>

<table id="studies" class="studies" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("imaging.app.studyInstanceUid.label")}</th>
            <th>${ ui.message("imaging.app.patientName.label")}</th>
            <th>${ ui.message("imaging.app.studyDate.label")}</th>
            <th>${ ui.message("imaging.app.description.label")}</th>
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
                <td style="width:50px;">
                    <a href="${ui.pageLink("imaging", "series", [patientId: patient.id, studyInstanceUID: study.studyInstanceUID])}">${ui.format(study.studyInstanceUID)}</a>
                </td>
                <td>${ui.format(study.patientName)}</td>
                <td>${ui.format(study.studyDate)}</td>
                <td>${ui.format(study.studyDescription)}</td>
                 <td>
                   <i class="icon-remove delete-action" style="margin-left:27px" title="${ ui.message("coreapps.delete") }"
                    onclick="deleteStudy('${ui.encodeJavaScriptAttribute(ui.format(study))}', ${ study.studyInstanceUID})"></i>
                    <div style="display: flex">
                       <a href="http://localhost:8042/stone-webviewer/index.html?study=${ui.format(study.studyInstanceUID)}" title="${ ui.message("imaging.app.openStoneView.label") }">
                            <img class="stone-img" alt="Show image in stone viewer" src="${ ui.resourceLink("imaging", "images/stoneViewer.png")}"/></a>
                       <a href="http://localhost:8042/ohif/viewer?StudyInstanceUIDs=${ui.format(study.studyInstanceUID)}" title="${ ui.message("imaging.app.openOHIFView.label") }">
                           <img class="ohif-img" alt="Show image in OHIF viewer" src="${ ui.resourceLink("imaging", "images/stoneViewer.png")}"/></a>
                   </div>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<br/>


















