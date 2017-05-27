$(document).ready(function() {
    $('.notification').each(function(k,v) {
        var thing = $(this);
        var text  = thing.data('text');
        var level = thing.data('level');
        alertify.notify(text, level, 0);
    });
});
