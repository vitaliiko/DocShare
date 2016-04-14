$(document).ready(function() {

    var content = $('.content');
    var fileId;
    var fileAccess;
    var shareUrl;
    var files;
    var docIds = [];
    var dirIds = [];
    var tableRows = [];
    var allTable = $('.ALL');
    var privateTable = $('.PRIVATE');
    var publicTable = $('.PUBLIC');
    var forFriendsTable = $('.FOR_FRIENDS');
    var handlebarsPath = '/resources/js/templates/';
    var backLink = $('.back-link');
    var dirHashName;
    var dangerAlert = $('.alert-danger');
    var successAlert = $('.alert-success');

    function changeTab() {
        $('.doc-table').hide(true);
        $('.switch-btn').css('font-weight', 'normal');
        makeBoxesUnchecked();
    }

    function makeBoxesUnchecked() {
        $("input:checkbox:checked").each(function() {
            $(this).prop('checked', false);
        });
        $('.action-btn').hide(true);
    }

    function setSelectionStyle(element) {
        $(element).css('font-weight', 'bold');
    }

    changeTab();
    allTable.show(true);
    setSelectionStyle($('.all-href'));
    backLink.hide();
    $.getJSON('/document/get-directory-content-root', function(files) {
        renderDirectories(files);
        renderDocuments(files);
    });

    $('.all-href').click(function() {
        event.preventDefault();
        changeTab();
        allTable.show(true);
        setSelectionStyle($('.all-href'));
    });

    $('.public-href').click(function() {
        event.preventDefault();
        changeTab();
        publicTable.show(true);
        setSelectionStyle($('.public-href'));
    });

    $('.for-friends-href').click(function() {
        event.preventDefault();
        changeTab();
        forFriendsTable.show(true);
        setSelectionStyle($('.for-friends-href'));
    });

    $('.private-href').click(function() {
        event.preventDefault();
        changeTab();
        privateTable.show(true);
        setSelectionStyle($('.private-href'));
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

    content.on('change', '.select', function() {
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
            });
        }
    });

    $('.delete-btn').click(function() {
        var docCount = 0;
        var dirCount = 0;
        var message = 'Are you sure you want to move ';
        $('.select-doc:visible:checked').each(function() {
            docCount++;
            var id = $(this).val();
            docIds.push(id);
            tableRows.push($('.tr-doc' + id));
        });
        $('.select-dir:visible:checked').each(function() {
            dirCount++;
            var id = $(this).val();
            dirIds.push(id);
            tableRows.push($('.tr-dir' + id));
        });
        if (docCount == 1) {
            message += docCount + ' document ';
        }
        if (docCount > 1) {
            message += docCount + ' documents ';
        }
        if (docCount > 0 && dirCount > 0) {
            message += 'and ';
        }
        if (dirCount == 1) {
            message += dirCount + ' directory ';
        }
        if (dirCount > 1) {
            message += dirCount + ' directories ';
        }
        message += 'into trash?';
        $('#delete-dialog-text').text(message);
    });

    $('#deleteDocument').click(function() {
        $.ajax({
            url: '/document/move-to-trash',
            type: 'POST',
            data: {'docIds[]': docIds, 'dirIds[]': dirIds},
            success: function() {
                $.each(tableRows, function(k, v) {
                    v.remove();
                });
            }
        });
    });

    content.on('click', '.share-doc-btn', function() {
        clearModalWindow();
        makeBoxesUnchecked();
        $('.group-check-box').show();
        shareUrl = '/document/share_document';

        var docId = $(this).val();
        $.getJSON('/document/get_document', {docId: docId}, function(document) {
            fileId = document.id;
            fileAccess = document.access;
            makeBoxesChecked(document.readers, $('.reader-check-box'));
            makeBoxesChecked(document.readersGroups, $('.readers-group-check-box'));
            makeBoxesChecked(document.editors, $('.editor-check-box'));
            makeBoxesChecked(document.editorsGroups, $('.editors-group-check-box'));
            $('.share-modal-title').text('Share ' + document.name);
            $('#' + document.access).prop('checked', true);
        });
    });

    content.on('click', '.share-dir-btn', function() {
        clearModalWindow();
        makeBoxesUnchecked();
        $('.group-check-box').hide();
        shareUrl = '/document/share_directory';

        var dirId = $(this).val();
        $.getJSON('/document/get_directory', {dirId: dirId}, function(directory) {
            fileId = directory.id;
            fileAccess = directory.access;
            makeBoxesChecked(directory.readers, $('.reader-check-box'));
            makeBoxesChecked(directory.readersGroups, $('.readers-group-check-box'));
            $('.share-modal-title').text('Share ' + directory.name);
            $('#' + directory.access).prop('checked', true);
        });
    });

    function makeBoxesChecked(readers, checkBoxes) {
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
            success: function (file) {
                clearModalWindow();
                if (fileAccess !== file.access) {
                    if (file.type === 'doc') {
                        $('.' + fileAccess).find($('.tr-doc' + file.id)).remove();
                        loadTemplate(handlebarsPath + 'documentRow.html', function (template) {
                            $('.' + file.access).append(template(file));
                        });
                    } else {
                        $('.' + fileAccess).find($('.tr-dir' + file.id)).remove();
                        loadTemplate(handlebarsPath + 'directoryRow.html', function (template) {
                            $('.' + file.access).append(template(file));
                        });
                    }
                }
            }
        });
    });

    function clearModalWindow() {
        $('.check-box').each(function() {
            $(this).prop('checked', false);
        });
    }

    $('.make-dir-btn').click(function() {
        $('#directoryName').val('');
    });

    $('#makeDir').click(function() {
        var dirName = $('#directoryName').val();
        $.ajax({
            url: '/document/make-directory',
            data: {dirName: dirName, dirHashName: dirHashName},
            success: function (directory) {
                loadTemplate(handlebarsPath + 'directoryRow.html', function(template) {
                    var html = template(directory);
                    allTable.append(html);
                    privateTable.append(html);
                });
            }
        });
    });

    content.on('click', '.get-dir-content', function(event) {
        event.preventDefault();
        var dirName = $(this).text();
        dirHashName = this.id;
        var url = '/document/get-directory-content-' + dirHashName;

        $.getJSON(url, function(files) {
            var locationElement = $('#location');
            var location = locationElement.text();
            locationElement.text(location + '/' + dirName);

            $('.back-link').prop('href', '/document/get-parent-directory-content-' + dirHashName);
            $('.doc-table tr').not('.table-head').remove();
            $('#dirHashNameHidden').val(dirHashName);

            renderDirectories(files);
            renderDocuments(files);
            hideShowBackLink();
        });
    });

    backLink.click(function(event) {
        event.preventDefault();
        var url = $(this).prop('href');

        $.getJSON(url, function(files) {
            var locationElement = $('#location');
            var location = locationElement.text();
            locationElement.text(location.substring(0, location.lastIndexOf('/')));

            dirHashName = files[0].parentDirectoryHash;
            $('.back-link').prop('href', 'get-parent-directory-content-' + dirHashName);
            $('.doc-table tr').not('.table-head').remove();

            renderDirectories(files);
            renderDocuments(files);
            hideShowBackLink();
        });
    });

    function hideShowBackLink() {
        var location = $('#location').text();
        if (location.lastIndexOf('/') == -1) {
            backLink.hide();
        } else {
            backLink.show();
        }
    }

    function renderDirectories(files) {
        $.each(files, function (k, file) {
            if (file.type === 'dir') {
                loadTemplate(handlebarsPath + 'directoryRow.html', function (template) {
                    allTable.append(template(file));
                    $('.' + file.access).append(template(file));
                });
            }
        });
    }

    function renderDocuments(files) {
        $.each(files, function(k, file) {
            if (file.type === 'doc') {
                loadTemplate(handlebarsPath + 'documentRow.html', function (template) {
                    allTable.append(template(file));
                    $('.' + file.access).append(template(file));
                });
            }
        });
    }

    $('.replace-btn').click(function() {
        $.getJSON('/document/get_directories_names', function(directoriesMap) {
            $.each(directoriesMap, function(k, v) {
                $('#dirTree').append(k);
            });
        });
    });

    $('.rename-btn').click(function() {
        var docCheckBox = $('.select-doc:visible:checked');
        var dirCheckBox = $('.select-dir:visible:checked');
        if (docCheckBox.length == 1) {
            $.getJSON('/document/get_document', {docId: docCheckBox.val()}, function(document) {
                var docName = document.name;
                $('#newFileName').val(docName.substring(0, docName.lastIndexOf('.')));
            });
        } else if (dirCheckBox.length == 1) {
            $.getJSON('/document/get_directory', {dirId: dirCheckBox.val()}, function(directory) {
                $('#newFileName').val(directory.name);
            });
        }
    });

    $('#renameFile').click(function() {
        var newName = $('#newFileName').val();
        var docCheckBox = $('.select-doc:visible:checked');
        var dirCheckBox = $('.select-dir:visible:checked');
        if (docCheckBox.length == 1) {
            $.post('/document/rename_document', {docId: docCheckBox.val(), newDocName: newName}, function(document) {
                if (document !== undefined) {
                    $('.doc-table')
                        .find($('.tr-doc' + document.id))
                        .find('.document-name')
                        .html("<a href='/document/browse-'" + document.id + ">" + document.name + "</a>")
                }
            });
        } else if (dirCheckBox.length == 1) {
            $.post('/document/rename_directory', {dirId: dirCheckBox.val(), newDirName: newName}, function(directory) {
                if (directory !== undefined) {
                    $('.doc-table')
                        .find($('.tr-dir' + directory.id))
                        .find('.directory-name')
                        .html("<a href='#' id='" + directory.hashName + "' class='get-dir-content'>" +directory.name + "</a>")
                }
            });
        }
    });
});
