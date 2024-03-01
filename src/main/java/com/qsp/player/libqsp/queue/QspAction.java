package com.qsp.player.libqsp.queue;

/**
 * @author：ChenXingYu
 * @date 2024/2/29 14:40
 */
public enum QspAction {

    init(99),
    execute(1),
    initGame(2),
    loadGame(3),
    saveGame(4),
    setSelObject(5),
    refresh(6),
    onActionClicked(7);
    private int action;

    QspAction(int action)
    {
        this.action=action;
    }
    // 根据code返回枚举类型,主要在switch中使用
    public static QspAction getByCode(int code) {
        for (QspAction optionTypeEnum : values()) {
            if (optionTypeEnum.action==code) {
                return optionTypeEnum;
            }
        }
        return null;
    }
    public int getAction() {
        return action;
    }
}
