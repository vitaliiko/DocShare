
$(document).ready(function() {

    var friendsGroupId;
    var handlebarsPath = '/resources/js/templates/';

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
        $('#groupName').val('');
    }

    $.getJSON('/friends/get-friends-groups', function(groups) {
        $.each(groups, function (k, group) {
            loadTemplate(handlebarsPath + 'groupInfoRow.html', function (template) {
                $('#groupTable').append(template(group));
            });
        });
    });

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
                var group = {id: groupId, name: groupName};
                loadTemplate(handlebarsPath + 'groupInfoRow.html', function(template) {
                    $('.group-table').append(template(group));
                });
                $.each(friends, function (k, v) {
                    loadTemplate(handlebarsPath + 'groupInfoButton.html', function (template) {
                        $('.td-friend' +  v).append(template(group));
                    });
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
                $('.table').find($('.group' + friendsGroupId)).html(groupName);
                clearModalWindow();
            }
        });
    });

    $('.group-table').on('click', '.group-info-btn', function(event) {
        event.preventDefault();
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

    $('#groupTable').on('click', '.removeGroupButton', function() {
        var groupId = this.id;
        $.ajax({
            url: '/friends/delete_group',
            data: {groupId: groupId},
            success: function() {
                $('.group-table').find($('.tr-group' + groupId)).remove();
            }
        })
    });
});
