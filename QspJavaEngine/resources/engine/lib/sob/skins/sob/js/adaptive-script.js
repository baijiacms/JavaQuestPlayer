onresize = function()
{
	// Получаем текущие данные окна
    var htmlWidth  = document.getElementsByTagName('html')[0].clientWidth;
    var htmlHeight = document.getElementsByTagName('html')[0].clientHeight;
	
	// Выбираем элемент масштабирования
	var scalable = document.getElementsByTagName('body')[0];
	
	// Вводим стандартные значения окна (из них берется коэфициент масштабирования)
	var defWidth = 1280;
	var defHeight = 720;
	
	// Масштабируем элемент
	zoom = Math.min(htmlWidth / defWidth, htmlHeight / defHeight);
	scalable.style.zoom = zoom;
}

document.addEventListener("DOMContentLoaded", onresize);
window.addEventListener('resize', onresize, true);