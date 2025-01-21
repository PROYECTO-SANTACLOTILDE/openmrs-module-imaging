<div id="imaging-delete-study-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("imaging.deleteStudy") }</h3>
    </div>
    <div class="dialog-content">
        <ul>
            <li class="info">
                <span id="deleteStudyMessage">${ ui.message("imaging.deleteStudy.message") }</span>
            </li>
        </ul>
        <form method="POST" action="studies.page">
            <input type="hidden" name="patientId" value="${patient.id}"/>
            <input type="hidden" id="studyInstanceUID" name="studyInstanceUID" value=""/>
            <input type="hidden" name="action" value="deleteStudy"/>
            <input type="hidden" id="deleteStudyMessageTemplate" value="${ ui.message("imaging.deleteStudy.message") }"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>