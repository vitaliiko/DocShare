
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
            success: function() {
                $('#groupTable').append("<tr id='" + "'>" +
                "<td><button type='button' name='groupInfoButton' class='btn btn-link group-info-btn'" +
                "data-toggle='modal' data-target='#groupInfo'> " + groupName + " </button>" +
                "</td><td><input type='button' class='btn btn-default removeGroupButton' id='"  + "' value='Remove friends group'>" +
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
                    $(this).text(groupName);
                });
                $('#groupInfo').modal('hide');
                clearModalWindow();
            }
        });
    });

    $('.group-info-btn').click(function() {
        clearModalWindow();
        $('#saveGroupButton').hide();
        $('#updateGroupButton').show();
        $.ajax({
            url: '/friends/get_group',
            dataType: 'json',
            data: {groupName: $(this).text()},
            success: function(group) {
                friendsGroupId = group.id;
                oldGroupName = group.name;
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
