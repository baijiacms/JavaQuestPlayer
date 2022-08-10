/**

	jQuery BorderImage plugin

	* fullImage - путь к картинке в первозданном виде; путь задаётся относительно html-документа или корня сайта;
		Пр: fullImage:'skins/gfx/top_panel.png'
	* topImage, bottomImage, rightImage, leftImage, centerImage - пути к картинкам соответственно для верхней,
		нижней, правой и левой границ и центральной области;
	* topWidth, leftWidth, rightWidth, bottomWidth - толщина верхней, левой, правой, нижней рамки;
		Можно не указывать, тогда будет взято соотв. значение из CSS;
		Пр: topWidth: '10px'
	* thisElSelector - указывать, если элемент ещё не существует; скорее всего, в связке с bgOnly;
		Пр: thisElSelector:'#content'
	* bgOnly - задать true, если нужно изменить только фоновые картинки для элемента; например, этот параметр стоит задавать для подсвеченного элемента, для обычного состояния которого функция borderImage уже вызывалась;
	* transparent - задать false, если внутренняя область элемента не прозрачна;
		Понадобится использование centerImage.
	* repeatCenterX - задать true, если нужно, чтобы во внутренней области элемента фон повторялся по горизонтали.
	* repeatCenterY - задать true, если нужно, чтобы во внутренней области элемента фон повторялся по вертикали.
*/
(function($){
	$.fn.borderImage = function(options){
		var defaults = {thisElSelector:this.selector, transparent:true, repeatCenterX:false, repeatCenterY:false};
		var opts = $.extend({}, defaults, options || {});
		
		if($('html').data(opts.thisElSelector) !== undefined){
			return this.each(function(){});
		}
		options.topImage = qspMakeRetinaPath(options.topImage);
		options.leftImage = qspMakeRetinaPath(options.leftImage);
		options.rightImage = qspMakeRetinaPath(options.rightImage);
		options.bottomImage = qspMakeRetinaPath(options.bottomImage);
		options.fullImage = qspMakeRetinaPath(options.fullImage);
		if (opts.transparent === false)
		{ options.centerImage = qspMakeRetinaPath(options.centerImage); }
		if(options.bgOnly != true){
			return this.each(function (){
				var thisEl = $(this);
				if(thisEl.children('.border-emu').length != 0)
					return thisEl;
				defaults.topWidth = thisEl.css('border-top-width');
				defaults.leftWidth = thisEl.css('border-left-width');
				defaults.rightWidth = thisEl.css('border-right-width');
				defaults.bottomWidth = thisEl.css('border-bottom-width');
				var opts = $.extend({}, defaults, options || {});
				var	topMost=$('<div class="border-emu top"></div>').prependTo(thisEl),topLeft=$('<div class="border-emu top left"></div>').appendTo(thisEl),
					leftMost=$('<div class="border-emu left"></div>').prependTo(thisEl),bottomLeft=$('<div class="border-emu bottom left"></div>').appendTo(thisEl),
					bottomMost=$('<div class="border-emu bottom"></div>').prependTo(thisEl),bottomRight=$('<div class="border-emu bottom right"></div>').appendTo(thisEl),
					borderDivs=$('.border-emu'),
					rightMost=$('<div class="border-emu right"></div>').prependTo(thisEl),topRight=$('<div class="border-emu top right"></div>').appendTo(thisEl),
					centerBg=$('<div class="border-emu center"></div>').prependTo(thisEl);
				if(thisEl.css('position') == 'static'){thisEl.css({'position':'relative'});}
				thisEl.css({'border-image':'none', 'border-color':'transparent'});
				var centerWidth = opts.fullImageSizeW - parseInt(opts.leftWidth, 10) - parseInt(opts.rightWidth, 10);
				var centerHeight = opts.fullImageSizeH - parseInt(opts.topWidth, 10) - parseInt(opts.bottomWidth, 10);
				var leftRepeatYWidth = parseInt(opts.leftWidth, 10) + 1;
				var rightRepeatYWidth = parseInt(opts.rightWidth, 10) + 1;
				var topRepeatXHeight = parseInt(opts.topWidth, 10) + 1;
				var bottomRepeatXHeight = parseInt(opts.bottomWidth, 10) + 1;
				var horRepeatMode = opts.repeatCenterY ? 'repeat-y' : 'no-repeat'; // left, right
				var vertRepeatMode = opts.repeatCenterX ? 'repeat-x' : 'no-repeat'; // top, bottom
				var leftBackSize = opts.repeatCenterY ? ('' + leftRepeatYWidth + 'px ' + centerHeight + 'px') : '100% 100%'; // left, right
				var rightBackSize = opts.repeatCenterY ? ('' + rightRepeatYWidth + 'px ' + centerHeight + 'px') : '100% 100%'; // left, right
				var topBackSize = opts.repeatCenterX ? ('' + centerWidth + 'px ' + topRepeatXHeight + 'px') : '100% 100%'; // top, bottom
				var bottomBackSize = opts.repeatCenterX ? ('' + centerWidth + 'px ' + bottomRepeatXHeight + 'px') : '100% 100%'; // top, bottom
				var cornerBackSize = '' + opts.fullImageSizeW + 'px ' + opts.fullImageSizeH + 'px';
				// center
				if(opts.transparent === false){
					centerBg.css({'background-image': 'url('+opts.centerImage+')',
						'background-repeat':'no-repeat',
						'background-size':'100% 100%',
						'width':'100%',
						'height':'100%'
					});
					if (opts.repeatCenterX && !opts.repeatCenterY){
						centerBg.css({'background-image': 'url('+opts.centerImage+')',
						'background-repeat':'repeat-x',
						'background-origin':'padding-box',
						'background-size':('' + centerWidth + 'px 100%')
					});
					}
					if (!opts.repeatCenterX && opts.repeatCenterY){
						centerBg.css({'background-image': 'url('+opts.centerImage+')',
						'background-repeat':'repeat-y',
						'background-origin':'padding-box',
						'background-size':('100% ' + centerHeight + 'px')
					});
					}
					if (opts.repeatCenterX && opts.repeatCenterY){
						centerBg.css({'background-image': 'url('+opts.centerImage+')',
						'background-repeat':'repeat',
						'background-origin':'padding-box',
						'background-size':('' + centerWidth + 'px ' + centerHeight + 'px')
					});
					}
				}
				// top
				topMost.css({'top':'-'+opts.topWidth, 'left':'0px','right':'0px','height':opts.topWidth,
					'background':'url('+opts.topImage+') left top '+vertRepeatMode, 
					'background-size':topBackSize});
				// bottom
				bottomMost.css({'bottom':'-'+opts.bottomWidth, 'left':'0px','right':'0px','height':opts.bottomWidth,
					'background':'url('+opts.bottomImage+') left bottom '+vertRepeatMode, 
					'background-size':bottomBackSize});
				// left
				leftMost.css({'left':'-'+opts.leftWidth, 'top':'0px','bottom':'0px','width':opts.leftWidth,
					'background':'url('+opts.leftImage+') top left '+horRepeatMode, 
					'background-size':leftBackSize});
				// right
				rightMost.css({'right':'-'+opts.rightWidth, 'top':'0px','bottom':'0px','width':opts.rightWidth,
					'background':'url('+opts.rightImage+') top right '+horRepeatMode, 
					'background-size':rightBackSize});
				// top right
				topRight.css({'top':'-'+opts.topWidth,'right':'-'+opts.rightWidth,'height':opts.topWidth,'width':opts.rightWidth,
					'background':'url('+opts.fullImage+') right top no-repeat',
					'background-size':cornerBackSize});
				// top left
				topLeft.css({'left':'-'+opts.leftWidth,'top':'-'+opts.topWidth,'height':opts.topWidth,'width':opts.leftWidth,
					'background':'url('+opts.fullImage+') left top no-repeat',
					'background-size':cornerBackSize});
				// bottom right
				bottomRight.css({'right':'-'+opts.rightWidth,'bottom':'-'+opts.bottomWidth,'height':opts.bottomWidth,'width':opts.rightWidth,
					'background':'url('+opts.fullImage+') right bottom no-repeat',
					'background-size':cornerBackSize});
				// bottom left
				bottomLeft.css({'left':'-'+opts.leftWidth,'bottom':'-'+opts.bottomWidth,'height':opts.bottomWidth,'width':opts.leftWidth,
					'background':'url('+opts.fullImage+') left bottom no-repeat',
					'background-size':cornerBackSize});
			});
		}
		else{
			return this.each(function (){
				var thisEl = $(this), 
					opts = $.extend({}, defaults, options || {});
				$(document.head).append('<style>'+
					opts.thisElSelector+' .border-emu.top{background-image:url('+opts.topImage+')!important;}'+
					opts.thisElSelector+' .border-emu.left{background-image:url('+opts.leftImage+')!important;}'+
					opts.thisElSelector+' .border-emu.right{background-image:url('+opts.rightImage+')!important;}'+
					opts.thisElSelector+' .border-emu.bottom{background-image:url('+opts.bottomImage+')!important;}'+

					(opts.transparent ? '' : (opts.thisElSelector+' .border-emu.center{background-image:url('+opts.centerImage+')!important;}')) +
					
					opts.thisElSelector+' .border-emu.top.left{background-image:url('+opts.fullImage+')!important;}'+
					opts.thisElSelector+' .border-emu.top.right{background-image:url('+opts.fullImage+')!important;}'+
					opts.thisElSelector+' .border-emu.bottom.left{background-image:url('+opts.fullImage+')!important;}'+
					opts.thisElSelector+' .border-emu.bottom.right{background-image:url('+opts.fullImage+')!important;}'+
				'</style>');
			});
		}
		$('html').data(opts.thisElSelector, true);
	}
})(jQuery);//������ = rust.legion@gmail.com   