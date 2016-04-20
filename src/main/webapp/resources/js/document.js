
$(document).ready(function() {
    var handlebarsPath = '/resources/js/templates/comment.html';
    var docId = $('.doc-id').val();
    var commentBox = $('.commentBox');

    $('.dropdown-toggle').dropdown().click(function(e) {
        e.preventDefault()
    });

    $.getJSON('/document/get_comments', {docId: docId}, function(comments) {
        $.each(comments, function(k, v) {
            loadTemplate(handlebarsPath, function (template) {
                $('.commentList').prepend(template(v));
            });
        });
    });

    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        if (text != '') {
            $.post('/document/add_comment', {text: text, docId: docId}, function(comment) {
                loadTemplate(handlebarsPath, function (template) {
                    $('.commentList').prepend(template(comment));
                });
                $('.comment-text').val('');
            });
        }
    });

    $('.close-comments').click(function() {
        commentBox.hide(true);
        $('.comment-box-visible').html('Show comment box');
    });

    $('.comment-box-visible').click(function(e) {
        e.preventDefault();
        if (commentBox.is(':visible')) {
            commentBox.hide(true);
            $('.comment-box-visible').html('Show comment box');
        } else {
            commentBox.show(true);
            $('.comment-box-visible').html('Hide comment box');
        }
    });

    $('.on-off-comments').click(function() {
        $.post('/document/set_comment_ability')
    });

    $('.clear-comments').click(function() {
        var docId = $('.doc-id').val();
        $.post('/document/clear_comments', {'docId': docId}, function() {
            $('.commentBox').find($('.commentText')).remove();
        });
    });
});
