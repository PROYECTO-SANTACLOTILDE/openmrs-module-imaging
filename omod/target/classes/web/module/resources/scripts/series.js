//var removeSeriesDialogy = null;

$(document).ready( function() {
    deleteSeriesDialog = emr.setupConfirmationDialog({
        selector: '#imaging-delete-series-dialog',
        actions: {
            cancel: function() {
            	deleteSeriesDialog.close();
            }
        }
    });
});

function showRemoveSeriesDialog() {
    deleteSeriesDialog.show();
}

function deleteStudy(series, id) {
    jq("#seriesInstanceUID").val(id);
    jq("#deleteSeriesMessage").text(jq("#deleteSeriesMessageTemplate").val().replace("{0}", series));
    showRemoveSeriesDialog(series, id);
}