// jQuery Context Menu Plugin
//
// Version 1.01 (rewritten by Nex)
//
// Cory S.N. LaViska
// A Beautiful Site (http://abeautifulsite.net/)
//
// More info: http://abeautifulsite.net/2008/09/jquery-context-menu-plugin/
//
// Terms of Use
//
// This plugin is dual-licensed under the GNU General Public License
//   and the MIT License and is copyright A Beautiful Site, LLC.
//

function showContextMenu(o, callback) 
{
	// Defaults
	if( o.menu == undefined ) return false;
	if( o.item == undefined ) return false;
	if( o.x == undefined ) return false;
	if( o.y == undefined ) return false;
	if( o.inSpeed == undefined ) o.inSpeed = 150;
	if( o.outSpeed == undefined ) o.outSpeed = 75;
	// 0 needs to be -1 for expected results (no fade)
	if( o.inSpeed == 0 ) o.inSpeed = -1;
	if( o.outSpeed == 0 ) o.outSpeed = -1;

	var menu = $('#' + o.menu);
	var item = '.' + o.item;
	
	var x, y;
	x = o.x;
	y = o.y;
	
	// Show the menu
	$(document).unbind('click');
	$(menu).css({ top: y, left: x }).fadeIn(o.inSpeed);
	// Hover events
	$(menu).find('A').mouseover( function() {
		$(menu).find(item + '.hover').removeClass('hover');
		$(this).parent().addClass('hover');
	}).mouseout( function() {
		$(menu).find(item + '.hover').removeClass('hover');
	});
	
	// Keyboard
	$(document).keypress( function(e) {
		switch( e.keyCode ) {
			case 38: // up
				if( $(menu).find(item + '.hover').size() == 0 ) {
					$(menu).find(item + ':last').addClass('hover');
				} else {
					$(menu).find(item + '.hover').removeClass('hover').prevAll(item + ':not(.disabled)').eq(0).addClass('hover');
					if( $(menu).find(item + '.hover').size() == 0 ) $(menu).find(item + ':last').addClass('hover');
				}
			break;
			case 40: // down
				if( $(menu).find(item + '.hover').size() == 0 ) {
					$(menu).find(item + ':first').addClass('hover');
				} else {
					$(menu).find(item + '.hover').removeClass('hover').nextAll(item + ':not(.disabled)').eq(0).addClass('hover');
					if( $(menu).find(item + '.hover').size() == 0 ) $(menu).find(item + ':first').addClass('hover');
				}
			break;
			case 13: // enter
				$(menu).find(item + '.hover A').trigger('click');
			break;
			case 27: // esc
				$(document).trigger('click');
			break
		}
	});
	
	// When items are selected
	$(menu).find('A').unbind('click');
	$(menu).find(item + ':not(.disabled) A').click( function() {
		$(document).unbind('click').unbind('keypress');
		$(".contextMenu").hide();
		// Callback
		if( callback ) callback($(this).attr('href').substr(1));
		return false;
	});
	
	// Hide bindings
	setTimeout( function() { // Delay for Mozilla
		$(document).click( function() {
			$(document).unbind('click').unbind('keypress');
			$(menu).fadeOut(o.outSpeed);
			// Callback
			if( callback ) callback(-1);
			return false;
		});
	}, 0);
}