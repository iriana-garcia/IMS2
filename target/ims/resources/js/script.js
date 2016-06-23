
function removeDecimalPlaces(event) {

	event.value = event.value.trim().replace(/[^\d\.]/g, '');

}

function removeAllDecimalPlaces(event) {
	event.value = event.value.trim().replace(/[^\d\.]/g, '');
	if (event.value.indexOf('.') != -1) {
		var splitfield = event.value.split(".");
		var finalPlace = splitfield[0];
		event.value = finalPlace;
	}
}

function onlyNumber(evt) {
	if (event.value.indexOf('.') != -1) {
		var splitfield = event.value.split(".");
		return false;
	}
	return true;
}
