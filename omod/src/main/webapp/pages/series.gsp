<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.studySeries.title") ])
    ui.includeCss("imaging", "series.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }", link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.series") }" }
    ];
</script>

<% if(includeFragments) {
    includeFragments.each{ %>
        ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, [ patient: patient])}
<%   }
} %>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("imaging", "deleteStudyDialog") }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.series") }
</h2>

<script>
    function toggleUploadStudySeries() {
        console.log("Function upload study series!")
    }
</script>

<div class="form-container">
    <button class="btn-open-popup-upload" onclick="toggleUploadStudySeries()">Upload Series</button>
</div>

<table id="series" class="series" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("imaging.app.seriesInstanceUID.label")}</th>
            <th>${ ui.message("imaging.app.seriesDescription.label")}</th>
            <th>${ ui.message("imaging.app.seriesNumber.label")}</th>
            <th>${ ui.message("imaging.app.modality.label")}</th>
            <th>${ ui.message("imaging.app.openStoneView.label")}</th>
            <th>${ ui.message("imaging.app.delete.label")}</th>
        </tr>
    </thead>
    <tbody>
        <% if (series.size() == 0) { %>
            <tr>
                <td colspan="6" align="center">${ui.message("imaging.series.none")}</td>
            </tr>
        <% } %>
        <% series.each { ser -> %>
            <tr>
                <td>${ui.format(ser.seriesInstanceUID)}</td>
                <td>${ui.format(ser.seriesDescription)}</td>
                <td>${ui.format(ser.seriesNumber)}</td>
                <td>${ui.format(ser.modality)}</td>
                 <td>
                    <form onsubmit=""
                          action="http://localhost:8042/stone-webviewer/index.html?study=2.16.840.1.113669.632.20.1211.10000231621">
                        <button class="table-btn-link" type="submit"><img class="icon" src="${ ui.resourceLink("imaging", "images/stoneViewer.png") }"/></button>
                    </form>
                 </td>
                 <td>
                    <form onsubmit="return confirm('Do you really want to delete the series?');" action="/openmrs/module/imaging/deleteSeries.form?seriesInstanceUID=${ser.seriesInstanceUID}" method="post">
                        <button class="table-btn-link" type="submit"><img class="icon" src="${ ui.resourceLink("imaging", "images/delete.png") }"/></button>
                    </form>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<br/>