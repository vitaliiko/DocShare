
$(document).ready(function() {

    var friendsGroupId;
    var oldFriends = [];

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
            success: function(groupId) {
                $('#groupTable').append("<tr class='group" + groupId + "'>" +
                    "<td><button type='button' name='groupInfoButton' class='btn btn-link group-info-btn'" +
                    "data-toggle='modal' data-target='#groupInfo'> " + groupName + " </button></td>" +
                    "<td><input type='button' class='btn btn-default removeGroupButton' id='" + groupId +
                    "' value='Remove friends group'>" +
                    "</td></tr>");
                $.each(friends, function(k, v) {
                    $('.td' + v).append("<button type='button' name='groupInfoButton' " +
                        "data-toggle='modal' data-target='#groupInfo'" +
                        "class='btn btn-link group-info-btn group" + groupId + "'>" + groupName + "</button>");
                });
                clearModalWindow();
            }
        });
    });

    $('#updateGroupButton').click(function() {
        var groupName = $('#groupName').val();
        var friends = [];
        $('.check-box:checked').each(function(k, v) {
            friends.push(v.value);
        });
        $.ajax({
            url: '/friends/update_group',
            contentType: 'json',
            data: {groupId: friendsGroupId, groupName: groupName, friends: friends},
            success: function() {
                $('.group' + friendsGroupId).html(groupName);
                clearModalWindow();
            }
        });
    });

    $('.group-info-btn').click(function() {
        clearModalWindow();
        $('#saveGroupButton').hide();
        $('#updateGroupButton').show();
        var groupId = this.id;
        $.getJSON('/friends/get_group', {groupId: groupId}, function(group) {
            friendsGroupId = group.id;
            var memberIds = [];
            $.each(group.friends, function (k, v) {
                memberIds.push(v.id);
            });
            $('.check-box').each(function (k, v) {
                var isChecked = $.inArray(parseInt(v.value), memberIds) != -1;
                $(this).prop('checked', isChecked);
            });
            $('.group-name-input').val(group.name);
        });
    });

    $('#addGroupButton').click(function() {
        clearModalWindow();
        $('#saveGroupButton').show();
        $('#updateGroupButton').hide();
    });

    $('.removeFriendButton').click(function() {
        var friendId = this.id;
        $.ajax({
            url: '/friends/delete_friend',
            data: {friendId: friendId},
            success: function() {
                $('.friend' + friendId).remove();
            }
        })
    });

    $('.removeGroupButton').click(function() {
        var groupId = this.id;
        $.ajax({
            url: '/friends/delete_group',
            data: {groupId: groupId},
            success: function() {
                $('.group' + groupId).remove();
            }
        })
    });
});
