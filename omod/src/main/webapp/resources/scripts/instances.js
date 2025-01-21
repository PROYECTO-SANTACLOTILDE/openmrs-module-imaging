var previewInstanceDialog = null;

$(document).ready( function() {
    previewInstanceDialog = emr.setupConfirmationDialog({
        selector: '#imaging-preview-instance-dialog',
        actions: {
            cancel: function() {
            	previewInstanceDialog.close();
            }
        }
    });
});

function showPreviewInstanceDialog() {
    previewInstanceDialog.show();
}

function instancePreview(instance, id) {
    jq("#sopInstanceUID").val(id);
    jq("#previewInstanceMessage").text(jq("#previewInstanceMessageTemplate").val().replace("{0}", instance));
    showPreviewInstanceDialog(instance, id);
}