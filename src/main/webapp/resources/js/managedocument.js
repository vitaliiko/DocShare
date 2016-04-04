$(document).ready(function() {

    var documentId;
    var files;
    var docIds = [];
    var tableRows = [];

    function changeTab() {
        $('#publicDocumentsTable').hide(true);
        $('#forFriendsDocumentsTable').hide(true);
        $('#privateDocumentsTable').hide(true);
        $('#allDocumentsTable').hide(true);
        $("input:checkbox:checked").each(function() {
            $(this).prop('checked', false);
        });
        $('.action-btn').hide(true);
    }

    changeTab();
    $('#allDocumentsTable').show(true);

    $('input[type=file]').on('change', function(event) {
        files = event.target.files;
    });

    $('.all-href').click(function() {
        changeTab();
        $('#allDocumentsTable').show(true);
    });

    $('.public-href').click(function() {
        changeTab();
        $('#publicDocumentsTable').show(true);
    });

    $('.for-friends-href').click(function() {
        changeTab();
        $('#forFriendsDocumentsTable').show(true);
    });

    $('.private-href').click(function() {
        changeTab();
        $('#privateDocumentsTable').show(true);
    });

    $('.select-all').click(function() {
        var checked = this.checked;
        $("input:checkbox").each(function() {
            $(this).prop('checked', checked);
        });
    });

    $('.check-box').click(function() {
        var checkBoxCount = $("input:checkbox:checked").length;
        if (checkBoxCount == 0) {
            $('.action-btn').hide(true);
        } else {
            $('.action-btn').show(true);
            if (checkBoxCount == 1) {
                $('.single-selection').show(true);
            } else {
                $('.single-selection').hide(true);
            }
        }
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
        $('.check-box:checked').each(function() {
            docIds.push($(this).val());
            tableRows.push(this.closest('tr'));
        });
        //var row = this.closest('tr');
        //var fileName = $(row['name=file-name']).text();
        //documentId = row.id;
        //$('#dialog-text').text('Are you sure you want to move ' + fileName + ' into trash?');
    });

    $('#deleteDocument').click(function() {
        alert(docIds);
        $.ajax({
            url: '/document/move-to-trash',
            type: 'POST',
            data: {'docIds[]': docIds},
            success: function() {
                tableRows.forEach(function() {
                    $(this).remove();
                });
                $('#deleteDialog').modal('hide');
            }
        });
    });
});
