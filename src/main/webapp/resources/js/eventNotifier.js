
$(document).ready(function() {

    var countLabel = $('#unreadEventsCount');

    checkUnreadEvents();

    setInterval(checkUnreadEvents, 5000);

    function checkUnreadEvents() {
        $.ajax({
            url: '/event/get_unread_events_count',
            type: 'GET',
            success: function(count) {
                if (count > 0) {
                    countLabel.text(count);
                } else {
                    countLabel.text('');
                }
            }
        });
    }
});
