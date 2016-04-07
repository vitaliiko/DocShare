
$(document).ready(function() {
    $('.add-comment').click(function() {
        var text = $('.comment-text').val();
        var docId = $('.doc-id').val();
        if (text != '') {
            $.ajax({
                url: 'add-comment',
                contentType: 'json',
                data: {text: text, docId: docId},
                success: function (comment) {
                    $('.commentList').append("<li>" +
                        "<div class='commentText'>" +
                        "<p class=''><strong>" + comment.owner.firstName + ' ' + comment.owner.lastName +
                        "</strong></p>" +
                        "<p class=''>" + comment.text +
                        "</p> <span class='date sub-text'>" +
                        "<fmt:formatDate type='both' dateStyle='long' timeStyle='short' value='" + comment.date +
                        "'/></span>" +
                        "</div> </li>");
                    $('.comment-text').val('');
                }
            });
        }
    });
});
