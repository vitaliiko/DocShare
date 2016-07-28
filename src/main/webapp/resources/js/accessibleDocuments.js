
$(document).ready(function() {
    $('.add-doc').click(function() {
        $.ajax({
            url: '/api/documents/' + $(this).val() + '/add-to-my-files',
            type: 'POST',
            success: function() {

            }
        });
    });

    $('.add-dir').click(function() {
        $.ajax({
            url: '/api/directories/' + $(this).val() + '/add-to-my-files',
            type: 'POST',
            success: function() {

            }
        });
    });
});
