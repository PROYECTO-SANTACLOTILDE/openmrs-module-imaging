var deleteSeriesDialog = null;

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

function showDeleteSeriesDialog() {
    deleteSeriesDialog.show();
}

function deleteSeries(series, id) {
    jq("#seriesInstanceUID").val(id);
    jq("#deleteSeriesMessage").text(jq("#deleteSeriesMessageTemplate").val().replace("{0}", series));
    showDeleteSeriesDialog(series, id);

}