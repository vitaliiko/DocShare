
$(document).ready(function() {
    var handlebarsPath = '/resources/js/templates/comment.html';
    var docId = $('.doc-id').val();
    var commentBox = $('.commentBox');

    $('.dropdown-toggle').dropdown().click(function(e) {
        e.preventDefault()
    });

    $.getJSON('/api/documents/' + docId + '/comments', function(comments) {
        $.each(comments, function(k, v) {
            loadTemplate(handlebarsPath, function (template) {
                $('.commentList').prepend(template(v));
            });
        });
    });

    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        if (text != '') {
            $.post('/api/documents/' + docId + '/comments', {text: text}, function(comment) {
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
