$(function() {
	
});


var changeField = function(x, y) {
	//alert(x + "," +y);
	$.ajax({
		type: "GET",
		url: "/gobang/set/" + x + "/" + y,
		data: {
			'action': 'get',
			'message': x + y
		},
		dataType: "json",
		success: function() {
			updateField(x + "_" + y);
		},
	});
};

function updateField(source) {
	$('#' + source).css('background-color', 'black');
};