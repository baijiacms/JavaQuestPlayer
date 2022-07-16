/**

	jQuery Retina plugin - adds support for retina images, presented by IMG tag

	Author: Troy Mcilvena (http://troymcilvena.com)
	Twitter: @mcilvena
	Date: 24 November 2011
	Version: 1.3
	
	Revision History:
		1.0 (23/08/2010)	- Initial release.
		1.1 (27/08/2010)	- Made plugin chainable
		1.2 (10/11/2010)	- Fixed broken retina_part setting. Wrapped in self executing function (closure)
		1.3 (29/10/2011)	- Checked if source has already been updated (via mattbilson)
		
		
	Rewritten by Nex 
	http://nex-otaku-en.blogspot.com
	nex@otaku.ru
	18 November 2012
*/

(function ($) {
	$.fn.retina = function (retina_part) {
		// Set default retina file part to '-2x'
		// Eg. some_image.jpg will become some_image-2x.jpg
		var settings = {'retina_part': '-2x'};
		if (retina_part) {
			jQuery.extend(settings, { 'retina_part': retina_part });
		}


		this.each(function (index, element) {
			// Проверка, что мы уже обработали этот IMG
			if ($(element).hasClass('processed-retina-img')) {
				return;
			}
			var img = $(element);
			img.addClass('processed-retina-img');
			if (!img.attr('src')) {
				return;
			}
			
			var sizes = img.attr('src').match(/-(\d+)x(\d+)(\.\w{3,4})$/);
			if (sizes === null) {
				return;
			}
			img.attr('width', sizes[1]);
			img.attr('height', sizes[2]);

			var new_image_src = img.attr('src').replace(/(.+)(\.\w{3,4})$/, "$1" + settings.retina_part + "$2");
			img.attr('src', new_image_src);
			if (img.attr('data-pressed')) {
				var new_image_src2 = img.attr('data-pressed').replace(/(.+)(\.\w{3,4})$/, "$1" + settings.retina_part + "$2");
				img.attr('data-pressed', new_image_src2);
			}
		});
		return this;
	};
})(jQuery);