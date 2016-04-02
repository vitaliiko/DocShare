$(document).ready(function() {

    var documentId;
    var files;

    $('input[type=file]').on('change', function(event) {
        files = event.target.files;
    });

    $('.upload-btn').click(function() {
        var data = new FormData();
        $.each(files, function(key, value) {
            data.append(key, value);
        });
        $.ajax({
            url: 'document/upload',
            type: 'POST',
            data: data,
            cache: false,
            dataType: 'json',
            success: function(documents) {
                //$('#documentTable').append("<tr id='" + "'>" +
                //    "<td><button type='button' name='groupInfoButton' class='btn btn-link group-info-btn'" +
                //    "data-toggle='modal' data-target='#groupInfo'> " + groupName + " </button>" +
                //    "</td><td><input type='button' class='btn btn-default removeGroupButton' id='"  + "' value='Remove friends group'>" +
                //    "</td></tr>");
            }
        });
    });

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
