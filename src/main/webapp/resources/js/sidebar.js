$(document).ready(function() {
    var accessibleFilesUrl = '/api/files/accessible';
    var friendsUrl = '/api/friends';
    var eventsUrl = '/api/events';
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
