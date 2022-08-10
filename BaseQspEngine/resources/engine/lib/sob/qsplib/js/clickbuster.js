/**
 *
 * "Fast Buttons" / Ghost Click Buster.
 * Based on http://code.google.com/intl/en/mobile/articles/fast_buttons.html
 * 
 *
 */
//
// START gcb_clickBuster
//

var gcb_clickPointX = Array();                                          // PRIVATE:  last X coordinate of a touch
var gcb_clickPointY = Array();                                          // PRIVATE:  last Y coordiante of a touch

/**
 *
 * add a click to the gcb_clickPointX/Y array. if nopop is specified, no timeout is set to clear the click.
 *
 * @param x     x-coordinate
 * @param y     y-coordinate
 * @param nopop if not specified, or FALSE, the coordinate is removed from the array after 5s. if TRUE, remains
 *              there forever.
 */ 
function gcb_addClick ( x, y, nopop )
{
    gcb_clickPointX.push (x);
    gcb_clickPointY.push (y);
    if (!nopop)
    {
        setTimeout (gcb_popClick, 5000);
    }
}

/**
 *
 * Removes the first item off the gcb_clickPointX/Y array.
 *
 */
function gcb_popClick ()
{
    gcb_clickPointX.splice(0,1);
    gcb_clickPointY.splice(0,1);
}

/**
 *
 * Determines if a click is within 15 pixels of a previous click in the gcb_clickPointX/Y array.
 * If it is, it is added to via gcb_addClick. Returns TRUE if the click SHOULD BE IGNORED;
 * false if it is not to be ignored.
 *
 * @param x     x-coordinate
 * @param y     y-coordinate
 * @param nopop no use here; passed on to gcb_addClick
 *
 */
function gcb_ignoreClick (x, y, nopop, noadd)
{
    for (var i=0;i<gcb_clickPointX.length;i++)
    {
        var testX = gcb_clickPointX[i];
        var testY = gcb_clickPointY[i];
		// Hack by Nex: we don't need threshold
        //if ( ( Math.abs(x - testX) < 15 ) && ( Math.abs(y - testY) < 15 ) )
        if ((x == testX) && (y == testY))
            return true;
    }
	if (!noadd)
		gcb_addClick (x, y, nopop);
    return false;
}

/**
 *
 * Attached to the document in order to prevent duplicate clicks from occuring.
 * iOS' webkit has a nasty habit of throwing duplicate onClick events. Not good.
 *
 * Also prevents (or tries to) a click if the scrollers are in movement.
 */
function gcb_clickBuster (event)
{
    //console.log ("Clicked " + event.clientX + ", " + event.clientY );
    if (gcb_ignoreClick(event.clientX, event.clientY))
    {
        //console.log ("... and ignored it.");
        event.stopPropagation();
        event.preventDefault();
    }
}
function gcb_clickBusterNoTouch (event)
{
    if (gcb_ignoreClick(event.clientX, event.clientY, false, true))
    {
        event.stopPropagation();
        event.preventDefault();
    }
}

// Это мы лучше сделаем в api.js
// document.addEventListener ( 'click', gcb_clickBuster, true );

//
// END gcb_clickBuster
//