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
        { label: "${ ui.message("imaging.studies") }", link: '${ui.pageLink("imaging", "studies", [patientId: patient.id])}'},
        { label: "${ ui.message("imaging.worklist.editor") }" }
    ];
</script>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient ]) }
${ ui.includeFragment("uicommons", "infoAndErrorMessage")}

<h2>
    ${ ui.message("imaging.worklist.editor") }
</h2>

<script>
    function autoFillSummary() {
        let patientName = document.getElementById('patientName').value;
        let modality = document.getElementById('modality').value;
        let studyDescription = document.getElementById('studyDescription').value;

        document.getElementById('summary').innerText =
            `Patient ${patientName} requires a ${modality} scan for ${studyDescription}.`;
    }
</script>

<div class="container mt-4">
    <h2>Doctor Worklist Entry</h2>
    <form action="/submitWorklist" method="POST" class="mt-3">
        <div class="row">
            <div class="col-md-6">
                <label>Patient ID:</label>
                <input type="text" class="form-control" name="patientID" value="${patientData.id}" readonly>
            </div>
            <div class="col-md-6">
                <label>Patient Name:</label>
                <input type="text" class="form-control" id="patientName" name="patientName" value="${patientData.name}" required>
            </div>
        </div>

        <div class="row mt-2">
            <div class="col-md-6">
                <label>Birth Date:</label>
                <input type="date" class="form-control" name="patientBirthDate" value="${patientData.birthDate}" required>
            </div>
            <div class="col-md-6">
                <label>Gender:</label>
                <input type="text" class="form-control" name="patientGender" value="${patientData.gender}" required>
            </div>
        </div>

        <div class="row mt-2">
            <div class="col-md-6">
                <label>Study ID:</label>
                <input type="text" class="form-control" name="studyID" value="${patientData.studyID}" required>
            </div>
            <div class="col-md-6">
                <label>Accession Number:</label>
                <input type="text" class="form-control" name="accessionNumber" value="${patientData.accessionNumber}" required>
            </div>
        </div>

        <div class="row mt-2">
            <div class="col-md-6">
                <label>Referring Physician:</label>
                <input type="text" class="form-control" name="referringPhysician" value="${patientData.referringPhysician}" required>
            </div>
            <div class="col-md-6">
                <label>Study Description:</label>
                <input type="text" class="form-control" id="studyDescription" name="studyDescription" value="${patientData.studyDescription}" required oninput="autoFillSummary()">
            </div>
        </div>

        <div class="row mt-2">
            <div class="col-md-6">
                <label>Modality:</label>
                <input type="text" class="form-control" id="modality" name="modality" value="${patientData.modality}" required oninput="autoFillSummary()">
            </div>
            <div class="col-md-6">
                <label>Requested Procedure Description:</label>
                <input type="text" class="form-control" name="requestedProcedureDescription" value="${patientData.requestedProcedureDescription}" required>
            </div>
        </div>

        <div class="row mt-2">
            <div class="col-md-6">
                <label>Scheduled Performing Physician:</label>
                <input type="text" class="form-control" name="scheduledPerformingPhysician" value="${patientData.scheduledPerformingPhysician}" required>
            </div>
            <div class="col-md-6">
                <label>Special Needs:</label>
                <input type="text" class="form-control" name="specialNeeds" value="${patientData.specialNeeds}" required>
            </div>
        </div>

        <div class="mt-4">
            <h5>Summary:</h5>
            <p id="summary">Patient ${patientData.name} requires a ${patientData.modality} scan for ${patientData.studyDescription}.</p>
        </div>

        <button type="submit" class="btn btn-primary mt-3">Submit Worklist</button>
    </form>
</div>
