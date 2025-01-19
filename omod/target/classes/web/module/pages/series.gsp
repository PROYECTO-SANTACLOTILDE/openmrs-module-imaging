<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.studySeries.title") ])
    ui.includeCss("imaging", "series.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) },
            ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.series") }" }
    ];
</script>


${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("imaging", "deleteSeriesDialog") }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.series") }
</h2>

<table id="series" class="series" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("imaging.app.seriesInstanceUID.label")}</th>
            <th>${ ui.message("imaging.app.seriesNumber.label")}</th>
            <th>${ ui.message("imaging.app.modality.label")}</th>
            <th>${ ui.message("imaging.app.openStoneView.label")}</th>
            <th>${ ui.message("imaging.app.delete.label")}</th>
        </tr>
    </thead>
    <tbody>
        <% if (serieses.size() == 0) { %>
            <tr>
                <td colspan="6" align="center">${ui.message("imaging.series.none")}</td>
            </tr>
        <% } %>
        <% serieses.each { series -> %>
            <tr>
                <td>
                    <a href="${ui.pageLink("imaging", "instances", [patientId: patient.id, seriesInstanceUID: series.seriesInstanceUID])}">${ui.format(series.seriesInstanceUID)}</a>
                </td>
                <td>${ui.format(series.seriesNumber)}</td>
                <td>${ui.format(series.modality)}</td>
                 <td>
                    <a href="http://localhost:8042/stone-webviewer/index.html?study=${ui.format(studyInstanceUID)}&series=${series.seriesInstanceUID}">
                        <img class="series-stone-img" src="${ ui.resourceLink("imaging", "images/stoneViewer.png") }"/></a>
                 </td>
                 <td>
                    <form onsubmit="return confirm('Do you really want to delete the series?');" action="/openmrs/module/imaging/deleteSeries.form?series?uuid=${series.seriesInstanceUID}" method="GET">
                        <button class="table-btn-link" type="submit">
                            <img class="icon" style="margin-left:12px;" src="${ ui.resourceLink("imaging", "images/delete.png") }"/></button>
                    </form>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<br/>