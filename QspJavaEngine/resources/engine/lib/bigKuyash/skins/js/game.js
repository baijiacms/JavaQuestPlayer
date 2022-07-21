// Переменные скина
var skinMusic = true;
var skinStage = "";
var skinSplashScreenVisible = true;

// Функции скина для игры

// Колбэки

function qspSkinOnDeviceSet() {
	// Вызывается, когда мы узнали, на каком устройстве запущена игра
	var mobile = qspIsAndroid || qspIsIos;
	$(document.body).toggleClass('mobile', mobile);

	var more_games_link = 'http://qsp.su';
	if (qspIsAndroid) {
		more_games_link = 'market://search?q=pub:Butterfly+Lantern';
	} else if (qspIsIos) {
		more_games_link = 'itms-apps://itunes.apple.com/ru/artist/butterfly-lantern-interactive/id508671395';
	}
	$("#more-games-button a").attr('href', more_games_link);
}

function qspSkinOnUpdateSkin() {
	// Когда строка ввода видна, элемент BODY получает класс "input-visible".
	// Это позволяет нам определить правила CSS, зависящие от видимости строки ввода.
	$(document.body).toggleClass('input-visible', qspGameSkin.showInput == 1);
	// Аналогично для остальных окошек
	$(document.body).toggleClass('acts-visible', qspGameSkin.showActs == 1);
	$(document.body).toggleClass('objs-visible', qspGameSkin.showObjs == 1);
	$(document.body).toggleClass('vars-visible', qspGameSkin.showVars == 1);
	
	skinSetMusicButton(skinMusic);
}

function qspSkinOnSetGroupedContent() {
	// При первом вызове заполнения окон,
	// гасим сплэш-скрин.
	if (skinSplashScreenVisible) {
		skinHideSplashScreen();
	}
}

function qspSkinOnInitApi() {
	// Для мобильников в портретном режиме отключаем обычные скроллы.
	// Потестить на девайсах, может и не пригодится.
	if ($(window).width() <= 480) {
	/*
		if (qsp_iScroll_main != null) {
			qsp_iScroll_main.destroy();
			qsp_iScroll_main = null;
		}
		if (qsp_iScroll_acts != null) {
			qsp_iScroll_acts.destroy();
			qsp_iScroll_acts = null;
		}
		if (qsp_iScroll_vars != null) {
			qsp_iScroll_vars.destroy();
			qsp_iScroll_vars = null;
		}
		if (qsp_iScroll_objs != null) {
			qsp_iScroll_objs.destroy();
			qsp_iScroll_objs = null;
		}
		*/
	}
}
// Создание разметки для действия. 
function qspSkinGetActionHtml(action, index) {
	qspActsListItemFormat = "<div class='skin-action'><span class='skin-action-pointer'>&gt;</span>" +
							((action.image.length > 0) ? "<img src='%IMAGE%'>" : "") +
							"<span class='skin-action-text'>%TEXT%</span></div>";
	return qspGetDefaultActionHtml(action, index);
}
// Создание разметки для предмета. 
function qspSkinGetObjectHtml(object, index) {
	qspObjsListItemFormat = "<center><div class='skin-object-outer'><div class='skin-object-inner'>" + 
							((object.image.length > 0) ?"<div class='skin-object-image'><img src='%IMAGE%'></div>" : "") + 
							"<div class='skin-object-text'>%TEXT%</div></div></div></center>";
	qspObjsListSelItemFormat = "<center><div class='skin-object-outer selected'><div class='skin-object-inner'>" + 
							((object.image.length > 0) ?"<div class='skin-object-image'><img src='%IMAGE%'></div>" : "") + 
							"<div class='skin-object-text'>%TEXT%</div></div></div></center>";
	return qspGetDefaultObjectHtml(object, index);
}
// Создание разметки для пункта меню.
function qspSkinGetMenuItemHtml(menuItem, index)
{
	qspMenuListItemFormat = "<div class='skin-menu-item-outer'><div class='skin-menu-item-inner'>" + 
							((menuItem.image.length > 0) ?"<div class='skin-menu-item-image'><img src='%IMAGE%'></div>" : "") + 
							"<div class='skin-menu-item-text'>%TEXT%</div></div></div>";
							
	return qspGetDefaultMenuItemHtml(menuItem, index);
}


// Собственные функции скина

// Гасим сплэш-скрин.
function skinHideSplashScreen() {
	skinSplashScreenVisible = false;
	$("#skin-ui-wrapper").show();
	$("#skin-splashscreen-foreground").fadeOut('slow');
}

function skinToggleInv() {
	$("#skin-inv-wrapper").slideToggle();
	$("#skin-inv-toggle").toggleClass('open');
}

function skinToggleMusic() {
	skinMusic = !skinMusic;
	skinSetMusicButton(skinMusic);
	QspLib.setMute(!skinMusic);
}

function skinSetMusicButton(skinMusic) {
	$('#skin-user-music').toggleClass('on', skinMusic);
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
//������ = rust.legion@gmail.com   