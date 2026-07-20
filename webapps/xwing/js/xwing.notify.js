Xwing.notify = function(opt) {
	if (typeof opt == "string") {
		opt = {
			icon : Xwing.XWING_HOME + "css/themes/" + Xwing.config.theme + "/img/notify/notify-information.png",
			delay : 2000,
			text : opt
		};
	}
	return jQuery.notify(opt);
}