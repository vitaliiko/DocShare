
$(document).ready(function() {

    var friendsGroupId;
    var handlebarsPath = '/resources/js/templates/';
    var removeButton;

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
        $('#groupName').val('');
    }

    $.getJSON('/friends/friend_groups', function(groups) {
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
            },
            error: function() {
                $('.alert-danger').show();
                $('.alert-text').text('Friends group with such name already exist');
            }
        });
    });

    $('#updateGroupButton').click(function() {
        var groupName = $('#groupName').val();
        var friends = [];
        $('.check-box:checked').each(function(k, v) {
            friends.push(v.value);
        });
        if (groupName === undefined) {
            $('.alert-danger').show();
            $('.alert-text').text('You cannot create friends group without name');
        } else {
            $.ajax({
                url: '/friends/update_group',
                contentType: 'json',
                data: {groupId: friendsGroupId, groupName: groupName, friends: friends},
                success: function() {
                    $('.info-table').find($('.group' + friendsGroupId)).html(groupName);
                    clearModalWindow();
                },
                error: function() {
                    $('.alert-danger').show();
                    $('.alert-text').text('Friends group with such name already exist');
                }
            });
        }
    });

    $('.info-table').on('click', '.group-info-btn', function(event) {
        event.preventDefault();
        clearModalWindow();
        $('#saveGroupButton').hide();
        $('#updateGroupButton').show();
        $('.modal-title').text('Change group');
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
        $('.modal-title').text('Add new group');
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
        friendsGroupId = this.id;
        removeButton = $(this);
        var message = 'Are you sure yo want to remove friends group?';
        $('#deleteDialog').modal('show');
        $('#delete-dialog-text').text(message);
    });

    $('#deleteGroup').click(function() {
        $.ajax({
            url: '/friends/delete_group',
            data: {groupId: friendsGroupId},
            success: function() {
                removeButton.parent().parent().remove();
                $('.friend-table').find($('.group' + friendsGroupId)).remove();
            }
        });
    });
});
