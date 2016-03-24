$(document).ready(function() {

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
        $('#groupName').val('');
    }

    $('#saveGroupButton').click(function() {
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
                clearModalWindow();
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
                var memberIds = [];
                $.each(group.friendsSet, function(k, v) {
                    memberIds.push(v.id);
                });
                $('.check-box').each(function() {
                    var isChecked = $.inArray(parseInt($(this).val()), memberIds) != -1;
                    $(this).prop('checked', isChecked);
                });
                $('#groupName').val(group.name);
            }
        });
    });

    $('#addGroupButton').click(function() {
        clearModalWindow();

        //$.ajax({
        //    url: '/friends/get_friends',
        //    dataType: 'json',
        //    success: function(friends) {
        //        var input = '';
        //        $.each(friends, function (k, v) {
        //            input += "<input type='checkbox' class='check-box' value='" + v.id + "'>" +
        //                v.firstName + ' ' + v.lastName + '<br>';
        //        });
        //        $('#friends-list').html(input);
        //        $('#groupName').val('');
        //    }
        //});
    });

    $('.removeFriendButton').click(function() {
        var friendId = this.id;
        $.ajax({
            url: '/friends/delete_friend',
            data: {friendId: friendId},
            success: function() {
                $('table#friendsTable tr#' + friendId).remove();
            }
        })
    });
});
