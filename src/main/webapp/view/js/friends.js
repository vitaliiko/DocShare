$(document).ready(function() {

    var friendsGroupId;
    var oldGroupName;

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
                $('#groupTable').append("<tr id='" + groupId + "'>" +
                "<td><button type='button' name='groupInfoButton' class='btn btn-link group-info-btn'" +
                "data-toggle='modal' data-target='#groupInfo'> " + groupName + " </button>" +
                "</td><td><input type='button' class='btn btn-default removeGroupButton' id='" + groupId + "' value='Remove friends group'>" +
                "</td></tr>");
                $('#groupInfo').modal('hide');
                clearModalWindow();
            }
        });
    });

    $('#updateGroupButton').click(function() {
        var groupName = $('#groupName').val();
        var friends = [];
        $('.check-box:checked').each(function() {
            friends.push($(this).val());
        });
        $.ajax({
            url: '/friends/update_group',
            contentType: 'json',
            data: {groupId: friendsGroupId, groupName: groupName, friends: friends},
            success: function() {
                $('.group-info[text="'+oldGroupName+'"]').each(function() {
                    alert($(this).text());
                    $(this).text(groupName);
                });
                $('#groupInfo').modal('hide');
                clearModalWindow();
            }
        })
    });

    function showGroupInfo(groupName) {
        $('#saveGroupButton').hide();
        $('#updateGroupButton').show();
        $.ajax({
            url: '/friends/get_group',
            dataType: 'json',
            data: {groupName: groupName},
            success: function(group) {
                friendsGroupId = group.id;
                var memberIds = [];
                $.each(group.friends, function(k, v) {
                    memberIds.push(v.id);
                });
                $('.check-box').each(function() {
                    var isChecked = $.inArray(parseInt($(this).val()), memberIds) != -1;
                    $(this).prop('checked', isChecked);
                });
                $('#groupName').val(group.name);
            }
        });
    }

    $('.group-info-btn').click(function() {
        showGroupInfo($(this).text());
    });

    $('<button/>')
        .name('groupInfoButton')
        .click(function() {
            showGroupInfo($(this).text());
        });

    $('#addGroupButton').click(function() {
        clearModalWindow();
        $('#saveGroupButton').show();
        $('#updateGroupButton').hide();

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
                var divId = 'checkBoxDiv' + friendId;
                $('div[id="'+divId+'"]').remove();
                //var checkBox = $('.check-box[value="'+friendId+'"]');
                //var checkBoxId = checkBox.attr('id');
                //checkBox.remove();
                //$('label[for="'+checkBoxId+'"]').remove();
            }
        })
    });

    $('.removeGroupButton').click(function() {
        var groupId = this.id;
        $.ajax({
            url: '/friends/delete_group',
            data: {groupId: groupId},
            success: function() {
                $('table#groupTable tr#' + groupId).remove();
            }
        })
    });
});
