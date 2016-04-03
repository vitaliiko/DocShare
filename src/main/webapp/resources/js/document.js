
$(document).ready(function() {
    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        var docId = $('.doc-id').val();
        $.ajax({
            url: 'document/add-comment',
            contentType: 'json',
            data: {text: text, docId: docId},
            success: function(comment) {
                $('.commentList').append("<li>" +
                    "<div class='commentText'>" +
                    "<p class=''><strong>" + comment.owner + "</strong></p>" +
                    "<p class=''>" + comment.text +
                    "</p> <span class='date sub-text'>" + comment.date +
                    "</span>" +
                    "</div>" +
                    "</li>");
            }
        });
    });
});
