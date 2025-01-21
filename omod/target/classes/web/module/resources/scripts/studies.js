var deleteStudyDialog = null;

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

function showDeleteStudyDialog() {
    deleteStudyDialog.show();
}

function deleteStudy(study, id) {
    jq("#studyInstanceUID").val(id);
    jq("#deleteStudyMessage").text(jq("#deleteStudyMessageTemplate").val().replace("{0}", study));
    showDeleteStudyDialog(study, id);
}