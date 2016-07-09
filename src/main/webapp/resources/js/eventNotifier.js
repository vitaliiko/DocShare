
$(document).ready(function() {

    var countLabel = $('#unreadEventsCount');

    checkUnreadEvents();

    setInterval(checkUnreadEvents, 5000);

    function checkUnreadEvents() {
        $.ajax({
            url: '/api/events/unread-count',
            type: 'GET',
            success: function(count) {
                if (count > 0) {
                    countLabel.text('(' + count + ' new)');
                } else {
                    countLabel.text('');
                }
            }
        });
    }
});
