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
            <th>${ ui.message("imaging.app.openStoneView.label")}</th>
            <th>${ ui.message("imaging.app.openOHIFView.label")}</th>
            <th>${ ui.message("imaging.app.delete.label")}</th>
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
                    <a href="http://localhost:8042/stone-webviewer/index.html?study=${ui.format(study.studyInstanceUID)}">
                        <img class="stone-img" alt="Show image in stone viewer" src="${ ui.resourceLink("imaging", "images/stoneViewer.png")}"/></a>
                </td>
                <td>
                    <a href="http://localhost:8042/ohif/viewer?StudyInstanceUIDs=${ui.format(study.studyInstanceUID)}">
                        <img class="ohif-img" alt="Show image in OHIF viewer" src="${ ui.resourceLink("imaging", "images/stoneViewer.png")}"/></a>
                </td>
                 <td>
                    <form class="delete-form" onsubmit="return confirm('Do you really want to delete the study?');"
                        action="/openmrs/module/imaging/deleteStudy.form?seriesInstanceUID=${ui.format(study.studyInstanceUID)}" method="post">
                        <button class="table-btn-link" type="submit">
                            <img class="icon" style="margin-left:12px;" src="${ ui.resourceLink("imaging", "images/delete.png") }"/></button>
                    </form>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<br/>


















