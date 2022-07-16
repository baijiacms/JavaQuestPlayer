/*
*  Bridge:
*           Javascript -> Awesomium QSP Plugin
*
*/

var qspLibMode = "AWESOMIUM";       // "AIR", "PHONEGAP", "AWESOMIUM" - устанавливаем для того, 
                                   // чтобы api.js мог выполнять различный код в зависимости от платформы


var QspLib = null;

function onWebDeviceReady() {
	if (QspLib !== null) {
		throw "onWebDeviceReady must be called only once!";
	}
	// Эта функция вызывается из движка Awesomium
	// в обработчике завершения загрузки страницы.
	// Только к этому моменту мы можем быть уверены,
	// что созданный движком Awesomium библиотечный объект "QspLibAwesomium"
	// уже загружен и нормально обработает вызовы колбэков.
	// Событие "$(document).ready" срабатывает раньше.
	if (typeof QspLibAwesomium == 'undefined') {
		throw "QspLibAwesomium is not available in \"document.ready\"";
	}
	QspLib = QspLibAwesomium;
	// Запускаем API.
	qspInitApi();
	// Самодельный диалог alert, 
	// так как в Awesomium стандартные диалоги не работают.
	// Короткий вариант будет работать только после полной инициализации.
	// До этого, вызываем напрямую через QspLib.
	window.alert = function(text) { QspLib.alert(text) };

	qspIsDesktop = true;
	// Сообщаем API, что нам стал известен тип устройства.
	qspSetDevice();
}

function debug(str) {
	$(document.body).append(str);
}

function qspLibOnInitApi() {
	setTimeout( function() { // Delay for Mozilla
		// Запуск игры по завершению инициализации API.
		QspLib.restartGame();
	}, 10);
}
