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
        { label: "${ ui.message("imaging.worklist") }" }
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
    function togglePopupNewProcedureSteps(requestProcedureId, patient){
      const overlay = document.getElementById('popupOverlayNewProcedureSteps');
      overlay.classList.toggle('show');
      document.newProcedureStepsForm.action = "/${contextPath}/module/imaging/newProcedureSteps.form?requestProcedureId="
                                           + requestProcedureId
                                           + "&patientId=" + patient;
    }

    function togglePopupNewRequest(patient) {
        const overlay = document.getElementById('popupOverlayNewRequest');
        overlay.classList.toggle('show');
        document.newRequestForm.action = "/${contextPath}/module/imaging/newRequest.form?patientId="
                                          + patient
    }

    function togglePopupDeleteProcedureSteps(stepsId, patient){
      const overlay = document.getElementById('popupOverlayDeleteProcedureSteps');
      overlay.classList.toggle('show');
      document.deleteProcedureStepsForm.action = "/${contextPath}/module/imaging/deleteProcedureSteps.form?id="
                                           + stepsId
                                           + "&patientId=" + patient;
    }

    function toggleSteps(requestProcedureId) {
        var row = document.getElementById("steps-" + requestProcedureId);
        var toggleLink= document.querySelector("a.toggle-items[onclick='toggleSteps(\"" + requestProcedureId + "\")']");

        if (row.style.display === "none" || row.style.display === "") {
            row.style.display = "table-row";
        } else {
            row.style.display = "none";
        }
    }

    function togglePopupDeleteRequest(requestProcedureId, patient) {
        const overlay = document.getElementById('popupOverlayDeleteRequest');
        overlay.classList.toggle('show');
        document.deleteRequestForm.action = "/${contextPath}/module/imaging/deleteRequest.form?requestProcedureId="
                                            + requestProcedureId
                                            + "&patientId=" + patient;
    }

    function generateAccessionNumber() {
        // Example: Generate a unique number using the current timestamp
        let uniqueNumber = Date.now();
        document.getElementById("accessionNumber").value = uniqueNumber;
    }

</script>

<div>
    <% if (orthancConfigurations.size() == 0) { %>
        No Orthanc server configured
    <% } else { %>
        <% if (privilegeEditWorklist) { %>
            <button class="btn-open-popup-new-request" onclick="togglePopupNewRequest('${patient.id}')">New Request</button>
        <% } %>
    <% } %>
</div>

<div id="table-scroll">
   <table id="worklist" class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl" data-sortable>
       <thead class="imaging-table-thead">
           <script src="filter_table.js" defer></script>
           <tr>
               <th>${ ui.message("imaging.app.accessionNumber.label")}</th>
               <th>${ ui.message("imaging.app.worklistStatus.label")}</th>
               <th>${ ui.message("imaging.app.priority.label")}</th>
               <th>${ ui.message("imaging.app.studyInstanceUid.label")}</th>
               <th>${ ui.message("imaging.app.physician.label")}</th>
               <th>${ ui.message("imaging.app.description.label")}</th>
               <th>${ ui.message("imaging.app.server.label")}</th>
               <th data-no-filter style="width: 120px;">${ ui.message("coreapps.actions") }</th>
           </tr>
       </thead>
       <tbody>
            <% if (requestProcedureMap.size() == 0) { %>
                <tr>
                    <td colspan="7" align="center">${ui.message("imaging.worklist.none")}</td>
                </tr>
            <% } %>
            <% requestProcedureMap.keySet().each { requestProcedure -> %>
                 <tr>
                    <th>${ui.format(requestProcedure.accessionNumber)}</th>
                    <td>${ui.format(requestProcedure.status)}</td>
                    <td>${ui.format(requestProcedure.priority)}</td>
                    <td>${ui.format(requestProcedure.studyInstanceUID)}</td>
                    <td>${ui.format(requestProcedure.orthancConfiguration.orthancBaseUrl)}</td>
                    <td>${ui.format(requestProcedure.requestingPhysician)}</td>
                    <td>${ui.format(requestProcedure.requestDescription)}</td>
                    <td>
                        <% if (privilegeEditWorklist) { %>
                           <a class="delete-requestProcedure"
                                onclick="togglePopupDeleteRequest('${requestProcedure.id}', '${patient.id}')"><i class="icon-remove delete-action"></i></a>
                           <a class="create-requestProcedureSteps"
                                onclick="togglePopupNewProcedureSteps('${requestProcedure.id}', '${patient.id}')">
                                <img class="new-img" alt="Create a new procedure steps" src="${ ui.resourceLink("imaging", "images/edit.png")}"/></a>
                           <a class="toggle-items" aria-expanded="false"
                                onclick="toggleSteps('${requestProcedure.id}')">
                                <img class="expand-img" alt="Show the procedure steps" src="${ ui.resourceLink("imaging", "images/expand.png")}"/></a>
                        <% } %>
                    </td>
                 </tr>
                 <!-- Hidden row for item IDs -->
                 <tr id="steps-${requestProcedure.id}" class="hidden-steps" style="display: none;">
                     <td colspan="6">
                        <% requestProcedureMap[requestProcedure].each { steps ->  %>
                           <div class="stepsDiv">
                                <% if (privilegeEditWorklist) { %>
                                    <button class="btn-delete-request" onclick="togglePopupDeleteProcedureSteps('${steps.id}', '${patient.id}')">Delete</button>
                                <% } %>
                                <table class="table procedureStepsTable no-filter">
                                    <thead>
                                        <tr>
                                           <th class='step-name-th'>Name</th>
                                           <th>Value</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                           <td>Modality:</td>
                                           <td>${steps.modality}</td>
                                        </tr>
                                        <tr>
                                           <td>AET Title:</td>
                                           <td>${steps.aetTitle}</td>
                                        </tr>
                                        <tr>
                                          <td>Referring Physician:</td>
                                          <td>${steps.scheduledReferringPhysician}</td>
                                        </tr>
                                        <tr>
                                           <td>Requested Procedure Description:</td>
                                           <td>${steps.requestedProcedureDescription}</td>
                                        </tr>
                                        <tr>
                                           <td>Step Start Date:</td>
                                           <td>${steps.stepStartDate}</td>
                                        </tr>
                                        <tr>
                                           <td>Step Start Time:</td>
                                           <td>${steps.stepStartTime}</td>
                                        </tr>
                                        <tr>
                                           <td>Station Name:</td>
                                           <td>${steps.stationName}</td>
                                        </tr>
                                        <tr>
                                           <td>Procedure Step Location:</td>
                                           <td>${steps.procedureStepLocation}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        <% } %>
                     </td>
                 </tr>
            <% } %>
       </tbody>
  </table>
</div>

<div id="popupOverlayNewProcedureSteps" class="overlay-container">
    <div class="popup-box">
        <h2 style="color: #009384;">Create procedure steps</h2>
        <form class="form-container" name="newProcedureStepsForm" method="POST">
            <table class="table procedureStepsTable no-filter">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Modality</td>
                        <td><select name="modality" id="modality" required>
                                <option value="CR">CR (Computed Radiography)</option>
                                <option value="CT">CT (Computed Tomography)</option>
                                <option value="MR">MR (Magnetic Resonance Imaging)</option>
                                <option value="US">US (Ultrasound)</option>
                                <option value="XA">XA (X-ray Angiography)</option>
                                <option value="DX">DX (Digital Radiography)</option>
                                <option value="MG">MG (Mammography)</option>
                                <option value="NM">NM (Nuclear Medicine)</option>
                                <option value="PT">PT (Positron Emission Tomography)</option>
                                <option value="RF">RF (Radio Fluoroscopy)</option>
                                <option value="SC">SC (Secondary Capture)</option>
                                <option value="XC">XC (External-camera Photography)</option>
                                <option value="OP">OP (Ophthalmic Photography)</option>
                                <option value="PR">PR (Presentation State)</option>
                                <option value="SR">SR (Structured Report)</option>
                                <option value="RT">RT (Radiotherapy)</option>
                              </select>
                        </td>
                    </tr>
                    <tr>
                        <td>aetTitle</td>
                        <td><input class="rpInput" type="text" name="aetTitle" id="aetTitle" required></td>
                    </tr>
                    <tr>
                        <td>Referring Physician</td>
                        <td><input class="rpInput" type="text" name="scheduledReferringPhysician" id="scheduledReferringPhysician" required></td>
                    </tr>
                    <tr>
                        <td>Description</td>
                        <td><textarea class="rpInput" name="requestedProcedureDescription" id="requestedProcedureDescription" rows="4" cols="50" required></textarea></td>
                    </tr>
                    <tr>
                        <td>Start Date</td>
                        <td><input class="rpInput" type="date" name="stepStartDate" id="stepStartDate" required></td>
                    </tr>
                    <tr>
                        <td>Start Time</td>
                        <td><input class="rpInput" type="time" name="stepStartTime" id="stepStartTime" required></td>
                    </tr>
                    <tr>
                        <td>Station Name</td>
                        <td><input class="rpInput" type="text" name="stationName" id="stationName"></td>
                    </tr>
                    <tr>
                        <td>Procedure Step Location</td>
                        <td><input class="rpInput" type="text" name="procedureStepLocation" id="procedureStepLocation"></td>
                    </tr>
                </tbody>
            </table>
            <div class="popup-box-btn">
                <button class="btn-submit" type="submit">Save</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupNewProcedureSteps()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="popupOverlayNewRequest" class="overlay-container">
    <div class="popup-box">
        <h2 style="color: #009384;">Create request</h2>
        <form class="form-container" name="newRequestForm" method="POST">
            <table class="table requestTable no-filter">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                   <tr>
                      <td>Accession Number</td>
                      <td>
                        <div style="display: inline-flex; width: 100%">
                           <input class="rpInput" type="text" name="accessionNumber" id="accessionNumber" required>
                           <a class="toggle-items" aria-expanded="false" onclick="generateAccessionNumber()">
                           <img class="numbers-img" alt="Generate the new numbers" src="${ ui.resourceLink("imaging", "images/numbers.png")}"/></a>
                        </div>
                      </td>
                   </tr>
                   <tr>
                        <td>Orthanc Configuration</td>
                        <td>
                            <select class="select-config" id="orthancConfigurationId" name="orthancConfigurationId">
                                <% orthancConfigurations.each { config -> %>
                                    <option value="${config.id}">${ui.format(config.orthancBaseUrl)}</option>
                                <% } %>
                            </select>
                        </td>
                   </tr>
                   <tr>
                        <td>Physician</td>
                        <td><input class="rpInput" type="text" name="requestingPhysician" id="requestingPhysician" required></td>
                   </tr>
                   <tr>
                       <td>Description</td>
                       <td><textarea class="rpInput" name="requestDescription" id="requestDescription" rows="4" cols="50" required></textarea></td>
                   </tr>
                   <tr>
                       <td>Priority</td>
                       <td><select name="priority" id="priority" required>
                               <option value="HIGH">HIGH</option>
                               <option value="MEDIUM">MEDIUM</option>
                               <option value="LOW">LOW</option>
                           </select>
                       </td>
                   </tr>
                   <tr>
                      <td>study Instance UID</td>
                      <td><input class="rpInput" type="text" name="studyInstanceUID" id="studyInstanceUID"></td>
                   </tr>
                </tbody>
            </table>
            <div class="popup-box-btn">
                <button class="btn-submit" type="submit">Save</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupNewRequest()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="popupOverlayDeleteRequest" class="overlay-container">
    <div class="popup-box" style="width: 65%;">
        <h2>Delete Request</h2>
        <form name="deleteRequestForm" class="form-container" method="POST">
            <h2 id="deleteRequestMessage">${ ui.message("imaging.deleteRequest.message") }</h3>
            <div class="popup-box-btn" style="margin-top: 40px;">
                <button class="btn-submit" type="submit">${ ui.message("imaging.action.delete") }</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupDeleteRequest()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<div id="popupOverlayDeleteProcedureSteps" class="overlay-container">
    <div class="popup-box" style="width: 65%;">
        <h2>Delete procedure steps</h2>
        <form name="deleteProcedureStepsForm" class="form-container" method="POST">
            <h2 id="deleteProcedureStepsMessage">${ ui.message("imaging.deleteProcedureSteps.message") }</h3>
            <div class="popup-box-btn" style="margin-top: 40px;">
                <button class="btn-submit" type="submit">${ ui.message("imaging.action.delete") }</button>
                <button class="btn-close-popup" type="button" onclick="togglePopupDeleteProcedureSteps()">Cancel</button>
            </div>
        </form>
    </div>
</div>

