<%
    ui.decorateWith("appui", "standardEmrPage",  [ title: ui.message("imaging.app.instances.title") ])
    ui.includeCss("imaging", "general.css")
    ui.includeCss("imaging", "instances.css")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.familyName))) }, ${ ui.escapeJs(ui.encodeHtmlContent(ui.format(patient.givenName))) }",
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.studies") }",
            link: '${ui.pageLink("imaging", "studies", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.series") }",
            link: '${ui.pageLink("imaging", "series", [patientId: patient.id, studyInstanceUID: param["studyInstanceUID"].getAt(0)])}'},
        { label: "${ ui.message("imaging.instances") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<% ui.includeJavascript("imaging", "sortable.min.js") %>

<h2>
    ${ ui.message("imaging.instances") }
</h2>

<script>
    function togglePopupPreview(orthancInstanceUID, studyInstanceUID) {
        const overlay = document.getElementById('popupOverlayPreview');
        overlay.classList.toggle('show');

        if(orthancInstanceUID) {
            const container = document.getElementById("preview-container");
            if(container.lastChild) container.removeChild(container.lastChild);
            container.appendChild(document.createTextNode("Loading preview"))

            const url = '/openmrs/module/imaging/previewInstance.form?orthancInstanceUID='+orthancInstanceUID+'&studyInstanceUID='+studyInstanceUID
            fetch(url, { method: 'GET'})
                .then((response) => {
                    if(response.ok)
                        return response.blob()
                    else
                        return Promise.reject(response)
                })
                .then((blob) => {
                    const imageUrl = URL.createObjectURL(blob);
                    const imageElement = document.createElement("img");
                    imageElement.src = imageUrl;
                    imageElement.width = 340;
                    imageElement.height = 300;
                    container.removeChild(container.lastChild);
                    container.appendChild(imageElement);
                })
                .catch((response) => {
                    container.removeChild(container.lastChild);
                    return response.text()
                })
                .then((error) => {
                    if(error) container.appendChild(document.createTextNode(error));
                })
                .catch((error) => {
                    container.appendChild(document.createTextNode("Preview failed with status code "+response.status));
                })
          }
    }
</script>

<div id="table-scroll">
    <table id="instances" class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl" data-sortable>
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
                        <a style="margin-left:15px" title="${ ui.message("imaging.app.instancePreview.label") }"
                            onclick="togglePopupPreview('${ui.format(instance.orthancInstanceUID)}', '${param['studyInstanceUID'].getAt(0)}' )">
                            <img class="instance-preview" src="${ ui.resourceLink("imaging", "images/preview.png") }"/>
                        </a>
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
<br/>

<div id="popupOverlayPreview" class="overlay-container">
    <div class="popup-box" style="width: 55%">
        <h2>Instance Preview</h2>
        <div id="preview-container"></div>
        <div class="popup-box-btn">
            <button class="btn-close-popup" style="margin-top: 20px;" type="button" onclick="togglePopupPreview()">Close</button>
        </div>
    </div>
</div>

