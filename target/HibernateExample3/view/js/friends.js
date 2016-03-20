$(document).ready(function() {
    $('#saveGroup').click(function() {
        var groupName = $('#groupName').val();
        $.ajax({
            url: '/friends/create_group',
            data: {groupName: groupName},
            success: function() {
                $('#groupTable').append('<tr><td><button type="button" ' +
                    'class="btn btn-link" data-toggle="modal" data-target="#groupInfo">' + groupName + '</button></td> </tr>');
                $('#groupInfo').modal('hide');
                $('#groupName').val('');
            }
        })
    });

    $('.group-info').click(function() {
        var groupName = $(this).text();
        $.ajax({
           url: '/friends/get_group',
           data: {groupName: groupName},
           success: function(groupId) {
               $('#groupName').val(groupName);
               $('#group-action').text(groupId);
           }
        });
    });
});
