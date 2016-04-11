
$(document).ready(function() {
    var handlebarsPath = '/resources/js/templates/comment.html';
    var docId = $('.doc-id').val();

    $.getJSON('/document/get-comments', {docId: docId}, function(comments) {
        $.each(comments, function(k, v) {
            loadTemplate(handlebarsPath, function (template) {
                $('.commentList').prepend(template(v));
            });
        });
    });


    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        if (text != '') {
            $.post('add-comment', {text: text, docId: docId}, function(comment) {
                loadTemplate(handlebarsPath, function (template) {
                    $('.commentList').prepend(template(comment));
                });
                $('.comment-text').val('');
            });
        }
    });
});
