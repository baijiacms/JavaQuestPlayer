jQuery(document).ready(function($) {
	/* Регистрируем обьект нашего приложения */
	window.bigKuyashMain = {};

	/* Регистрируем ивэнты */
	window.bigKuyashMain.events = {
		/* обработчик jQuery события show/hide*/
		'showHideJqHocks': function(){
			$.each(['show', 'hide'], function (i, ev) {
				var el = $.fn[ev];
				$.fn[ev] = function () {
				  this.trigger(ev);
				  return el.apply(this, arguments);
				};
			});
		},
		'inputListeners': function() {
			window.bigKuyashMain.events.showHideJqHocks();
			/*Фокус на поле ввода*/
			var $document = $(document);
			
			/* Input dialog*/
			$document.on('show', '#qsp-dialog-input', window.bigKuyashMain.views.inputFocus);
			$document.on('hide', '#qsp-dialog-input', window.bigKuyashMain.views.hideDialogBlur);
			$document.on('click', '#qsp-dialog-input .qsp-overlay', window.bigKuyashMain.controllers.inputClean);
			/* MSG dialog */
			$document.on('show', '#qsp-dialog-msg', window.bigKuyashMain.views.showDialogBlur);
			$document.on('hide', '#qsp-dialog-msg', window.bigKuyashMain.views.hideDialogBlur);
			$document.on('click', '#qsp-dialog-msg .qsp-overlay', window.bigKuyashMain.controllers.hideMsg);
			/* Error dialog */
			$document.on('show', '#qsp-dialog-error', window.bigKuyashMain.views.showDialogBlur);
			$document.on('hide', '#qsp-dialog-error', window.bigKuyashMain.views.hideDialogBlur);
			$document.on('click', '#qsp-dialog-error .qsp-overlay', window.bigKuyashMain.controllers.hideError);
			
			/* Save dialog */
			$document.on('show', '#qsp-dialog-save-slots', window.bigKuyashMain.views.showDialogBlur);
			$document.on('hide', '#qsp-dialog-save-slots', window.bigKuyashMain.views.hideDialogBlur);
			$document.on('click', '#qsp-dialog-save-slots .qsp-overlay', window.bigKuyashMain.controllers.hideSave);
		
			
		}
	}
	/* Регистрируем контроллеры  */
	window.bigKuyashMain.controllers = {
		'init': function() {
			window.bigKuyashMain.events.inputListeners();
			window.bigKuyashMain.controllers.debug();
		},
		'inputClean': function() {
			$('#qsp-dialog-input-text').val('');
			window.qspCloseInput(true);
		},
		'hideMsg': function() {
			window.qspCloseMsg();
		},
		'hideError': function() {
			window.qspCloseError();
		},
		'hideSave': function() {
			window.qspCloseSave();
		},
		'debug': function() {
			$('#skin-ui-wrapper').show();
			$(document).on('click', '.debug-btn', function() {
				console.log($('#qsp-main').html());
			});
		}
	}

	/* Регистрируем виды  */
	window.bigKuyashMain.views = {
		'inputFocus': function(e) {
			window.bigKuyashMain.views.showDialogBlur();
			window.setTimeout(function(){
				$(e.target).find('input').focus();
			}, 100);
		},
		'showDialogBlur': function() {
			$('#dialog-overlay').addClass('blur');
		},
		'hideDialogBlur': function(){
			$('#dialog-overlay').removeClass('blur');
		}
		
	}
	
	window.bigKuyashMain.controllers.init();
});

//������ = rust.legion@gmail.com   