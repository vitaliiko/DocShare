$(document).ready(function() {

    var fileId;
    var shareUrl;
    var files;
    var docIds = [];
    var tableRows = [];
    var allTable = $('.allDocumentsTable');
    var privateTable = $('.privateDocumentsTable');
    var publicTable = $('.publicDocumentsTable');
    var forFriendsTable = $('.forFriendsDocumentsTable');

    function changeTab() {
        publicTable.hide(true);
        forFriendsTable.hide(true);
        privateTable.hide(true);
        allTable.hide(true);
        $("input:checkbox:checked").each(function() {
            $(this).prop('checked', false);
        });
        $('.action-btn').hide(true);
    }

    changeTab();
    allTable.show(true);

    $('.all-href').click(function() {
        event.preventDefault();
        changeTab();
        allTable.show(true);
    });

    $('.public-href').click(function() {
        event.preventDefault();
        changeTab();
        publicTable.show(true);
    });

    $('.for-friends-href').click(function() {
        event.preventDefault();
        changeTab();
        forFriendsTable.show(true);
    });

    $('.private-href').click(function() {
        event.preventDefault();
        changeTab();
        privateTable.show(true);
    });

    $('.select-all').click(function() {
        var checked = this.checked;
        $(".select:visible").each(function() {
            $(this).prop('checked', checked);
        });
        showHideButtons();
    });

    function showHideButtons() {
        var checkBoxCount = $(".select:checked").length;
        if (checkBoxCount == 0) {
            $('.action-btn').hide(true);
        } else {
            $('.action-btn').show(true);
            if (checkBoxCount == 1) {
                $('.single-selection').show(true);
            } else {
                $('.single-selection').hide(true);
            }
        }
    }

    $('.select').change(function() {
        showHideButtons();
    });

    $('.upload-btn').click(function() {
        var files = new FormData();
        var description = $('#description').val();
        files.append('file', $('#file').files[0]);
        if (files.length) {
            $.ajax({
                url: 'upload',
                type: 'POST',
                data: {files: files, description: description},
                cache: false,
                contentType: false,
                processData: false,
                success: function () {
                    location.reload();
                }
                //success: function (documents) {
                //    var counter = allTable.find('tr').count + 1;
                //    $.each(documents, function(k, v) {
                //        allTable.append(
                //            "<td class='document-num'>" +
                //            "<input type='checkbox' class='check-box' value='" + v.id + "'/> " + counter++ + "</td>" +
                //            "<td class='document-name'>" +
                //            "<a href='/document/browse-" + v.id + "'>" + v.name + "</a></td>" +
                //            "<td>" + v.size + "</td>" +
                //            "<td class='document-date'>Now</td>" +
                //            "<td><a href='/document/download-" + v.id + "' class='btn btn-success custom-width'>Download</a></td>"
                //        );
                //        counter++;
                //    });
                //}
            });
        }
    });

    $('.delete-btn').click(function() {
        var checkBoxesCount = 0;
        $('.select-doc:visible:checked').each(function() {
            checkBoxesCount++;
            var id = $(this).val();
            docIds.push(id);
            tableRows.push($('.tr-doc' + id));
        });
        $('#delete-dialog-text').text('Are you sure you want to move ' + checkBoxesCount + ' documents into trash?');
        //var row = this.closest('tr');
        //var fileName = $(row['name=file-name']).text();
        //documentId = row.id;

    });

    $('#deleteDocument').click(function() {
        $.ajax({
            url: '/document/move-to-trash',
            type: 'POST',
            data: {'docIds[]': docIds},
            success: function() {
                $.each(tableRows, function(k, v) {
                    v.remove();
                });
            }
        });
    });

    $('.share-doc-btn').click(function() {
        clearModalWindow();
        $('.group-check-box').show();
        shareUrl = '/document/share_document';

        var docId = $(this).val();
        $.getJSON('/document/get_document', {docId: docId}, function(document) {
            fileId = document.id;
            checkedBoxes(document.readers, $('.reader-check-box'));
            checkedBoxes(document.readersGroups, $('.readers-group-check-box'));
            checkedBoxes(document.editors, $('.editor-check-box'));
            checkedBoxes(document.editorsGroups, $('.editors-group-check-box'));
            $('.modal-title').text('Share ' + document.name);
            $('#' + document.access).prop('checked', true);
        });
    });

    $('.share-dir-btn').click(function() {
        clearModalWindow();
        $('.group-check-box').hide();
        shareUrl = '/document/share_directory';

        var dirId = $(this).val();
        $.getJSON('/document/get_directory', {dirId: dirId}, function(directory) {
            fileId = directory.id;
            checkedBoxes(directory.readers, $('.reader-check-box'));
            checkedBoxes(directory.readersGroups, $('.readers-group-check-box'));
            $('.modal-title').text('Share ' + directory.name);
            $('#' + directory.access).prop('checked', true);
        });
    });

    function checkedBoxes(readers, checkBoxes) {
        $.each(readers, function (k, v) {
            readers.push(v.id);
        });
        $(checkBoxes).each(function (k, v) {
            var isChecked = $.inArray(parseInt(v.value), readers) != -1;
            $(this).prop('checked', isChecked);
        });
    }

    $('#shareDocument').click(function() {
        var readers = [];
        var readersGroups = [];
        var editors = [];
        var editorsGroups = [];
        var access = $('input[name=access]:checked').val();
        $('.readers-group-check-box:checked').each(function (k, v) {
            readersGroups.push(v.value);
        });
        $('.reader-check-box:checked').each(function (k, v) {
            readers.push(v.value);
        });
        $('.editors-group-check-box:checked').each(function (k, v) {
            editorsGroups.push(v.value);
        });
        $('.editor-check-box:checked').each(function (k, v) {
            editors.push(v.value);
        });
        $.ajax({
            url: shareUrl,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                docId: fileId,
                access: access,
                readers: readers,
                readersGroups: readersGroups,
                editors: editors,
                editorsGroups: editorsGroups
            }),
            success: function () {
                clearModalWindow();
            }
        });
    });

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
    }

    $('#makeDir').click(function() {
        var dirName = $('#directoryName').val();
        $.ajax({
            url: '/document/make-directory',
            data: {dirName: dirName},
            success: function () {
                location.reload();
            }
        });
    });
});
