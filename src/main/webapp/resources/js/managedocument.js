$(document).ready(function() {

    var content = $('.content');
    var fileAccessAttribute;
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
    var dirHashName = 'root';
    var parentDirHashName;
    var dangerAlert = $('.alert-danger');
    var successAlert = $('.alert-success');
    var alertText = $('.alert-text');

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

    function saveSelectedFilesIds() {
        $('.select-doc:visible:checked').each(function() {
            var id = $(this).val();
            docIds.push(id);
            tableRows.push($('.tr-doc' + id));
        });
        $('.select-dir:visible:checked').each(function() {
            var id = $(this).val();
            dirIds.push(id);
            tableRows.push($('.tr-dir' + id));
        });
    }

    changeTab();
    allTable.show(true);
    setSelectionStyle($('.all-href'));
    backLink.hide();
    $('.add-action-btn').hide();
    $.getJSON('/api/directories/root/content', function(files) {
        renderDirectories(files);
        renderDocuments(files);
    });
    $('.form-search').show();

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
        files.append('file', $('#file').files[0]);
        if (files.length) {
            $.ajax({
                url: 'upload',
                type: 'POST',
                data: {files: files},
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
            url: '/api/files/move-to-trash',
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

        var getUrl = '/api/documents/' + $(this).val();
        shareUrl = getUrl + '/share';
        $.getJSON(getUrl + '/access', function(document) {
            fileAccessAttribute = document.attribute;
            makeBoxesChecked(document.readers, $('.reader-check-box'));
            makeBoxesChecked(document.editors, $('.editor-check-box'));
            makeBoxesChecked(document.readerGroups, $('.readers-group-check-box'));
            makeBoxesChecked(document.editorGroups, $('.editors-group-check-box'));
            $('.share-modal-title').text('Share ' + document.name);
            $('#' + document.attribute).prop('checked', true);
        });
    });

    content.on('click', '.share-dir-btn', function() {
        clearModalWindow();
        makeBoxesUnchecked();
        $('.group-check-box').hide();

        var getUrl = '/api/directories/' + $(this).val();
        shareUrl = getUrl + '/share';
        $.getJSON(getUrl + '/access', function(directory) {
            fileAccessAttribute = directory.attribute;
            makeBoxesChecked(directory.readers, $('.reader-check-box'));
            makeBoxesChecked(directory.readerGroups, $('.readers-group-check-box'));
            $('.share-modal-title').text('Share ' + directory.name);
            $('#' + directory.attribute).prop('checked', true);
        });
    });

    function makeBoxesChecked(readers, checkBoxes) {
        var ids = [];
        $.each(readers, function (k, v) {
            ids.push(v.id);
        });
        $(checkBoxes).each(function (k, v) {
            var isChecked = $.inArray(parseInt(v.value), ids) != -1;
            $(this).prop('checked', isChecked);
        });
    }

    $('#shareDocument').click(function() {
        var readers = [];
        var readerGroups = [];
        var editors = [];
        var editorGroups = [];
        var access = $('input[name=access]:checked').val();
        $('.readers-group-check-box:checked').each(function (k, v) {
            readerGroups.push(v.value);
        });
        $('.reader-check-box:checked').each(function (k, v) {
            readers.push(v.value);
        });
        $('.editors-group-check-box:checked').each(function (k, v) {
            editorGroups.push(v.value);
        });
        $('.editor-check-box:checked').each(function (k, v) {
            editors.push(v.value);
        });
        $.ajax({
            url: shareUrl,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                access: access,
                readers: readers,
                readerGroups: readerGroups,
                editors: editors,
                editorGroups: editorGroups
            }),
            success: function (file) {
                clearModalWindow();
                if (fileAccessAttribute !== file.attribute) {
                    if (file.type === 'DOCUMENT') {
                        $('.' + fileAccessAttribute).find($('.tr-doc' + file.id)).remove();
                        loadTemplate(handlebarsPath + 'documentRow.html', function (template) {
                            $('.' + file.attribute).append(template(file));
                        });
                    } else {
                        $('.' + fileAccessAttribute).find($('.tr-dir' + file.id)).remove();
                        loadTemplate(handlebarsPath + 'directoryRow.html', function (template) {
                            $('.' + file.attribute).append(template(file));
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
            url: '/api/directories/' + dirHashName + '/make-dir',
            type: 'POST',
            data: {dirName: dirName},
            success: function (directory) {
                loadTemplate(handlebarsPath + 'directoryRow.html', function(template) {
                    var html = template(directory);
                    allTable.append(html);
                    privateTable.append(html);
                });
                dangerAlert.hide(true);
            },
            error: function() {
                dangerAlert.show(true);
                alertText.text("Directory with such name already exist");
            }
        });
    });

    content.on('click', '.get-dir-content', function(event) {
        event.preventDefault();
        var dirName = $(this).text();
        dirHashName = this.id;
        var url = '/api/directories/' + dirHashName + '/content';

        $.getJSON(url, function(directoryContent) {
            var locationElement = $('#location');
            var location = locationElement.text();
            locationElement.text(location + '/' + dirName);

            parentDirHashName = directoryContent.parentDirHashName;
            $('.back-link').prop('href', '/api/directories/' + parentDirHashName + '/content');
            $('.doc-table tr').not('.table-head').remove();
            $('#uploadingForm').attr('action', '/api/directories/' + dirHashName + '/documents/upload');

            renderDirectories(directoryContent);
            renderDocuments(directoryContent);
            hideShowBackLink();
        });
        if ($('.add-action-btn').is(':visible')) {
            copyReplaceMode();
        }
    });

    backLink.click(function(event) {
        event.preventDefault();
        var url = $(this).prop('href');

        $.getJSON(url, function(directoryContent) {
            var locationElement = $('#location');
            var location = locationElement.text();
            locationElement.text(location.substring(0, location.lastIndexOf('/')));

            dirHashName = directoryContent.dirHashName;
            parentDirHashName = directoryContent.parentDirHashName;
            $('.back-link').prop('href', '/api/directories/' + parentDirHashName + '/content');
            $('.doc-table tr').not('.table-head').remove();
            $('#uploadingForm').attr('action', '/api/directories/' + dirHashName + '/documents/upload');

            renderDirectories(directoryContent);
            renderDocuments(directoryContent);
            hideShowBackLink();
        });
        if ($('.add-action-btn').is(':visible')) {
            copyReplaceMode();
        }
    });

    $('#searchButton').click(function() {
        var searchName = $('#filesSearch').val();
        $.getJSON('/api/files/search', {searchName: searchName}, function(files) {
            var locationElement = $('#location');
            var location = locationElement.text();
            locationElement.text(location.substring(0, location.indexOf('/')));

            dirHashName = files[0].parentDirectoryHash;
            $('.back-link').prop('href', '/api/directories/' + dirHashName + '/content');
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

    function renderDirectories(directoryContent) {
        $.each(directoryContent.files, function (k, file) {
            if (file.type === 'DIRECTORY') {
                loadTemplate(handlebarsPath + 'directoryRow.html', function (template) {
                    allTable.append(template(file));
                    $('.' + file.attribute).append(template(file));
                });
            }
        });
    }

    function renderDocuments(directoryContent) {
        $.each(directoryContent.files, function(k, file) {
            if (file.type === 'DOCUMENT') {
                loadTemplate(handlebarsPath + 'documentRow.html', function (template) {
                    allTable.append(template(file));
                    $('.' + file.attribute).append(template(file));
                });
            }
        });
    }

    $('.replace-btn').click(function() {
        saveSelectedFilesIds();
        makeBoxesUnchecked();
        copyReplaceMode();
        $('.replace-message').show(true);
        $('.move-here-btn').show(true);
    });

    $('.copy-btn').click(function() {
        saveSelectedFilesIds();
        makeBoxesUnchecked();
        copyReplaceMode();
        $('.copy-message').show(true);
        $('.copy-here-btn').show(true);
    });

    $('.move-here-btn').click(function() {
        $.ajax({
            url: '/api/files/replace',
            type: 'POST',
            data: {'docIds[]': docIds, 'dirIds[]': dirIds, "destinationDirHash": dirHashName},
            success: function() {
                location.reload();
            }
        });
    });

    $('.copy-here-btn').click(function() {
        $.ajax({
            url: '/api/files/copy',
            type: 'POST',
            data: {'docIds[]': docIds, 'dirIds[]': dirIds, "destinationDirHash": dirHashName},
            success: function() {
                location.reload();
            }
        });
    });

    function copyReplaceMode() {
        $(".select").attr("disabled", true);
        $(".select-all").attr("disabled", true);
        $('.table-btn').attr("disabled", true);
        $('.cancel-btn').show(true);
    }

    $('.cancel-btn').click(function() {
        $(".select").attr("disabled", false);
        $(".select-all").attr("disabled", false);
        $('.table-btn').attr("disabled", false);
        $('.replace-message').hide(true);
        $('.copy-message').hide(true);
        $('.add-action-btn').hide();
    });

    $('#renameFile').click(function() {
        var newName = $('#newFileName').val();
        var docCheckBox = $('.select-doc:visible:checked');
        var dirCheckBox = $('.select-dir:visible:checked');
        if (docCheckBox.length == 1) {
            $.ajax({
                url: '/api/documents/' + docCheckBox.val() + '/rename',
                type: 'POST',
                data: {newDocName: newName},
                success: function(document) {
                    if (document !== undefined) {
                        $('.doc-table')
                            .find($('.tr-doc' + document.id))
                            .find('.document-name')
                            .html("<a href='/api/documents/'" + document.id + ">" + document.name + "</a>")
                    }
                    dangerAlert.hide(true);
                },
                error: function() {
                    dangerAlert.show(true);
                    alertText.text("File with such name already exist");
                }
            });
        } else if (dirCheckBox.length == 1) {
            $.ajax({
                url: '/api/directories/' + dirCheckBox.val() + '/rename',
                type: 'POST',
                data: {newDirName: newName},
                success: function(directory) {
                    if (directory !== undefined) {
                        $('.doc-table')
                            .find($('.tr-dir' + directory.id))
                            .find('.directory-name')
                            .html("<a href='#' id='" + directory.hashName + "' class='get-dir-content'>" + directory.name + "</a>")
                    }
                    dangerAlert.hide(true);
                },
                error: function() {
                    dangerAlert.show(true);
                    alertText.text("Directory with such name already exist");
                }
            });
        }
        $('#newFileName').val('');
    });
});
