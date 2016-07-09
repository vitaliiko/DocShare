
$(document).ready(function() {

    var friendGroupId;
    var handlebarsPath = '/resources/js/templates/';
    var removeButton;

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
        $('#groupName').val('');
    }

    $.getJSON('/api/friend-groups', function(groups) {
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
            url: '/api/friend-groups',
            type: 'POST',
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
                url: '/api/friend-groups/' + friendGroupId,
                type: 'PUT',
                //contentType: 'json',
                data: {groupName: groupName, friendIds: friends},
                success: function() {
                    $('.info-table').find($('.group' + friendGroupId)).html(groupName);
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
        $.getJSON('/api/friend-groups/' + groupId, function(group) {
            friendGroupId = group.id;
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
            url: '/api/friends/' + friendId,
            type: 'DELETE',
            success: function() {
                $('.friend' + friendId).remove();
            }
        })
    });

    $('#groupTable').on('click', '.removeGroupButton', function() {
        friendGroupId = this.id;
        removeButton = $(this);
        var message = 'Are you sure yo want to remove friends group?';
        $('#deleteDialog').modal('show');
        $('#delete-dialog-text').text(message);
    });

    $('#deleteGroup').click(function() {
        $.ajax({
            url: '/api/friend-groups/' + friendGroupId,
            type: 'DELETE',
            success: function() {
                removeButton.parent().parent().remove();
                $('.friend-table').find($('.group' + friendGroupId)).remove();
            }
        });
    });
});
