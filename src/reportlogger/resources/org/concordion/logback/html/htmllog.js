<script type="text/javascript">
/*
 * TODO This may allow fixed header at top of page - works with chrome but firefox and edge body cells loose their
 * width.  Possibly setting body cell min-width to header width during onload, then header width during onresize
 * might do the trick.
 
//window.onresize = function() {
window.onload = function() {
	var table = document.getElementsByTagName("table")[0];
	var thead = table.getElementsByTagName("thead")[0];
	var htr = thead.getElementsByTagName("tr")[0];
	
	htr.style.position = "fixed";
	htr.style.zIndex = "1000";
	
	var tbody = table.getElementsByTagName("tbody")[0];
	var btr = tbody.getElementsByTagName("tr")[0];
	
	for(var i = 0; i < btr.cells.length; i++) {
	var w1 = btr.cells[i].getBoundingClientRect().width;
	var w2 = btr.cells[i].offsetWidth;
		htr.cells[i].width = w2 + 'px';
	}
}
*/

function hasClass(element, cls) {
	return element.classList ? element.classList.contains(cls) : (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;
}

function addClass(element, cls) {
	if (element.classList) {
		element.classList.add(cls);
	} else {
		if (!hasClass(element, cls)) {
			element.className += " " + cls;
		}
	}
}

function removeClass(element, cls) {
	if (element.classList) {
		element.classList.remove(cls);
    } else {
        var classes = element.className.split(" ");
        classes.splice(classes.indexOf(cls), 1);
        element.className = classes.join(" ");
    }
}

/* Stack Trace Toggling */
function getElementById(id) {
	var element;

	if (document.getElementById) { // standard
		return document.getElementById(id);
	} else if (document.all) { // old IE versions
		return document.all[id];
	} else if (document.layers) { // nn4
		return document.layers[id];
	}
	alert("Sorry, but your web browser is not supported by Concordion.");
}

function isVisible(element) {
	return element.style.display;
}

function makeVisible(element) {
	element.style.display = "block";
}

function makeInvisible(element) {
	element.style.display = "";
}

function toggleStackTrace(stackTraceNumber) {
	var stackTrace = getElementById("stackTrace" + stackTraceNumber);
	var stackTraceButton = getElementById("stackTraceButton" + stackTraceNumber);
	if (isVisible(stackTrace)) {
		makeInvisible(stackTrace);
		stackTraceButton.value = "View Stack";
	} else {
		makeVisible(stackTrace);
		stackTraceButton.value = "Hide Stack";
	}
}

/* Image popup */
function showScreenPopup(src) {
	var img = document.getElementById('ScreenshotPopup');
	img.src = src.src

	var scrollTop = Math.max(document.body.scrollTop, document.documentElement.scrollTop);
	var scrollLeft = Math.max(document.body.scrollLeft, document.documentElement.scrollLeft);
	var viewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
	var viewportHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
	var scrollbarWidth = viewportWidth - document.body.offsetWidth;

	var srcRect = src.getBoundingClientRect();
	var imgRect = img.getBoundingClientRect();
	var naturalWidth = (img.naturalWidth || 0);
	var naturalHeight = (img.naturalHeight || 0);

	var above = srcRect.top > (viewportHeight - srcRect.top - srcRect.height);
	var posTop = 0;
	var posLeft = 0;

	if (above) {
		var height = Math.min(naturalHeight, srcRect.top - 10)
		posTop = scrollTop + srcRect.top - height;

		img.style.height = height + "px";
		img.style.width = 'auto';

		if (img.width > Math.min(naturalWidth, viewportWidth)) {
			img.style.height = 'auto';
			img.style.width = Math.min(naturalWidth, viewportWidth) + "px";

			posTop = scrollTop + srcRect.top - img.height;
		}
	} else {
		var height = Math.min(naturalHeight, viewportHeight - srcRect.top - srcRect.height - 10)
		posTop = scrollTop + srcRect.top + srcRect.height;

		img.style.height = height + "px";
		img.style.width = 'auto';

		if (img.width > Math.min(naturalWidth, viewportWidth)) {
			img.style.height = 'auto';
			img.style.width = Math.min(naturalWidth, viewportWidth) + "px";
		}
	}

	var posLeft = srcRect.left + scrollLeft + 1;

	if (posLeft + img.width > scrollLeft + viewportWidth - scrollbarWidth) {
		posLeft = scrollLeft + viewportWidth - img.width - scrollbarWidth;
	}
	if (posLeft < scrollLeft) {
		posLeft = scrollLeft;
	}

	img.style.left = posLeft + "px";
	img.style.top = posTop + "px";
	img.style.visibility = 'visible';   
}

function hideScreenPopup() {
	document.getElementById('ScreenshotPopup').style.visibility = 'hidden';
}

/* Log Filtering */
function filterDebug(cb) {
	doFilter("debug", cb.checked);
}
function filterTrace(cb) {
	doFilter("trace", cb.checked);
}

function doFilter(className, checked) {
	var displaySetting = checked ? "" : "none";
	var borderSetting = checked ? "1px solid #f0f0f0" : "1px dotted black";

	var scrollToElement = getFirstVisibleRow(className);
	
	var all = document.querySelectorAll("TR." + className);
	for (var i = 0; i < all.length; i++) {
		var prevrow = all[i].previousElementSibling;
		if (prevrow != null && !hasClass(prevrow, className)) {
			prevrow.style.borderBottom = borderSetting;
		}

		all[i].style.display = displaySetting;
	}
	
	// Attempt to keep the currently selected row visible
	if (scrollToElement) {
		scrollToElement.scrollIntoView();
	}
}

function getFirstVisibleRow(className) { 
	var element = null;
	
	if (document.caretPositionFromPoint) {
		var range = document.caretPositionFromPoint(0, 5);
		element = range.offsetNode.parentElement;
		
	} else if (document.caretRangeFromPoint) {
		var range = document.caretRangeFromPoint(0, 5);
		element = range.startContainer.parentNode;
		
	} else if (document.elementFromPoint) {
		element = document.elementFromPoint(100, 5).parentElement;
	}
 	
	while (element != null && (element.tagName.toLowerCase() != 'tr' || hasClass(element, className))) {
		if (element.tagName.toLowerCase() == 'tr') {
			element = element.previousElementSibling;
		} else {
			element = element.parentElement;
		}
	}
	
	return element;
}

/* Expand / Collapse embedded content */
function toggleContent(el) {
	var resizeEl = el.parentElement;
	var isCollapsed = el.text == "Collapse";

	while (resizeEl != null) {
		if (hasClass(resizeEl, "resizeable")) {
			if (isCollapsed) {
				resizeEl.style.maxHeight = "100px";
				resizeEl.style.overflow = "hidden";
				el.text = "Expand";

			} else {
				resizeEl.style.maxHeight = viewport().height - 50 + 'px';
				//console.log(resizeEl.style.maxHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0) * .8);
				console.log(viewport());
				resizeEl.style.overflow = "auto";
				el.text = "Collapse";
			}

			// Resized element
			resizeEl = resizeEl.firstElementChild;

			if (resizeEl.tagName.toLowerCase() == "object") {
				if (isCollapsed) {
					resizeEl.style.height = 'auto';
					resizeEl.style.width = 'auto';
				} else {   
					resizeEl.style.height = viewport().height - 300 + 'px';
					resizeEl.style.width = viewport().width - 300 + 'px';                                                                                                                                                                      
				}
			} else {
				if (isCollapsed) {
					addClass(resizeEl, "fadeout");
				} else {
					removeClass(resizeEl, "fadeout");
				}
			}			

			break;
		}

		resizeEl = resizeEl.nextElementSibling;
	}
}

function viewport() {
	var e = window, a = 'inner';
	if (!('innerWidth' in window )) {
		a = 'client';
		e = document.documentElement || document.body;
	}
	return { width : e[ a+'Width' ] , height : e[ a+'Height' ] };
}


</script>