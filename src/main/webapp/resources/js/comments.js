
$(document).ready(function() {
    var handlebarsPath = '/resources/js/templates/comment.html';
    var docId = $('.doc-id').val();
    var token = $('.token').val();
    var location = $('.doc-location').val();
    var commentBox = $('.commentBox');
    var commentsResourceURL;

    if (location !== undefined) {
        commentsResourceURL = '/api/documents/' + docId + '/comments';
        $.getJSON(commentsResourceURL, function(comments) {
            renderComments(comments);
        });
    } else {
        commentsResourceURL = '/api/links/documents/comments';
        $.getJSON(commentsResourceURL + '?token=' + token, function(comments) {
            renderComments(comments);
        });
    }

    function renderComments(comments) {
        $.each(comments, function(k, v) {
            loadTemplate(handlebarsPath, function (template) {
                $('.commentList').prepend(template(v));
            });
        });
    }

    $('.dropdown-toggle').dropdown().click(function(e) {
        e.preventDefault()
    });

    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        if (text != '') {
            $.ajax({
                url: commentsResourceURL,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    text: text,
                    token: token
                }),
                success: function(comment) {
                    loadTemplate(handlebarsPath, function (template) {
                        $('.commentList').prepend(template(comment));
                    });
                    $('.comment-text').val('');
                }
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

    $('.off-comments').click(function() {
        setCommentAbility('false');
    });

    $('.on-comments').click(function() {
        setCommentAbility('true');
    });

    function setCommentAbility(ability) {
        $.post('/api/documents/' + docId + '/comment-ability', {'abilityToComment': ability}, function() {
            location.reload();
        });
    }

    $('.clear-comments').click(function() {
        $.ajax({
            url: '/api/documents/' + docId + '/comments',
            type: 'DELETE',
            success: function() {
                $('.commentBox').find($('.commentText')).remove();
            }
        });
    });
});
