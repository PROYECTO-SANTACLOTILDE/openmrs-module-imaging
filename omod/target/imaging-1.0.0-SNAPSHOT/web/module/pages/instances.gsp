<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.instances.title") ])
    ui.includeCss("imaging", "instances.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }", link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.instances") }" }
    ];
</script>

<% if(includeFragments) {
    includeFragments.each{ %>
        ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment, [ patient: patient])}
<%   }
} %>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.instances") }
</h2>

<table id="instances" class="instances" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("imaging.app.sopInstanceUID.label")}</th>
            <th>${ ui.message("imaging.app.instanceNumber.label")}</th>
            <th>${ ui.message("imaging.app.modality.label")}</th>
            <th>${ ui.message("imaging.app.instancePreview.label")}</th>
        </tr>
    </thead>
    <tbody>
         <% if (instances.size() == 0) { %>
            <tr>
                <td colspan="6" align="center">${ui.message("imaging.instances.none")}</td>
            </tr>
        <% } %>
        <% instances.each { instance -> %>
            <tr>
                <td>${ui.format(instance.sopInstanceUID)}</td>
                <td>${ui.format(instance.instanceNumber)}</td>
                <td>${ui.format(instance.instanceModality)}</td>
                <td>
                    <form onsubmit="" action="/openmrs/module/imaging/previewInstance.form?sopInstanceUID=${instance.sopInstanceUID}" method="post">
                        <button class="table-btn-link" type="submit"><img class="icon" src="${ ui.resourceLink("imaging", "images/preview.svg") }"/></button>
                    </form>
                </td>
            </tr>
        <% } %>
    </tbody>
</table>

<br/>