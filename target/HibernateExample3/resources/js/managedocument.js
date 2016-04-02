$(document).ready(function() {

    var documentId;

    $('.delete-btn').click(function() {
        var row = this.closest('tr');
        var fileName = $(row['name=file-name']).text();
        documentId = row.id;
        $('#dialog-text').text('Are you sure you want to move ' + fileName + ' into trash?');
    });

    $('#deleteDocument').click(function() {
        $.ajax({
            url: '/document/move-to-trash',
            type: 'POST',
            data: {docId: documentId},
            success: function() {
                $('table#documentTable tr#' + documentId).remove();
                $('#deleteDialog').modal('hide');
            }
        });
    });
});
