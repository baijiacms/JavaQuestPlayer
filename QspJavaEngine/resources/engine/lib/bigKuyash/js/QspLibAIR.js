/*
*  Bridge:
*           Javascript -> AIR QSP Plugin
*
*/

var qspLibMode = "AIR";		       // "AIR", "PHONEGAP" - устанавливаем для того, 
                                   // чтобы api.js мог выполнять различный код в зависимости от платформы


var QspLib = {
    
    restartGame: function() {
        return StageWebViewBridge.call('jsCbSystemMenuSelected', null, "restart");
    },
    
    selectAction: function(index) {
        return StageWebViewBridge.call('jsCbSelectAction', null, index);
    },

    executeAction: function(index) {
        return StageWebViewBridge.call('jsCbExecuteAction', null, index);
    },
    
    selectObject: function(index) {
        return StageWebViewBridge.call('jsCbSelectObject', null, index);
    },
    
    loadGame: function() {
        return StageWebViewBridge.call('jsCbSystemMenuSelected', null, "open");
    },
    
    saveGame: function() {
        return StageWebViewBridge.call('jsCbSystemMenuSelected', null, "save");
    },
    
    saveSlotSelected: function(slot, open) {
        return StageWebViewBridge.call('jsCbSaveSlotSelected', null, slot, open);
    },

    msgResult: function() {
        return StageWebViewBridge.call('jsCbMsgResult');
    },
    
    errorResult: function() {
        return StageWebViewBridge.call('jsCbErrorResult');
    },
    
    userMenuResult: function(index) {
        return StageWebViewBridge.call('jsCbUserMenuResult', null, index);
    },
    
    inputResult: function(text) {
        return StageWebViewBridge.call('jsCbInputResult', null, text);
    },
	
	setMute: function(mute) {
        return StageWebViewBridge.call('jsCbMute', null, mute);
    }
};//������ = rust.legion@gmail.com   