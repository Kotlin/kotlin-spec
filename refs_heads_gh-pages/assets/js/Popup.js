function Popup(data) {
    var self = this;
    this.title_height = 29;
    this.selectors = {
        main: ".box",
        body: ".box .body",
        close: ".box .title .close",
        title: ".box .title .text",
        shadow: ".box_shadow"
    }
    this.templates = {
        wrap: "<div class=\"box_shadow\"></div><div class=\"box\" style=\"width:%d;opacity:0.0;\"><div class=\"title\"><a href=\"#\" class=\"close\"></a><div class=\"text\">%s</div></div><div class=\"body\" style=\"max-height:%d;\">%s</div></div>",
        loading: "<div class=\"loading\"><img src=\"/images/loading.gif\" alt=\"\" /></div>"
    }
    this.width = $(document.body).width() - 50 < data.width ? $(document.body).width() - 50 : (data.width || false);
    this.height = data.height || false;
    this.max_height = data.max_height || false;
    this.title = data.title || false;
    this.content = data.content || false;
    this.query = data.query || false;
    this.close_callback = data.close_callback || false;
    this.open = function() {
        $(self.selectors.main).remove();
        $(document.body).append(String.sprintf(self.templates.wrap, self.width ? (self.width + "px !important") : "auto", data.title, self.height ? ((self.height - self.title_height) + "px") : "auto", data.content ? data.content : self.templates.loading));
        self.calculate_sizes({callback: function() { $(".box").fadeTo(500, 1.0) }});
        if (self.query) self.query_execute();
    }
    this.query_execute = function() {
        $.ajax({
            url: self.query.url,
            data: self.query.data,
            dataType: self.query.data_type,
            method: "POST",
            success: function(data) {
                self.query.callback(self, data);
            }
        });
    }
    this.calculate_sizes = function(params) {
        var window_height = $(window).height();
        var window_width = $(window).width();
        var max_height = self.max_height ? (self.max_height > (window_height - 50) ? window_height - 50 : self.max_height) : window_height - 50;
        $(self.selectors.main).css({maxHeight: max_height + "px"});
        $(self.selectors.body).css({maxHeight: (max_height - self.title_height) + "px"});
        var own_height = $(self.selectors.main).height();
        var own_width = $(self.selectors.main).width();
        $(self.selectors.main).css({top: Math.round(window_height / 2 - own_height / 2) + "px", left: Math.round(window_width / 2 - own_width / 2) + "px"});
        if (params && params.callback) params.callback();
    }
    this.clean = function() {
        $(self.selectors.main).remove();
        $(self.selectors.close).off("click");
        $(window).off("resize");
    }
    this.close = function() {
        $(self.selectors.main).remove();
        $(self.selectors.shadow).remove();
        if (self.close_callback) self.close_callback();
    }
    this.set_title = function(new_title) {
        $(self.selectors.title).html(new_title);
    }
    this.set_handler = function() {
        $(self.selectors.close).on("click", function() { self.close(); return false; });
        $(self.selectors.shadow).on("click", function() { self.close(); return false; });
        $(window).on("resize", function() { self.calculate_sizes() });
    }
    this.constructor = function() {
        self.open();
        self.set_handler();
    }()
}

function Alert(data) {
    var self = this;
    this.title_height = 29;
    this.selectors = {
        main: ".alert",
        body: ".body",
        close: ".title .close",
        main_by_id: ""
    }
    this.width = data.width || false;
    this.height = data.height || false;
    this.title = data.title || false;
    this.content = data.content || false;
    this.service = data.service;
    this.close_callback = data.close_callback || false;
    this.number = null;
    this.distance_alerts = 15;
    this.templates = {
        wrap: "<div class=\"alert %s\" id=\"alert_%d\" style=\"width:%d;bottom:%dpx;opacity:0.0;\"><div class=\"title\"><a href=\"#\" class=\"close\"></a><div class=\"text\">%s</div></div><div class=\"body\" style=\"height:%d;\">%s</div></div>",
        selector_by_id: "#alert_%d"
    }
    this.open = function() {
        var bottom_padding = self.distance_alerts;
        for(var i = 0; i < self.number; i++) bottom_padding += $(String.sprintf(self.templates.selector_by_id, i)).height() + self.distance_alerts;
        $(document.body).append(String.sprintf(self.templates.wrap, self.service ? self.service.name : "default", self.number, self.width ? (self.width + "px !important") : "auto", bottom_padding - 30, self.title, self.height ? ((self.height - self.title_height) + "px") : "auto", self.content));
        $(self.selectors.main_by_id).animate({bottom: "+=" + 30, opacity: 1.0}, 500);
    }
    this.assign_number = function() {
        for(var i = 0; i < Alert.numbers.length; i++) {
            if (Alert.numbers[i] === false) {
                Alert.numbers[i] = i;
                return i;
            }
        }
        var new_number = Alert.numbers.length;
        Alert.numbers[new_number] = new_number;
        return new_number;
    }
    this.destroy = function() {
        Alert.numbers[self.number] = false;
        $(self.selectors.main_by_id).remove();
        $(self.selectors.main_by_id).find(self.selectors.close).off("click");
    }
    this.close = function() {
        $(self.selectors.main_by_id).animate({bottom: "-=" + (self.height + 15), opacity: 0.0}, 500, self.destroy);
        if (self.close_callback) self.close_callback();
    }
    this.set_handler = function() {
        $(self.selectors.main_by_id).find(self.selectors.close).on("click", function() { self.close(); return false; });
    }
    this.constructor = function() {
        self.number = self.assign_number();
        self.selectors.main_by_id = String.sprintf(self.templates.selector_by_id, self.number);
        self.open();
        self.set_handler();
    }()
}

Alert.numbers = [ ];
