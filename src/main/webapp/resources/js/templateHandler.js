function loadTemplate(path, callback) {
    var source, template;
    $.ajax({
        url: path,
        success: function(data) {
            source = data;
            template = Handlebars.compile(source);
            if (callback && typeof callback === 'function') {
                callback(template);
            }
        }
    });
}