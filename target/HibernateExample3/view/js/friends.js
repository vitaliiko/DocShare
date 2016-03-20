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

    $('#groupButton').click(function() {
        $('#groupName').val("hello");
    });
});
