$(document).ready(function() {

    var documentId;

    $('.delete-btn').click(function() {
        documentId = this.closest('tr').id;
    });

    $('#deleteDocument').click(function() {
        $.ajax({
            url: '/document/delete',
            type: 'POST',
            data: {docId: documentId},
            success: function() {
                $('table#documentTable tr#' + documentId).remove();
                $('#deleteDialog').modal('hide');
            }
        });
    });
});
