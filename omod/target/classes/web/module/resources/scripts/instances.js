var previewInstanceDialogy = null;

$(document).ready( function() {
    previewInstanceDialogy = emr.setupConfirmationDialog({
        selector: '#imaging-preview-instance-dialog',
        actions: {
            cancel: function() {
            	previewInstanceDialogy.close();
            }
        }
    });
});

function showPreviewInstanceDialog() {
    previewInstanceDialogy.show();
}

function instancePreview(instance, id) {
    jq("#sopInstanceUID").val(id);
    jq("#showInstanceMessage").text(jq("#showInstanceMessageTemplate").val().replace("{0}", instance));
    showPreviewInstanceDialog(instance, id);
}