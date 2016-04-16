
$(document).ready(function() {

    checkUnreadEvents();

    setInterval(checkUnreadEvents, 5000);

    function checkUnreadEvents() {
        $.ajax({
            url: '/event/get_unread_events_count',
            type: 'GET',
            success: function(count) {
                if (count > 0) {
                    $('#unreadEventsCount').text(count);
                }
            }
        });
    }
});