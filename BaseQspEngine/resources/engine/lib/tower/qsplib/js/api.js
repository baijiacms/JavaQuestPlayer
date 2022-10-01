var qsp_iScroll_main = null;
var qsp_iScroll_acts = null;
var qsp_iScroll_vars = null;
var qsp_iScroll_objs = null;
var qsp_iScroll_msg = null;

var qspDialogOpened = true;
var qspCurDialog = "";
var qspSystemMenuId = "#qsp-dialog-system-menu";
var qspUiBlocked = true;
var qspSaveSlotsModeOpen = true;
var qspGameSkin = null;
var qspActsListItemFormat = "<table><tr><td><img src='%IMAGE%'/></td><td style='width:100%;'>%TEXT%</td></tr></table>";
var qspObjsListItemFormat = "<table><tr><td><img src='%IMAGE%'/></td><td style='width:100%;'>%TEXT%</td></tr></table>";
var qspObjsListSelItemFormat = "<table><tr><td><img src='%IMAGE%'/></td><td style='width:100%;color:#0000FF;'>%TEXT%</td></tr></table>";
var qspMenuListItemFormat = "<table><tr><td><img src='%IMAGE%'/></td><td style='width:100%;'>%TEXT%</td></tr></table>";
var qspMainContent = null;
var qspMainViewNeedScroll = false;
var qspMainViewWasScrolled = false;
var qspSelectedObjectIndex = -1;
var qspInvObjs = null;
var qspSplashHidden = false;
var qspPreloadingImageArray = [];
var qspIsAndroid = false;
var qspIsIos = false;
var qspIsDesktop = false;
var qspScreenHD = false;
var qspLocalGames = null;

var qspSlotTitleText = "Слот ";
var qspSlotEmptyText = " (пусто)";

var qspHandlerViewClick = function() { qspCloseView(); };
var qspHandlerSystemMenuOverlayClick = function() { qspCloseSystemMenu(); };
var qspHandlerSaveSlotsOverlayClick = function() { qspCloseSaveSlots(-1); };

var qspLastPressedButton = null;

var qspMouseX = 0;
var qspMouseY = 0;

function qspInitApi() {
	qspInitScrolls();

	qspDialogOpened = false;
	qspCurDialog = "";
	qspUiBlocked = false;
	qspMainViewWasScrolled = false;
	qspMainViewNeedScroll = false;
	qspSetDialogs();
	
	$(document.body).prepend('<div id="qsp-js-sandbox" style="display:none;"></div>');

    $(document).bind("mousedown touchstart MozTouchDown", function(e) {
              // Небольшой трюк, чтобы словить событие не только от мыши, но и от нажатия тачскрина
              var ev = e;
              if (e.originalEvent.touches && e.originalEvent.touches.length) {
                    ev = e.originalEvent.touches[0];
              } else if(e.originalEvent.changedTouches && e.originalEvent.changedTouches.length) {
                    ev = e.originalEvent.changedTouches[0];
              }
              qspMouseX = ev.pageX;
              qspMouseY = ev.pageY;
          });

	qspSetPressableButtons();
	
	// Обработчик строки ввода
    $('#qsp-input-line input').keyup(function(event) {
		var input = $(event.target);
		var val = input.val();
		if ($.data(input, 'saved_text') !== val) {
			qspInputStringChange(val);
		}
        if ((event.which === 13) || (event.keyCode === 13)) {
			qspInputStringEnter();
            return false;
		}
    });
	
	// Обработка клавиши Escape.
	$(document).keyup(function(e) {
		if (e.keyCode == 27) { 
			qspEscapeDialog(); 
		} else if (e.keyCode == 13) {
			qspSubmitDialog();
		}
	});
	
	// Обработка ссылок в полке игр.
	$(document).on('click', '.qsp-gamelist-item', function (ev) {
		ev.preventDefault();
		var hash = $(this).attr('hash');
		// Skin callback
		if (typeof(qspSkinOnSelectLocalGameInGamestock) == 'function') {
			// Если обработчик вернул "false",
			// значит игру запускать не нужно.
			if (!qspSkinOnSelectLocalGameInGamestock(hash))
				return;
		}
		QspLib.selectLocalGameInGamestock(hash);
	});
	
	// Кнопка "Загрузить с диска".
	$(document).on('click', '.qsp-browsefile-button', function (ev) {
		ev.preventDefault();
		qspSelectGame();
	});
	
    // Cкрываемый текстовый блок.
	$(document).on('click', '.qsp-spoiler-title', function () {
        $(this).slideUp({done: function () {
			$(this).closest('.qsp-spoiler').find('div.qsp-spoiler-content').slideDown({done: function () {
				qspRefreshMainScroll();
			}});
		}});
    });
	
	// Выравниваем по центру экрана все DIV'ы с классом qsp-center
	$(window).resize(function(){
		$('.qsp-center').css({
			position:'absolute',
			left: ($(window).width() - $('.qsp-center').outerWidth()) / 2,
			top: ($(window).height() - $('.qsp-center').outerHeight()) / 2
		});
	});
		  
	$(document).bind("contextmenu", function(e) {
		return false;
	});

    // Переменная должна устанавливаться в подключаемом файле - QspLibPG.js, QspLibAIR.js
    if (typeof(qspLibMode) === "undefined")
        alert("Не подключена библиотека QspLibXXX.js!");
    
	// Library callback
	if (typeof(qspLibOnInitApi) === 'function')
		qspLibOnInitApi();
		
	// Fallback to JQuery "animate" if no CSS3 transition support
	if (!$.support.transition)
		$.fn.transition = $.fn.animate;
		
	qspDetectScreenHD();

	// Skin callback
	if (typeof(qspSkinOnInitApi) === 'function')
		qspSkinOnInitApi();
}

function qspInitScrolls() {
	document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);

	if ($('#qsp-scroller-main').length)
		qsp_iScroll_main = new iScroll("qsp-wrapper-main", {hScroll:false, bounce:false, hScrollbar:false, hideScrollbar:false, fadeScrollbar:false, onBeforeScrollEnd:qspOnBeforeScrollEnd});
	if ($('#qsp-scroller-acts').length)
		qsp_iScroll_acts = new iScroll("qsp-wrapper-acts", {hScroll:false, bounce:false, hScrollbar:false, hideScrollbar:false, fadeScrollbar:false, onBeforeScrollEnd:qspOnBeforeScrollEnd});
	if ($('#qsp-scroller-vars').length)
		qsp_iScroll_vars = new iScroll("qsp-wrapper-vars", {hScroll:false, bounce:false, hScrollbar:false, hideScrollbar:false, fadeScrollbar:false, onBeforeScrollEnd:qspOnBeforeScrollEnd});
	if ($('#qsp-scroller-objs').length)
		qsp_iScroll_objs = new iScroll("qsp-wrapper-objs", {hScroll:false, bounce:false, hScrollbar:false, hideScrollbar:false, fadeScrollbar:false, onBeforeScrollEnd:qspOnBeforeScrollEnd});
	if ($('#qsp-scroller-msg').length)
		qsp_iScroll_msg = new iScroll("qsp-wrapper-msg", {hScroll:false, bounce:false, hScrollbar:false, hideScrollbar:false, fadeScrollbar:false, onBeforeScrollEnd:qspOnBeforeScrollEnd});
}

function qspOnBeforeScrollEnd(e)
{
	// Не засчитываем клик, если контент скроллился
	if (this.moved)
	{
		var point = this.hasTouch ? e.changedTouches[0] : e;
		gcb_ignoreClick(point.clientX, point.clientY);
	}
}

function qspApplyScrollsVisibility()
{
    var clear = !qspDialogOpened && 
				(qspGameSkin.hideScrollAny != 1) && (qspGameSkin.hideScrollArrows != 1);
    var mainScrollVisible = clear && (qspGameSkin.hideScrollMain != 1);
    var actsScrollVisible = clear && (qspGameSkin.showActs == 1) && (qspGameSkin.hideScrollActs != 1);
    var varsScrollVisible = clear && (qspGameSkin.showVars == 1) && (qspGameSkin.hideScrollVars != 1);
    var objsScrollVisible = clear && (qspGameSkin.showObjs == 1) && (qspGameSkin.hideScrollObjs != 1);
	if ((qsp_iScroll_main != null) && ("vScrollbarIndicator" in qsp_iScroll_main) && (qsp_iScroll_main.vScrollbarIndicator != null))
		qsp_iScroll_main.vScrollbarIndicator.style.visibility = mainScrollVisible ? 'visible' : 'hidden';
	if ((qsp_iScroll_acts != null) && ("vScrollbarIndicator" in qsp_iScroll_acts) && (qsp_iScroll_acts.vScrollbarIndicator != null))
		qsp_iScroll_acts.vScrollbarIndicator.style.visibility = actsScrollVisible ? 'visible' : 'hidden';
	if ((qsp_iScroll_vars != null) && ("vScrollbarIndicator" in qsp_iScroll_vars) && (qsp_iScroll_vars.vScrollbarIndicator != null))
		qsp_iScroll_vars.vScrollbarIndicator.style.visibility = varsScrollVisible ? 'visible' : 'hidden';
	if ((qsp_iScroll_objs != null) && ("vScrollbarIndicator" in qsp_iScroll_objs) && (qsp_iScroll_objs.vScrollbarIndicator != null))
		qsp_iScroll_objs.vScrollbarIndicator.style.visibility = objsScrollVisible ? 'visible' : 'hidden';

	// Это хак для Андроида. Без перезаполнения основного описания, 
	// возникают странные глюки после отображения qsp-overlay (т.е. при показе любого диалога)
	if (qspMainContent != null)
		qspSetMainContent(qspMainContent, false);
	
	qspLoadRetinaImages('img');

	// Заново выравниваем по центру экрана все изменившиеся блоки
	$(window).resize();
}

function qspShowSystemMenu()
{
	if (qspDialogOpened)
		return;
	
	// Отключаем загрузку, если включена переменная "NOSAVE".
	if (qspGameSkin != null) {
		var saveButton = $(qspSystemMenuId).find('#qsp-save-button');
		if (saveButton.length > 0) {
			var noSave = qspGameSkin.noSave != 0;
			if (saveButton.data('onclick') == undefined) {
				saveButton.data('onclick', saveButton.attr('onclick'));
			}
			var onclick = noSave ? '' : saveButton.data('onclick');
			saveButton.attr('onclick', onclick);
			saveButton.toggleClass('disabled', noSave);
		}
	}
		
	qspDialogOpened = true;
	qspCurDialog = 'system-menu';
	$(qspSystemMenuId).show();
    qspApplyScrollsVisibility();
	//Закрываем при клике вне диалога
	setTimeout( function() { // Delay for Mozilla
			$(".qsp-overlay").bind('click', qspHandlerSystemMenuOverlayClick);
	}, 0);
} 

 
// Вызовы Native -> JS

function qspSetGroupedContent(content) 
{
    // parameter type: JSON Object
    
    // Единый вызов для установки всех атрибутов интерфейса,
    // включая скин и содержимое всех окошек.
    if (typeof(content.skin) !== 'undefined')
        qspUpdateSkin(content.skin);
    if (typeof(content.main) !== 'undefined') {
        qspSetMainContent(content.main, true);
		qspMainViewNeedScroll = content.scrollmain == 1;
	}
    if (typeof(content.acts) !== 'undefined')
        qspSetActsContent(content.acts);
    if (typeof(content.vars) !== 'undefined')
        qspSetVarsContent(content.vars);
    if (typeof(content.objs) !== 'undefined')
        qspSetInvContent(content.objs);
    if (typeof(content.js) !== 'undefined')
        qspExecJS(content.js);
    qspApplyScrollsVisibility();
	
	if (typeof(qspSkinOnSetGroupedContent) === 'function')
		qspSkinOnSetGroupedContent();
	
    if (!qspSplashHidden && (qspLibMode === "PHONEGAP"))
    {
		qspSplashHidden = true;
		setTimeout( function() {
				   cordova.exec(null, null, "SplashScreen", "hide", []);
				   }, 500);
    }
}

function qspShowSaveSlotsDialog(content)
{
    // parameter type: JSON Object

	//Показываем слоты - для загрузки либо сохранения
    var slots = content.slots;
	qspSaveSlotsModeOpen = content.open == 1;
	$("#qsp-dialog-save-slots-container").empty();
	
	for (i = 0; i < slots.length; i++)
	{
		var slot = slots[i];
		// Если в скине определена своя функция для заполнения слотов, используем её.
		// Иначе создаём разметку по умолчанию.
		var slotHtml = (typeof(qspSkinGetSaveSlotHtml) == 'function') ?
							qspSkinGetSaveSlotHtml(slot, i) :
							qspGetDefaultSaveSlotHtml(slot, i);
		$("#qsp-dialog-save-slots-container").append(slotHtml);
	}
	qspCloseSystemMenu();

	qspDialogOpened = true;
	qspCurDialog = 'save';
	$("#qsp-dialog-save-slots").show();
	
	qspApplyScrollsVisibility();

	//Закрываем при клике вне диалога
	setTimeout( function() { // Delay for Mozilla
			$(".qsp-overlay").bind('click', qspHandlerSaveSlotsOverlayClick);
	}, 0);
}

function qspGetDefaultSaveSlotHtml(slot, index)
{
	var empty = slot == "-empty-";
	var active = !qspSaveSlotsModeOpen || !empty;
	var slotName = empty ? qspSlotTitleText + (i + 1) + qspSlotEmptyText : qspSlotTitleText + slot;
	var div = "<div class='qsp-save-slot-" + (active ? "enabled" : "disabled") + " qsp-button'>" + 
					"<div>" +
					(active ? "<a onclick='javascript:qspCloseSaveSlots(" + (i + 1) + ");'>" : "") + 
					"<div>" + slotName + "</div>" +
					(active ? "</a>" : "") + 
					"</div>" +
				"</div>";
	return div;
}

function qspFillLocalGamesList(games)
{
	// Список локальных игр.
	qspLocalGames = games;
	var container = $('#qsp-localgames-container');
	if (container.length > 0) {
		// Заполняем список.
		container.empty();
		var html = '';
		var moreButton = games.length > 10;
		for (i = 0; i < games.length; i++) {
			game = games[i];
			var link = '<p><a href="#" class="qsp-gamelist-item" hash="' + game.hash + '">'
						+ game.title
						+ '</a></p>';
			
			// Начало скрываемого блока.
			if (moreButton && (i == 10)) {
				var spoilerStart = '<div class="qsp-spoiler"><div class="qsp-spoiler-title">'
									+ 'Показать весь список (' + games.length + ' игр)'
									+ '</div>'
									+ '<div class="qsp-spoiler-content" style="display:none;">';
				html += spoilerStart;
			}
						
			html += link;
		}
		// Конец скрываемого блока.
		if (moreButton) {
			html += '</div></div>'
		}
		container.append(html);
		qspRefreshMainScroll();
		// Skin callback
		if (typeof(qspSkinOnFillLocalGamesList) == 'function')
			qspSkinOnFillLocalGamesList();
	}
}

function qspMsg(text)
{
    // parameter type: JSON String
	qspDialogOpened = true;
	qspCurDialog = 'msg';
    text = qspApplyTemplateForText(qspGameSkin.msgTextFormat, text);

	$('#qsp-dialog-msg-content').empty();
	$('#qsp-dialog-msg-content').append(text);
	
	if (qsp_iScroll_msg != null)
		setTimeout(function () {
                   qsp_iScroll_msg.refresh();
                   if ((qspGameSkin != null) && (qspGameSkin.disableScroll == 0))
                       qsp_iScroll_msg.scrollTo(0, 0, 0, false);
                   }, 0);

	$('#qsp-dialog-msg').show();
	
	qspApplyScrollsVisibility();
	
	//Закрываем при клике вне диалога ?
	/*
	setTimeout( function() { // Delay for Mozilla
			$(".qsp-overlay").click( function() {
				qspCloseMsg();
			});
	}, 0);
*/
}

function qspError(error)
{
    // parameter type: JSON Object

	//Показываем окошко с сообщением ошибки игры
    //desc, loc, actIndex, line
	qspDialogOpened = true;
	qspCurDialog = 'error';
	
	var errDesc = "<center>Ошибка!</center>" + 
                    "Локация: " + error.loc + "<br>" +
					"Строка: " + error.line + "<br>" +
					"Номер действия: " + error.actIndex + "<br>" +
					"Описание: " + error.desc;
	$('#qsp-dialog-error-content').empty();
	$('#qsp-dialog-error-content').append(errDesc);
	$('#qsp-dialog-error').show();
    
    qspApplyScrollsVisibility();
}

function qspMenu(menu)
{
    // parameter type: JSON Array
	qspDialogOpened = true;
	qspCurDialog = 'user-menu';
	$('#qsp-dialog-user-menu-container').empty();
	for (i = 0; i < menu.length; i++)
	{
		var menuItem = menu[i];
		// Если в скине определена своя функция для создания элементов контекстного меню, используем её.
		// Иначе создаём разметку по умолчанию.
		var menuItemHtml = (typeof(qspSkinGetMenuItemHtml) == 'function') ?
							qspSkinGetMenuItemHtml(menuItem, i) :
							qspGetDefaultMenuItemHtml(menuItem, i);
		$("#qsp-dialog-user-menu-container").append(menuItemHtml);
	}
	
	
    // Если менюшка вылазит за правый край, сдвигаем ее влево.
    // Если менюшка вылазит за нижний край, сдвигаем ее вверх.
    var menuX = qspMouseX;
    var menuY = qspMouseY;
	var menuWidth = $('#qsp-dialog-user-menu').width();
	var menuHeight = $('#qsp-dialog-user-menu').height();
	var windowWidth = window.pageXOffset + $(window).width();
	var windowHeight = window.pageYOffset + $(window).height();
	if (menuX + menuWidth > windowWidth)
	{
		menuX = windowWidth - menuWidth;
	}
	if (menuY + menuHeight > windowHeight)
	{
		menuY = windowHeight - menuHeight;
	}
    // Показываем контекстное меню.
    showContextMenu({
        menu: "qsp-dialog-user-menu",
		item: "qsp-user-menu-item",
		x: menuX,
		y: menuY
    },
        function(action) {
        qspCloseMenu(action);
    });
}

function qspGetDefaultMenuItemHtml(menuItem, index)
{
	return (menuItem.desc == '-') ? 
		"<div class='qsp-user-menu-divider'><hr></div>" :
		"<div class='qsp-user-menu-item'><a href=\"#" + index + "\">" + 
		qspApplyTemplateForTextAndImage(qspMenuListItemFormat, menuItem.desc, menuItem.image) + 
		"</a></div>";
}

function qspInput(text)
{
    // parameter type: JSON String
	qspDialogOpened = true;
	qspCurDialog = 'input';
    
    text = qspApplyTemplateForText(qspGameSkin.inputTextFormat, text);
	$('#qsp-dialog-input-content').empty();
	$('#qsp-dialog-input-content').append(text);
	$('#qsp-dialog-input-text').val("");
	
	$("#qsp-dialog-input").show();
	
	qspApplyScrollsVisibility();
}

function qspView(path)
{
    // parameter type: JSON String

    // Библиотека QSP дергает вызов "VIEW" еще ДО выполнения кода первой локации,
    // поэтому qspView в первый раз зовется еще до того,
    // как мы назначили переменные оформления в qspGameSkin
    if (qspGameSkin === null)
        return;
    
	if (path == "")
	{
		//Закрываем VIEW по запросу из игры, если оно было ранее открыто
		qspCloseView();
	}
	else
	{
		// Открываем VIEW
		var newImageHtml = '<img src="' + path + '">';

		// Очищаем оба контейнера, так как режим могли переключить.
		$('#qsp-dialog-view-image-container').empty();
		$('#qsp-view').empty();
		if (qspGameSkin.viewAlwaysShow != 1)
		{
			$('#qsp-dialog-view-image-container').append(newImageHtml);
			// Делаем диалог невидимым, чтобы он не дёргался при центровке
			$('#qsp-dialog-view').css('visibility', 'hidden');
			// Выводим его на экран (он всё ещё невидим, но теперь занимает место)
			$('#qsp-dialog-view').show();

			qspDialogOpened = true;
			qspCurDialog = 'view';
			$('#qsp-dialog-view-image-container').imagesLoaded().always(function() {
				setTimeout( function() { // Delay for Mozilla
						// Закрываем при любом клике
						$(document).bind('click', qspHandlerViewClick);
						// Обновляем центровку блока, если требуется
						qspApplyScrollsVisibility();
						// Показываем view
						$('#qsp-dialog-view').css('visibility', 'visible');
				}, 0);
			});
		}
		else
		{
			// Показываем view
			$('#qsp-view').append(newImageHtml);
			$('#qsp-view').imagesLoaded().always(qspRefreshMainScroll);
		}
	}
}

function qspSetInputString(text)
{
	$('#qsp-input-line input').val(text);
}

// На будущее

function qspBlockUi(block)
{
	//Блокируем или разблокируем интерфейс
	qspUiBlocked = block;
}


// Вспомогательные функции

function qspSetMainContent(content, initial) 
{
	if (initial)
	{
		qspMainViewWasScrolled = false;
		qspMainContent = content;
	}
	
	$("#qsp-main").empty();
    content = qspApplyTemplateForText(qspGameSkin.mainDescTextFormat, content);
    $("#qsp-main").append(content);
	$("#qsp-main").imagesLoaded().always(qspRefreshMainScroll);
} 

function qspRefreshMainScroll()
{
	if (qsp_iScroll_main != null)
		setTimeout(function () {
            // Skin callback
            if (typeof(qspSkinOnMainScrollRefresh) == 'function')
                qspSkinOnMainScrollRefresh();
			qsp_iScroll_main.refresh();
			if ((qspGameSkin != null) && (qspGameSkin.disableScroll == 0) && !qspMainViewWasScrolled)
            {
				qspMainViewWasScrolled = true;
				// Скроллим в конец, если новый текст добавлен к предыдущему,
				// иначе скроллим в начало.
				var scrollAvailableHeight = qsp_iScroll_main.maxScrollY;
				var y = qspMainViewNeedScroll && scrollAvailableHeight < 0 ? scrollAvailableHeight : 0;
				qsp_iScroll_main.scrollTo(0, y, 0, false);
            }
            // Skin callback
            if (typeof(qspSkinOnMainScrollRefreshed) == 'function')
                qspSkinOnMainScrollRefreshed();
		}, 0);
}

function qspSetVarsContent(content) 
{
    var content_vars = qspApplyTemplateForText(qspGameSkin.varsDescTextFormat, content);
	$("#qsp-vars").empty();
	$("#qsp-vars").append(content_vars);
	$("#qsp-vars").imagesLoaded().always(qspRefreshVarsScroll);
} 

function qspRefreshVarsScroll()
{
	if (qsp_iScroll_vars != null)
		setTimeout(function () {
            // Skin callback
            if (typeof(qspSkinOnVarsScrollRefresh) == 'function')
                qspSkinOnVarsScrollRefresh();
			qsp_iScroll_vars.refresh();
			if ((qspGameSkin != null) && (qspGameSkin.disableScroll == 0))
				qsp_iScroll_vars.scrollTo(0, 0, 0, false);
            // Skin callback
            if (typeof(qspSkinOnVarsScrollRefreshed) == 'function')
                qspSkinOnVarsScrollRefreshed();
		}, 0);
}

function qspSetActsContent(acts, under_desc) 
{
	$("#qsp-acts").empty();
	if (acts)
	{
        for (i = 0; i < acts.length; i++) {
			var action = acts[i];
			// Если в скине определена своя функция для создания действий, используем её.
			// Иначе создаём разметку по умолчанию.
			var actionHtml = (typeof(qspSkinGetActionHtml) == 'function') ?
								qspSkinGetActionHtml(action, i) :
								qspGetDefaultActionHtml(action, i);
			$("#qsp-acts").append(actionHtml);
		}
	}

	$("#qsp-acts").imagesLoaded().always(function () {
		qspRefreshMainScroll();
		qspRefreshActsScroll();
	});
} 

function qspGetDefaultActionHtml(action, index)
{
	return "<div class='qsp-action qsp-button'><a " + 
	" onclick='javascript:qspExecuteAction(\"" + index + "\");'>" + 
	qspApplyTemplateForTextAndImage(qspActsListItemFormat, action.desc, action.image) + 
	"</a></div>";
}

function qspRefreshActsScroll()
{
	if (qsp_iScroll_acts != null)
		setTimeout(function () {
            // Skin callback
            if (typeof(qspSkinOnActsScrollRefresh) == 'function')
                qspSkinOnActsScrollRefresh();
			qsp_iScroll_acts.refresh();
			if ((qspGameSkin != null) && (qspGameSkin.disableScroll == 0))
				qsp_iScroll_acts.scrollTo(0, 0, 0, false);
            // Skin callback
            if (typeof(qspSkinOnActsScrollRefreshed) == 'function')
                qspSkinOnActsScrollRefreshed();
		}, 0);
}

function qspSetInvContent(objs) 
{
    qspSelectedObjectIndex = -1;
    qspInvObjs = objs;
	if (objs)
	{
		for (i = 0; i < objs.length; i++) {
            if (objs[i].selected == 1)
                qspSelectedObjectIndex = i;
		}
        qspFillInvWithObjs();
	}
	$("#qsp-inv").imagesLoaded().always(qspRefreshObjsScroll);
}

function qspRefreshObjsScroll()
{
	if (qsp_iScroll_objs != null)
		setTimeout(function () {
            // Skin callback
            if (typeof(qspSkinOnObjsScrollRefresh) == 'function')
                qspSkinOnObjsScrollRefresh();
			qsp_iScroll_objs.refresh();
            // Skin callback
            if (typeof(qspSkinOnObjsScrollRefreshed) == 'function')
                qspSkinOnObjsScrollRefreshed();
		}, 0);
}

function qspFillInvWithObjs()
{
	$("#qsp-inv").empty();
	if (qspInvObjs)
	{
		for (i = 0; i < qspInvObjs.length; i++) {
			var object = qspInvObjs[i];
			// Если в скине определена своя функция для создания предметов, используем её.
			// Иначе создаём разметку по умолчанию.
			var objectHtml = (typeof(qspSkinGetObjectHtml) == 'function') ?
								qspSkinGetObjectHtml(object, i) :
								qspGetDefaultObjectHtml(object, i);
			$("#qsp-inv").append(objectHtml);
		}
		qspLoadRetinaImages('#qsp-inv img');
	}
	// Skin callback
	if (typeof(qspSkinOnFillInvWithObjs) == 'function')
		qspSkinOnFillInvWithObjs();
}

function qspGetDefaultObjectHtml(object, index)
{
	var selected = index == qspSelectedObjectIndex;
	return "<div class='qsp-object'>" +
		(selected ? 
			"" : 
			("<a style=\"cursor: pointer;\" onclick='javascript:qspSelectObject(\"" + index + "\");'>")
		) +
		qspApplyTemplateForTextAndImage(
			selected ? qspObjsListSelItemFormat : qspObjsListItemFormat, 
			object.desc, 
			object.image) + 
		(selected ? "" : "</a>") +
		"</div>";
}

function qspExecJS(cmd) 
{
	// Выполняем яваскрипт, переданный из игры командой EXEC('JS:...')
	cmd = '<script>' + cmd + '</script>';
	$('#qsp-js-sandbox').html(cmd);
}

function qspUpdateSkin(skin)
{
	// Если это первое заполнение скина, устанавливаем имя системного диалога.
	// Диалог системного меню по умолчанию - "#qsp-dialog-system-menu".
	// Когда автору требуется собирать игру и для "Полки игр", и для "standalone", 
	// у него есть возможность задать дополнительный диалог "#qsp-dialog-system-menu-standalone".
	// Если такой диалог создан, то он будет выбран при настройке "standalone=true" в конфиге игры,
	// во всех остальных случаях будет выбран диалог по умолчанию.
	if ((qspGameSkin === null) 
		&& (skin.isStandalone == 1) 
		&& ($("#qsp-dialog-system-menu-standalone").length)) {
		qspSystemMenuId = "#qsp-dialog-system-menu-standalone";
	}
	
	//Устанавливаем переменные оформления
	qspGameSkin = skin;
    
	//Показываем либо скрываем окно действий
	var qspActsId = $('#qsp-wrapper-acts').length ? '#qsp-wrapper-acts' : '#qsp-acts';
	if (qspGameSkin.showActs == 1) {
		$(qspActsId).show();
	} else {
		$(qspActsId).hide();
	}
	//Показываем либо скрываем окно инвентаря
	if (qspGameSkin.showObjs == 1)
		$("#qsp-wrapper-objs").show();
	else
		$("#qsp-wrapper-objs").hide();
	//Показываем либо скрываем окно дополнительного описания
	var qspVarsId = $('#qsp-wrapper-vars').length ? '#qsp-wrapper-vars' : '#qsp-vars';
	if (qspGameSkin.showVars == 1)
		$(qspVarsId).show();
	else
		$(qspVarsId).hide();
 	//Показываем либо скрываем строку ввода(не реализовано)
	if (qspGameSkin.showInput == 1)
		$("#qsp-input-line").show();
	else
		$("#qsp-input-line").hide();

	// Skin callback
	if (typeof(qspSkinOnUpdateSkin) == 'function')
		qspSkinOnUpdateSkin();
}

// Вызовы JS -> Native

function qspExecuteAction(index)
{
	//Нажали на действие
	if (qspDialogOpened || qspUiBlocked)
		return;

    QspLib.executeAction(index);
}

function qspSelectObject(index)
{
	//Нажали на предмет в инвентаре
	if (qspDialogOpened || qspUiBlocked)
		return;
    qspSelectedObjectIndex = index;
    qspFillInvWithObjs();
    QspLib.selectObject(index);
}

function qspLoadGame()
{
    QspLib.loadGame();
}

function qspSaveGame()
{
    QspLib.saveGame();
}

function qspRestartGame()
{
	qspCloseSystemMenu();

	// Skin callback
	if (typeof(qspSkinOnRestart) == 'function')
		qspSkinOnRestart();

	QspLib.restartGame();
}

function qspCloseSaveSlots(slot)
{
	$(".qsp-overlay").unbind('click', qspHandlerSaveSlotsOverlayClick);
	$("#qsp-dialog-save-slots").hide();
	qspDialogOpened = false;
	qspCurDialog = '';
	
	// Skin callback
	if (slot != -1)
	{
		if (qspSaveSlotsModeOpen)
		{
			if (typeof(qspSkinOnLoad) == 'function')
				qspSkinOnLoad();
		}
		else
		{
			if (typeof(qspSkinOnSave) == 'function')
				qspSkinOnSave();
		}
	}
	
	qspApplyScrollsVisibility();
	
    QspLib.saveSlotSelected(slot, qspSaveSlotsModeOpen);
}

function qspCloseMsg()
{
//	$(".qsp-overlay").unbind('click');
	$("#qsp-dialog-msg").hide();
	qspDialogOpened = false;
	qspCurDialog = '';
	qspApplyScrollsVisibility();

    // Это не очень логично, что нам приходится обновлять
    // скроллер после того, как мы его прячем,
    // но иначе на Андроиде возникают страннейшие баги.
	if (qsp_iScroll_msg != null)
		setTimeout(function () {
                   qsp_iScroll_msg.refresh();
                   }, 0);
    
    QspLib.msgResult();
}

function qspCloseError()
{
	$('#qsp-dialog-error').hide(); 
	qspDialogOpened = false;
	qspCurDialog = '';
    qspApplyScrollsVisibility();
    
    QspLib.errorResult();
}

function qspCloseMenu(index)
{
	$('#qsp-dialog-user-menu').hide(); 
	qspDialogOpened = false;
	qspCurDialog = '';
	qspApplyScrollsVisibility();
    QspLib.userMenuResult(index);
}

function qspCloseInput(valid)
{
	$("#qsp-dialog-input").hide();
	qspDialogOpened = false;
	qspCurDialog = '';
	qspApplyScrollsVisibility();

	var text = valid ? $("#qsp-dialog-input-text").val() : '';
	QspLib.inputResult(text);
}

function qspCloseView()
{
	$(document).unbind('click', qspHandlerViewClick);
	
	$('#qsp-dialog-view').hide();
	$('#qsp-dialog-view-image-container').empty();
	$('#qsp-view').empty();
	
	if (qspDialogOpened) {
		qspDialogOpened = false;
		qspCurDialog = '';
	}
	qspApplyScrollsVisibility();
	return false;
}

function qspCloseSystemMenu()
{
	$(".qsp-overlay").unbind('click', qspHandlerSystemMenuOverlayClick);
	$(qspSystemMenuId).hide();
	qspDialogOpened = false;
	qspCurDialog = '';
	qspApplyScrollsVisibility();
}

function qspInputStringChange(text)
{
	// Изменился текст в строке ввода
	QspLib.setInputString(text);
}

function qspInputStringEnter()
{
	// Нажали Enter в строке ввода
	QspLib.runInputString();
}

function qspSelectGame()
{
	// Вызов диалога для открытия файла.
    QspLib.openGameFile();
}

function qspDefaultGame()
{
	// Запуск игры по умолчанию.
	// Для универсального плеера, открывается "Полка игр".
    QspLib.runDefaultGame();
	qspCloseSystemMenu();
}


// Диалоги

function qspSetDialogs()
{
	//MSG
	$('#qsp-dialog-msg').hide();

	//ERROR
	$('#qsp-dialog-error').hide();
	
	//MENU
	$('#qsp-dialog-user-menu').hide();
	$('#qsp-dialog-user-menu').css("position", "absolute").css("z-index", "500");
	
	//SYSTEM MENU
	$('#qsp-dialog-system-menu').hide();
	$('#qsp-dialog-system-menu-standalone').hide();
	
	//SAVE SLOTS
	$('#qsp-dialog-save-slots').hide();
	
	//INPUT
	$('#qsp-dialog-input').hide();
	
	//VIEW
	$('#qsp-dialog-view').hide();
}

function qspSetPressableButtons()
{
	// Делаем возможность задавать специальные картинки для нажатых кнопок.
	// Можно обходиться и без них.
	var isTouchPad = (/hp-tablet/gi).test(navigator.appVersion);
    var hasTouch = 'ontouchstart' in window && !isTouchPad;
	
	// Убиваем лишние клики
	if (hasTouch) {
		document.addEventListener('click', gcb_clickBuster, true);
	} else {
		document.addEventListener('click', gcb_clickBusterNoTouch, true);
	}
	
	var START_EV = hasTouch ? 'touchstart' : 'mousedown';
	var END_EV = hasTouch ? 'touchend touchcancel' : 'mouseup dragend';
    $(document).bind(START_EV, function(e) {
			qspLastPressedButton = e.target;
	
			// Делаем "нажатый" вид кнопок
			var t = $(qspLastPressedButton);
			
			// Если у картинки указан атрибут data-pressed, 
			// используем его для переключения в "нажатое" состояние
			var src1 = t.attr('src');
			var src2 = t.attr('data-pressed');
			if ((typeof(src1) !== 'undefined') && (typeof(src2) !== 'undefined'))
			{
				t.attr('src', src2);
				t.attr('data-pressed', src1);
			}
			
			// Для кнопки задаем класс "pressed"
			t = t.hasClass('qsp-button') ? t : t.parents(".qsp-button");
			if (t.length && t.hasClass('qsp-button') && !t.hasClass('pressed'))
				t.addClass('pressed');
          });
    $(document).bind(END_EV, function(e) {
			// Возвращаем вид кнопок к исходному состоянию
			var t = $(qspLastPressedButton);
			if (!t.length)
				return;
			qspLastPressedButton = null;
				
			var src1 = t.attr('src');
			var src2 = t.attr('data-pressed');
			if ((typeof(src1) !== 'undefined') && (typeof(src2) !== 'undefined'))
			{
				t.attr('src', src2);
				t.attr('data-pressed', src1);
			}

			t = t.hasClass('qsp-button') ? t : t.parents(".qsp-button");
			if (t.length && t.hasClass('pressed'))
			{
				setTimeout( function() {
					t.removeClass('pressed');
					t = null;
				}, 200);
			}
          });
	
	// Убираем задержку кликов на тач-девайсах (fastclick.js)
	new FastClick(document.body);
}

function qspApplyTemplateForText(template, text)
{
    var t = template;
    t = t.replace('%TEXT%', text);
    return t;
}

function qspApplyTemplateForTextAndImage(template, text, image)
{
    var t = template;
    t = t.replace('%TEXT%', text);
    t = t.replace('%IMAGE%', image);
    return t;
}

function qspMakeRetinaPath(path)
{
	// Добавляем @2x для картинок, если требуется
	if (!qspScreenHD) return path;
	var checkForRetina = new RegExp("(.+)(@2x\\.\\w{3,4})");
	if (checkForRetina.test(path)) return path;
	return path.replace(/(.+)(\.\w{3,4})$/, "$1@2x$2");
}

function qspSubmitDialog()
{
	// Валидное закрытие диалога по кнопке подтверждения.
	// Пользователь нажал Enter либо пробел.
	if (qspDialogOpened) {
		if (qspCurDialog === 'msg')
		{
			qspCloseMsg();
		}
		else if (qspCurDialog === 'error')
		{
			qspCloseError();
		}
		else if (qspCurDialog === 'input')
		{
			qspCloseInput(true);
		}
		else if (qspCurDialog === 'view')
		{
			// Вьюшку тоже закрываем по Enter и пробелу, хоть у неё и нет кнопки.
			// Чисто для удобства.
			qspCloseView();
		}
	}
}

function qspEscapeDialog()
{
	// Выход из активного диалога.
	// Пользователь нажал BACK либо Escape.
	if (qspDialogOpened) {
		if (qspCurDialog === 'save')
		{
			qspCloseSaveSlots(-1);
		}
		else if (qspCurDialog === 'msg')
		{
			qspCloseMsg();
		}
		else if (qspCurDialog === 'error')
		{
			qspCloseError();
		}
		else if (qspCurDialog === 'user-menu')
		{
			qspCloseMenu(-1);
		}
		else if (qspCurDialog === 'system-menu')
		{
			qspCloseSystemMenu();
		}
		else if (qspCurDialog === 'input')
		{
			qspCloseInput(false);
		}
		else if (qspCurDialog === 'view')
		{
			qspCloseView();
		}
	}
}

function qspBackKeyPressed()
{
	// Пользователь нажал кнопку BACK
	if (qspDialogOpened)
	{
		qspEscapeDialog();
	}
	else
	{
		QspLib.moveTaskToBackground();
	}
}

function qspLoadRetinaImages(selector)
{
	if (qspScreenHD)
	{
		$(selector).retina('@2x');
	}
}

function qspDetectScreenHD()
{
	// get pixel ratio
	var myDevicePixelRatio = 1;
	if (window.devicePixelRatio !== undefined) {
		myDevicePixelRatio = window.devicePixelRatio;
	} else if (window.matchMedia !== undefined) {
		for (var i = 1; i <= 2; i += 0.5) {
			if (window.matchMedia('(min-resolution: ' + i + 'dppx)').matches) {
				myDevicePixelRatio = i;
			}
		}
	}
	
	// Картинки @2x смотрятся лучше даже на экранах c плотностью пикселов 1.5 
	qspScreenHD = myDevicePixelRatio >= 1.5;

	if (qspScreenHD)
	{
		// Скрываем все картинки, заданные тегом IMG для Retina-дисплеев.
		// Нужно для того, чтобы не было "моргания" при подгрузке HD-версий
		$(document.head).append('<style> body.retina img:not(.processed-retina-img) { opacity:0; } </style>');
		// Добавляем класс "retina" к BODY
		$(document.body).addClass('retina');
	}
}

function qspSetDevice() {
	// Вызывается, когда мы узнали, на каком устройстве запущена игра
	if (typeof(qspSkinOnDeviceSet) === 'function')
		qspSkinOnDeviceSet();
}