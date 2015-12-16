$(function() {
	
	Server = new FancyWebSocket('ws://127.0.0.1:9000/websocket');

	$('.btn-lg').click(function() {
		command(this.id);
	});
	
	$('#new_round').click(function() {
		command("newRound");
	});
	
	Server.bind('message', function(field) {
		var jsonField = JSON.parse(field);

		for(var i = 0; i < jsonField.length; ++i) {
			for(var j = 0; j < jsonField.length; ++j) {
				if(jsonField[i][j].name != 'none') {
					updateField(jsonField[i][j].id, jsonField[i][j].name);
				} else {
					updateField(jsonField[i][j].id, 'white');
				}
			}
		}
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

//function checkField(source) {
//	jsonSource = {"command": source};
////	console.log(JSON.stringify(jsonSource));
//	Server.send('setField', JSON.stringify(jsonSource));
//};

function command(command) {
	jsonCommand = {"command": command};
	Server.send('command', JSON.stringify(jsonCommand));
};

function updateField(source, color) {
	$('#' + source).css('background-color', color);
};