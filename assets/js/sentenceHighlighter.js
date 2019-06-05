var SentenceHighlighter = new (function() {
    this.paragraphSelector = [".paragraph", "DL", "UL", "OL"].join(",");

    this.highlight = function(hashComponents) {
        var section = hashComponents[0];
        var paragraph = hashComponents[1];
        var sentence = hashComponents[2];

        var sectionElement = document.querySelector(section);
        var nextSibling = sectionElement.nextElementSibling;
        var counter = 1;

        while (nextSibling) {
            if (nextSibling.matches(this.paragraphSelector) && counter++ == paragraph) {
                nextSibling.scrollIntoView();
                var sentenceElement = nextSibling.getElementsByClassName('sentence')[sentence - 1];
                sentenceElement.style.backgroundColor = 'yellow';
                break;
            }
            nextSibling = nextSibling.nextElementSibling;
        }
    }
})()

$(document).ready(function () {
    if (location.hash) {
        SentenceHighlighter.highlight(location.hash.split(':'));
    }
})
