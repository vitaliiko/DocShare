$(document).ready(function() {

    $('#saveGroup').click(function() {
        var groupName = $('#groupName').val();
        var friends = [];
        $('.check-box:checked').each(function() {
           friends.push($(this).val());
        });
        $.ajax({
            url: '/friends/create_group',
            data: {groupName: groupName, friends: friends},
            success: function() {
                $('#groupTable').append('<tr><td><button type="button" ' +
                    'class="btn btn-link" data-toggle="modal" data-target="#groupInfo">' + groupName +
                    '</button></td> </tr>');
                $('#groupInfo').modal('hide');
                $('#groupName').val('');
                $('#friends-list').html('');
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

    $('#addGroupButton').click(function() {
        $.ajax({
            url: '/friends/get_friends',
            dataType: 'json',
            success: function(friends) {
                var input = '';
                $.each(friends, function (k, v) {
                    input += "<input type='checkbox' class='check-box' value='" + v.id + "'>" +
                        v.firstName + ' ' + v.lastName + '<br>';
                });
                $('#friends-list').html(input);
                $('#groupName').val('');
            }
        });
    });

    $('#removeFriendButton').click(function() {
       $.ajax({
           url: '/friends/delete_friend',
           data: $(this).val(),
           success: function() {
               $(this).closest('tr').remove();
           }
        })
    });
});
