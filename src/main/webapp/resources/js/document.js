
$(document).ready(function() {
    var docId = $('.doc-id').val();
    var newFileNameInput = $('#newFileName');
    var docNameLine = $('#docName');
    var dangerAlert = $('.alert-danger');
    var successAlert = $('.alert-success');
    var alertText = $('.alert-text');

    $('#renameFile').click(function() {
        var newName = newFileNameInput.val();
            $.ajax({
                url: '/api/documents/' + docId + '/rename',
                type: 'POST',
                data: {newDocName: newName},
                success: function(document) {
                    if (document !== undefined) {
                        var newDocName = document.name;
                        var docNameWithLocation = docNameLine.text();
                        var slashIndex = docNameWithLocation.lastIndexOf('/');
                        if (slashIndex > 0) {
                            docNameWithLocation = docNameWithLocation.substring(0, slashIndex + 1) + newDocName;
                            docNameLine.text(docNameWithLocation);
                        } else {
                            docNameLine.text(newDocName);
                        }
                    }
                    dangerAlert.hide(true);
                },
                error: function() {
                    dangerAlert.show(true);
                    alertText.text("File with such name already exist");
                }
            });
        newFileNameInput.val('');
    });
});
