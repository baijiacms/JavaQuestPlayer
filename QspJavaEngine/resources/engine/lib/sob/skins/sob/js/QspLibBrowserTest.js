/* Функции скина для игры */

// Колбэки

function qspSkinOnInitApi() {
	// Закрываем инвентарь по клику
	setTimeout( function() { // Delay for Mozilla
			$("#qsp-inv").bind('click', qspHandlerInvClick);
			$("#qsp-wrapper-objs").bind('click', qspHandlerInvClick);
			$("#qsp-user2 .qsp-skin-overlay").bind('click', qspHandlerInvClick);
	}, 0);
}

function qspSkinOnDeviceSet() {
	// Вызывается, когда мы узнали, на каком устройстве запущена игра
	var more_games_link = 'http://qsp.su';
	if (qspIsAndroid) {
		more_games_link = 'market://search?q=pub:Butterfly+Lantern';
	} else if (qspIsIos) {
		more_games_link = 'itms-apps://itunes.apple.com/ru/artist/butterfly-lantern-interactive/id508671395';
	}
	$("#more-games-button a").attr('href', more_games_link);
}

function qspSkinOnSetGroupedContent() {
    skinRefreshBugfix();
}

function qspSkinOnUpdateSkin() {
	skinSetMusicButton();
	skinMoveInv();

	skinHideScrollsOriginal = qspGameSkin.hideScrollMain;
	skinHideScrolls(false);
}

function qspSkinOnSave() {
	skinHideInv();
}

function qspSkinOnLoad() {
	skinHideInv();
}

function qspSkinOnRestart() {
	skinHideInv();
}

// Свои функции

var skinHideScrollsOriginal = 0;
var skinInv = false;
var skinMusic = true;
var skinStage = "";

var qspHandlerInvClick = function() { skinHideInv(); };

function skinToggleMusic() {
	skinMusic = !skinMusic;
	skinSetMusicButton();
	QspLib.setMute(!skinMusic);
}

function skinHideInv() {
	// прячем инвентарь
	if (skinInv) {
		skinInv = false;
		skinMoveInv();
		skinHideScrolls(true);
	}
}

function skinShowInv() {
	skinInv = true;
	skinMoveInv();
	skinHideScrolls(true);
}

function skinHideScrolls(immediate) {
	qspGameSkin.hideScrollMain = ((skinHideScrollsOriginal === 1) || skinInv) ? 1 : 0;
	if (immediate) {
		qspApplyScrollsVisibility();
	}
}

function skinMoveInv() {
	$('#qsp-wrapper-objs').toggle(skinInv);
	$('#qsp-user2').toggle(skinInv);
	qspRefreshObjsScroll();
}

function skinSetMusicButton() {
	skinToggleButton('#qsp-user-music img', '(button_music_)(on|off)(_pressed)?', '$1' + (skinMusic ? 'on' : 'off') + '$3');
}

function skinSetStage(cssClass) {
	// Переключаем класс всего body, тем самым задаем разный стиль для разных игровых экранов
	var t = $(document.body);
	if ((skinStage !== '') && (t.hasClass(skinStage))) {
		t.removeClass(skinStage);
	}
	skinStage = cssClass;
	if ((cssClass !== '') && (!t.hasClass(cssClass))) {
		t.addClass(cssClass);
	}
}

function skinToggleButton(selector, pattern, replacement) {
	var t = $(selector);
	var re = new RegExp(pattern, "g");
	var btn1 = t.attr('src').replace(re, replacement);
	var btn2 = t.attr('data-pressed').replace(re, replacement);
	t.attr('src', btn1);
	t.attr('data-pressed', btn2);
}

function skinRefreshBugfix()
{
	// Показываем и сразу скрываем невидимый блок размером с экран.
	// Без этого в эмуляторе не обновляется описание.
	$('#qsp-refresh-bugfix').show();
	$('#qsp-refresh-bugfix').hide();
}























/*
*  Bridge:
*           Javascript -> PhoneGap QSP Plugin
*
*/

var qspLibMode = "BROWSER_TEST";       // "AIR", "PHONEGAP" - устанавливаем для того, 
                                   // чтобы api.js мог выполнять различный код в зависимости от платформы

var testStage = 0;

var QspLib = {
    
    restartGame: function() {
		testChangeStage(0);
    },

    executeAction: function(index) {
		testRunAction(index);
    },
    selectObject: function(index) {
        //return cordova.exec(null, null, "QspLib", "selectObject", [index]);
    },
    loadGame: function() {
		qspShowSaveSlotsDialog(getTestSaveSlots(1));
    },
    
    saveGame: function() {
		qspShowSaveSlotsDialog(getTestSaveSlots(0));
    },
    
    saveSlotSelected: function(index, open) {
        //var mode = open ? 1 : 0;
        //return cordova.exec(null, null, "QspLib", "saveSlotSelected", [index, mode]);
    },

    msgResult: function() {
        //return cordova.exec(null, null, "QspLib", "msgResult", []);
    },
    
    errorResult: function() {
        //return cordova.exec(null, null, "QspLib", "errorResult", []);
    },
    
    userMenuResult: function(index) {
        //return cordova.exec(null, null, "QspLib", "userMenuResult", [index]);
    },
    
    inputResult: function(text) {
        //return cordova.exec(null, null, "QspLib", "inputResult", [text]);
		alert(text);
    },

    setMute: function(mute) {
        //return cordova.exec(null, null, "QspLib", "setMute", [mute]);
    }
};


//Функция для предзагрузки картинок (сейчас не используется)
jQuery.preloadImages = function () {
    if (typeof arguments[arguments.length - 1] === 'function') {
        var callback = arguments[arguments.length - 1];
    } else {
        var callback = false;
    }
    if (typeof arguments[0] == 'object') {
        var images = arguments[0];
        var n = images.length;
    } else {
        var images = arguments;
        var n = images.length - 1;
    }
    if (n == 0 && typeof callback == 'function') {
        callback();
        return;
    }
    var not_loaded = n;
    for (var i = 0; i < n; i++) {
    	$(images[i]).imagesLoaded().always(function() {
                                                        if (--not_loaded < 1 && typeof callback == 'function') {
                                                            callback();
                                                        }
                                                        });
    }
}

function getTestMainDesc()
{
	if (testStage===0)
	{
		return ('<div id="qsp-cover-wrapper">' +
				'<img src="skins/gfx/top_cover-310x288.png"><br>' + 
				'<a onclick="javascript:testChangeStage(2);" class="qsp-skin-button"><img src="skins/gfx/main_menu_start-149x48.png" data-pressed="skins/gfx/main_menu_start_pressed-149x48.png"></a><br>' + 
				'<a onclick="javascript:testChangeStage(1);" class="qsp-skin-button"><img src="skins/gfx/main_menu_about-149x48.png" data-pressed="skins/gfx/main_menu_about_pressed-149x48.png"></a><br>' +
				'<a href="http://qsp.su" class="qsp-skin-button"><img src="skins/gfx/main_menu_moregames-149x48.png" data-pressed="skins/gfx/main_menu_moregames_pressed-149x48.png"></a>' +
				'</div>'
				);
	}
	else if (testStage===1)
	{
		return ('<center><b>Феи: пропавший зонтик</b></center><br>' +
'<br>' +
'<i>Автор:</i> Ajenta Arrow<br>' +
'<br>' +
'<i>Дизайн, оформление:</i> Александра Гришина "yellowsparrow"<br>' +
'<br>' +
'<i>Музыка:</i><br>' +
'	"Easy Lemon", "Airport Lounge" - Kevin MacLeod (<a href="http://incompetech.com/">incompetech.com</a>)<br>' +
'<br>' +
'<br>' +
'<b>Что это за игра?</b><br>' +
'<br>' +
'Это интерактивная история. Вы будете выбирать различные варианты действий, таким образом управляя сюжетом. Ваша цель - достичь успешной концовки.<br>' +
'<br>' +
'<b>Волшебные зонтики</b><br>' +
'<br>' +
'В любой момент игры вы можете использовать волшебные зонтики. Но как только вы выберете действие, ситуация изменится. Поэтому, сначала проверьте - нельзя ли здесь применить зонтик, и пробуйте применить(нажать на иконку зонтика). Иначе момент может быть упущен.<br>' +
'<br>' +
'<br>' +
'<b>Обращение к игрокам:</b><br>' +
'<br>' +
'Если вы хотите сообщить об ошибке, недоработке, предложить свои идеи, или задать вопрос, обращайтесь по адресу <a href="mailto:butterfly-lantern@text-games.ru">butterfly-lantern@text-games.ru</a>.<br>' +
'<br>' +
'Если же вы просто хотите поделиться впечатлением от игры, пожалуйста, оставьте оценку и комментарий на странице игры в App Store.<br>' +
'<br>' +
'Имейте в виду, что App Store не позволяет разработчикам отвечать на комментарии на странице игры. Для обратной связи используйте вышеуказанный e-mail.<br>'
);
	}
	else if (testStage===2)
	{
		return ('В саду у Энке жили феи - три девочки и один мальчик: Алия, Амалия, Анития и Тенай. У Алии были прозрачные жёлтые крылышки, как и положено фее домашнего уюта, и жила она в щели между крышей дома и потолочными балками.<br>' +
				'<br>' +
				'Однажды Алия возвращалась от подруги Амалии к себе домой и обнаружила, что пропал зонтик. Это был её любимый зонтик - красный в жёлтый горошек. С его помощью она зажигала в саду ночных светлячков. Ночью был дождь, и утром она оставила его сушиться на подоконнике своего жилища, а теперь зонтик исчез. Самое странное, что три других зонтика, которые фея оставила сушиться рядом, остались на месте.<br>' +
				'<br>' +
				'Но кто же мог взять её любимый зонтик? Алия взяла оставшиеся три зонтика, чтобы они уж точно никуда не исчезли, и задумалась - что же теперь делать?<br>'
		);
	}
	else if (testStage===3)
	{
		return ('Алия открыла золотой зонт, её душа снова наполнилась радостью. Голова закружилась ещё больше. Крылья ослабели. Ей стало уже всё равно, что пропал зонтик и что она не сможет зажечь ночные светлячки. Да и что такое светлячки, она уже помнила с трудом. Она опустилась на черепицу крыши и уснула. Наутро она проснулась, но это была уже совсем другая история...<br>' +
				'<br>' +
				'Боюсь, что это конец приключений.<br>' +
				'Попробуйте начать игру сначала.<br>'
		);
	}
	return null;
}

function getTestActs()
{
	if (testStage===1)//об игре
	{
		return [
				{image:'', desc:'Назад'}
				];
	}
	else if (testStage===2)//начало
	{
		return [
				{image:'', desc:'Концовка'},
				{image:'', desc:'А это - описание действия, растянутое на две длинные строки. Оно не должно поместиться в одну строку шириной 600 пикселов.'},
				{image:'', desc:'Я нацелил телескоп на светило'}
				];
	}
	else if (testStage===3)//концовка
	{
		return [
				{image:'', desc:'Заново'}
				];
	}
	return null;
}

function getTestObjs()
{
	if (testStage===2)//начало
	{
		return [
				{desc:'Гранаты', image:'skins/gfx/grenades'},
				{desc:'Патроны', image:'skins/gfx/ammo'},
				{desc:'Динамит', image:'skins/gfx/dynamite'},
				{desc:'Чертежи', image:'skins/gfx/scheme'},
				{desc:'Неизв. устройство', image:'skins/gfx/device'},
				{desc:'Пулемет', image:'skins/gfx/pulemet'},
				{desc:'Анальгетик', image:'skins/gfx/analgetic'},
				{desc:'Плунжер', image:'skins/gfx/plunjer'}
				];
	} 
	else if (testStage===3)//концовка
	{
		return [];
	}
	return null;
}

function getTestSkin()
{
	var skin = {
					disableScroll:0,
					mainBackImagePath:"",
					sysMenuButtonImagePath:"",
					backColor:"#5A5A5A",
					menuBorder:1,
					menuBorderColor:"#FFF",
					fontColor:"#000",
					linkColor:"#F00",
					fontName:"_sans",
					fontSize:16,
					viewAlwaysShow:1,
					showActs:0,
					mainDescIntegratedActions:1,
					showObjs:0,
					showVars:0,
					mainDescTextFormat:"%TEXT%",
					msgTextFormat:"%TEXT%",
					inputTextFormat:"%TEXT%",
					actsListItemFormat:"<div style='padding-top:10px;padding-bottom:10px;padding-left: 5px;padding-right: 5px;'><font color='#880000'>&gt;</font> %TEXT%</div>",
					objsListItemFormat:"<div style='padding-bottom:5px;'><center>%TEXT%</center></div>",
					objsListSelItemFormat:"<div style='padding-bottom:5px;'><center>%TEXT%</center></div>",
					contentPath:""
					};
	if (testStage===0)//меню
	{
		skin.showActs = 0;
		skin.showObjs = 0;
	}
	else if (testStage===1)//об игре
	{
		skin.showActs = 1;
		skin.showObjs = 0;
	}
	else if (testStage===2)//начало
	{
		skin.showActs = 1;
		skin.showObjs = 1;
	}
	else if (testStage===3)//концовка
	{
		skin.showActs = 1;
		skin.showObjs = 1;
	}
	return skin;
}

function getTestSaveSlots(mode)
{
	return {
					open:mode,
					slots:[	
							'-empty-',
							'-empty-',
							'3',
							'-empty-',
							'-empty-'
							]
				};
}

function testChangeStage(stage)
{
	//console.log('stage ' + stage);
	testStage = stage;

	testRunNewStage();
	
	var content = {
					main:getTestMainDesc(),
					skin:getTestSkin(),
					acts:getTestActs(),
					objs:getTestObjs()
					}; 
	qspSetGroupedContent(content);
}

function testRunAction(actIndex)
{
	if (testStage === 1)//об игре
	{
		if (actIndex==='0')
		{
			testChangeStage(0);//меню
		}
	}
	else if (testStage === 2)//начало
	{
		if (actIndex==='0')
		{
			testChangeStage(3);//концовка
		}
		else if (actIndex==='2')
		{
			qspInput('Я нацелил телескоп на это светило:');// тестируем диалог ввода
		}
	}
	else if (testStage === 3)//концовка
	{
		if (actIndex==='0')
		{
			testChangeStage(0);//начало
		}
	}
}

function testRunNewStage()
{
	if (testStage === 0)//обложка
	{
		qspView('');
		skinSetStage('cover');
	}
	else if (testStage === 1)//об игре
	{
		skinSetStage('about');
	}
	else if (testStage === 2)//начало
	{
		skinSetStage('main');
	}
	else if (testStage === 3)//проигрыш
	{
		skinSetStage('main');
		qspView('skins/gfx/scenes/mansion-240x320.png');
	}
}


function onPhoneGapDeviceReady() {
    // Now safe to use the PhoneGap API
	testChangeStage(0);
}
