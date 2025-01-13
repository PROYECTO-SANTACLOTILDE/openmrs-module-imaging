var removeStudyDialogy = null;

$(document).ready( function() {
    deleteStudyDialog = emr.setupConfirmationDialog({
        selector: '#imaging-delete-study-dialog',
        actions: {
            cancel: function() {
            	deleteStudyDialog.close();
            }
        }
    });
});

function showRemoveStudyDialog() {
    deleteStudyDialog.show();
}

function deleteStudy(study, id) {
    jq("#studyInstanceUID").val(id);
    jq("#deleteStudyMessage").text(jq("#deleteStudyMessageTemplate").val().replace("{0}", study));
    showRemoveStudyDialog(study, id);
}