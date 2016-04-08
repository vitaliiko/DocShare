
$(document).ready(function() {

    var friendsGroupId;
    var handlebarsPath = '/resources/js/templates/';

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
                var group = {groupId: groupId, groupName: groupName};
                loadTemplate(handlebarsPath + 'groupTableRow.html', function(template) {
                    $('#groupTable').append(template(group));
                });
                $.each(friends, function (k, v) {
                    loadTemplate(handlebarsPath + 'groupInfoButton.html', function (template) {
                        $('.td' +  v).append(template(group));
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
                $('.group' + friendsGroupId).html(groupName);
                clearModalWindow();
            }
        });
    });

    $('.table').on('click', '.group-info-btn', function(event) {
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

    function loadTemplate(path, callback) {
        var source, template;
        $.ajax({
            url: path,
            success: function(data) {
                source = data;
                template = Handlebars.compile(source);
                if (callback && typeof callback === 'function') {
                    callback(template);
                }
            }
        });
    }
});
