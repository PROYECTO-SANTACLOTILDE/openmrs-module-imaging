<div id="imaging-remove-series-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("imaging.deleteSeries") }</h3>
    </div>
    <div class="dialog-content">
        <ul>
            <li class="info">
                <span id="removeSeriesMessage">${ ui.message("imaging.deleteSeries.message") }</span>
            </li>
        </ul>
        <form method="POST" action="studies.page">
            <input type="hidden" name="patientId" value="${patient.id}"/>
            <input type="hidden" id="seriesInstanceUID" name="seriesInstanceUID" value=""/>
            <input type="hidden" name="action" value="deleteSeries"/>
            <input type="hidden" id="removeSeriesMessageTemplate" value="${ ui.message("imaging.deleteSeries.message") }"/>
            <button class="confirm right" type="submit">${ ui.message("general.yes") }</button>
            <button class="cancel">${ ui.message("general.no") }</button>
        </form>
    </div>
</div>