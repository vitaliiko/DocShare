$(document).ready(function() {
    var accessibleFilesUrl = '/document/accessible';
    var friendsUrl = '/friends/';
    var eventsUrl = '/event/browse';
    var recoverUrl = '/api/files/removed';
    var currentUrl = window.location.href;

    if (currentUrl.indexOf(accessibleFilesUrl) > 0) {
        $('#accessibleFiles').toggleClass('active');
    } else if (currentUrl.indexOf(friendsUrl) > 0) {
        $('#friends').toggleClass('active');
    } else if (currentUrl.indexOf(eventsUrl) > 0) {
        $('#events').toggleClass('active');
    } else if (currentUrl.indexOf(recoverUrl) > 0) {
        $('#recover').toggleClass('active');
    }
});
