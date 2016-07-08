package com.geekhub.entities.enums;

public enum AbilityToCommentDocument {
    ENABLE, DISABLE;

    public static AbilityToCommentDocument getAttribute(boolean ability) {
        return ability ? ENABLE : DISABLE;
    }

    public static boolean getBoolean(AbilityToCommentDocument abilityToCommentDocument) {
        return abilityToCommentDocument == ENABLE;
    }
}
