String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) {
        return typeof args[number - 1] != 'undefined' ? args[number - 1] : match;
    });
}

String.sprintf = function(str) {
    for (i = 1; i <= arguments.length; i++) str = str.replace(/%[b|c|d|e|u|f|o|s|x|X]/, arguments[i]);
    return str;
}

String.prototype.escapeHtml = function() {
    return this
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
