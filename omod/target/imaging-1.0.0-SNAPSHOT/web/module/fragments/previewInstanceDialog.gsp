<div id="imaging-instance-preview-dialog" class="dialog" style="display: none">
     <div class="dialog-header">
            <h3>${ ui.message("imaging.instancePreview") }</h3>
     </div>
    <div class="dialog-content">
       <ul>
            <li class="info">
                <span id="previewInstanceMessage">Instance information</span>
            </li>
        </ul>
        <form method="POST" action="instance.page">
            <input type="hidden" name="patientId" value="${patient.id}"/>
            <input type="hidden" id="sopInstanceUID" name="sopInstanceUID" value=""/>
            <input type="hidden" name="action" value="instancePreview"/>
            <input type="hidden" id="previewInstanceMessageTemplate" value="${ ui.message("imaging.instancePreview.message") }"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>