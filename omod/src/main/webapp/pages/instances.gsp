<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.instances.title") ])
    ui.includeCss("imaging", "instances.css")
    ui.includeCss("imaging", "general.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) },
            ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.instances") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.instances") }
</h2>

<div id="table-scroll">
    <table id="instances" class="instances" width="100%" border="1" cellspacing="0" cellpadding="2">
        <thead>
            <tr>
                <th>${ ui.message("imaging.app.sopInstanceUID.label")}</th>
                <th>${ ui.message("imaging.app.instanceNumber.label")}</th>
                <th>${ ui.message("imaging.app.imagePositionPatient.label")}</th>
                <th>${ ui.message("coreapps.actions") }</th>
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
                    <td class="uid-td">${ui.format(instance.sopInstanceUID)}</td>
                    <td>${ui.format(instance.instanceNumber)}</td>
                    <td>${ui.format(instance.imagePositionPatient)}</td>
                    <td>
                        <i style="margin-left:15px" title="${ ui.message("imaging.app.instancePreview.label") }"
                            onclick="instancePreview('${ui.encodeJavaScriptAttribute(ui.format(instance))}', ${ instance.sopInstanceUID})">
                            <img class="instance-preview" src="${ ui.resourceLink("imaging", "images/preview.png") }"/>
                        </i>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
<br/>