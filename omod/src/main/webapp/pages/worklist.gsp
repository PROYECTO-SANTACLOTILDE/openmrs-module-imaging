<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.worklist.title") ])
    ui.includeCss("imaging", "general.css")
    ui.includeCss("imaging", "worklist.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.worklist.all") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}
<% ui.includeJavascript("imaging", "sortable.min.js") %>
<% ui.includeJavascript("imaging", "filter_table.js")%>

<h2>
    ${ ui.message("imaging.worklist") }
</h2>

<script>
</script>

<div>
    <% if (orthancConfigurations.size() == 0) { %>
        No Orthanc server configured
    <% } else { %>
        <% if (privilegeModifyWorklist) { %>
            <button class="btn-popup-create" onclick="togglePopupCreate()">Create Worklist</button>
        <% } %>
        <button class="btn-popup-synchronize" onclick="toggleSynchronizeWorklist()">Get Worklist</button>
    <% } %>
</div>

<div id="table-scroll">
    <table id="studies" class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl" data-sortable>
        <thead class="imaging-table-thead">
            <script src="filter_table.js" defer></script>
            <tr>
                <th>${ ui.message("imaging.app.createDate.label")}</th>
                <th>${ ui.message("imaging.app.worklistStatus.label")}</th>
                <th>${ ui.message("imaging.app.modality.label")}</th>
                <th>${ ui.message("imaging.app.server.label")}</th>
                <th data-no-filter style="width: max-content;">${ ui.message("coreapps.actions") }</th>
            </tr>
        </thead>
        <tbody>
            <% if (workTasks.size() == 0) { %>
                <tr>
                    <td colspan="6" align="center">${ui.message("imaging.workTasks.none")}</td>
                </tr>
            <% } %>
            <% workTasks.each { task ->
                def baseUrl = task.orthancConfiguration.orthancProxyUrl?.trim() ? task.orthancConfiguration.orthancProxyUrl : task.orthancConfiguration.orthancBaseUrl
            %>
                <tr>

                </tr>
        </tbody>
    </table>
</div>
