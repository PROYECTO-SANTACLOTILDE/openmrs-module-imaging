<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.studySeries.title") ])
    ui.includeCss("imaging", "general.css")
    ui.includeCss("imaging", "series.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.studies") }",
            link: '${ui.pageLink("imaging", "studies", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.series") }" }
    ];
</script>


${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}
<% ui.includeJavascript("imaging", "sortable.min.js") %>

<h2>
    ${ ui.message("imaging.series") }
</h2>

<div id="table-scroll">
    <table id="series" class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl" data-sortable>
        <thead>
            <tr>
                <th>${ ui.message("imaging.app.seriesInstanceUID.label")}</th>
                <th>${ ui.message("imaging.app.seriesNumber.label")}</th>
                <th>${ ui.message("imaging.app.description.label")}</th>
                <th>${ ui.message("imaging.app.date.label")}</th>
                <th>${ ui.message("imaging.app.modality.label")}</th>
                <th>${ ui.message("coreapps.actions") }</th>
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
                    <td class="uid-td">
                        <a href="${ui.pageLink("imaging", "instances", [patientId: patient.id, seriesInstanceUID: series.seriesInstanceUID, studyInstanceUID: studyInstanceUID])}">${ui.format(series.seriesInstanceUID)}</a>
                    </td>
                    <td>${ui.format(series.seriesNumber)}</td>
                    <td>${ui.format(series.seriesDescription)}</td>
                    <td>${ui.format(series.seriesDate)}</td>
                    <td>${ui.format(series.modality)}</td>
                     <td>
                        <i class="icon-remove delete-action" title="${ ui.message("coreapps.delete") }"
                            onclick="deleteSeries('${ui.encodeJavaScriptAttribute(ui.format(series))}', ${ series.seriesInstanceUID})"></i>
                        <a href="http://localhost:8042/stone-webviewer/index.html?study=${ui.format(studyInstanceUID)}&series=${series.seriesInstanceUID}" title="${ ui.message("imaging.app.openStoneView.label") }">
                            <img class="series-stone-img" src="${ ui.resourceLink("imaging", "images/stoneViewer.png") }"/></a>
                     </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
<br/>