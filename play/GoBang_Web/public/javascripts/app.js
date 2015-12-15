$(function() {
	
	Server = new FancyWebSocket('ws://127.0.0.1:9000/websocket');

	$('.btn-lg').click(function() {
		checkField(this.id);
	});
	
	Server.bind('message', function(setToken) {
		field = setToken.split("-")[0];
		color = setToken.split("-")[1];
		updateField(field, color);
	});

	Server.connect();
});

var changeField = function(x, y) {
	//alert(x + "," +y);
//	$.ajax({
//		type: "GET",
//		url: "/gobang/set/" + x + "/" + y,
//		data: {
//			'action': 'get',
//			'message': x + y
//		},
//		dataType: "json",
//		success: function() {
//			updateField(x + "_" + y);
//		},
//	});
};

var Server;

function checkField(source) {
	Server.send('setField', source);
};

function updateField(source, color) {
	$('#' + source).css('background-color', color);
};