<%
    ui.includeCss("imaging", "studies.css")
%>

<div class="info-section studies">
    <div class="info-header">
        <i class="icon-medical"></i>
        <h3>${ ui.message("imaging.studies").toUpperCase() }</h3>
        <i id ="imaging-editStudies" class="icon-pencil edit-action right" title="${ ui.message("coreapps.edit") }" onclick="location.href='${ui.pageLink("imaging", "studies", [patientId: patient.patient.id])}';"></i>
    </div>
    <div class="info-body">${ ui.message("imaging.sync.studies.message")}</div>
</div>