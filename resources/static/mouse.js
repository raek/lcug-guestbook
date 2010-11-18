// Simple follow the mouse script

var divName = 'followdiv'; // div that is to follow the mouse
                       // (must be position:absolute)
var offX = 15;          // X offset from mouse position
var offY = 15;          // Y offset from mouse position

function mouseX(evt) {
    if (!evt)
	evt = window.event;

    if (evt.pageX)
	return evt.pageX;
    else if (evt.clientX)
	return evt.clientX + (document.documentElement.scrollLeft
			      ? document.documentElement.scrollLeft
			      : document.body.scrollLeft);
    else
	return 0;
}

function mouseY(evt) {
    if (!evt)
	evt = window.event;

    if (evt.pageY)
	return evt.pageY;
    else if (evt.clientY)
	return evt.clientY + (document.documentElement.scrollTop
			      ? document.documentElement.scrollTop
			      : document.body.scrollTop);
    else
	return 0;
}

var count = 0;

function follow(evt) {
    if (document.getElementById) {
	var obj = document.getElementById(divName).style;
	obj.visibility = 'visible';
	var x = parseInt(mouseX(evt));
	var y = parseInt(mouseY(evt));
	var r = 100 + 100 * Math.sin(count/100);
	obj.left = (r * Math.cos(count/10) + x + offX) + 'px';
	obj.top = (r * Math.sin(count/10) + y + offY) + 'px';
	count += 1;
    }
}

document.onmousemove = follow;
