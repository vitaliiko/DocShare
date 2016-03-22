$(document).ready(function() {
    $('#saveGroup').click(function() {
        var groupName = $('#groupName').val();
        $.ajax({
            url: '/friends/create_group',
            data: {groupName: groupName},
            success: function() {
                $('#groupTable').append('<tr><td><button type="button" ' +
                    'class="btn btn-link" data-toggle="modal" data-target="#groupInfo">' + groupName +
                    '</button></td> </tr>');
                $('#groupInfo').modal('hide');
                $('#groupName').val('');
            }
        })
    });

    $('.group-info').click(function() {
        var groupName = $(this).text();
        $.ajax({
            url: '/friends/get_group',
            dataType: 'json',
            data: {groupName: groupName},
            success: function(group) {
                $('#groupName').val(group.name);
                var input = '';
                $.each(group.friendsSet, function(k, v) {
                    input += "<input type='checkbox' value='" + v.id + "' checked>" +
                        v.firstName + ' ' + v.lastName + '<br>';
                });
                $('#friends-list').html(input);
            }
        });
    });
});
