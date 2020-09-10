package com.niklasarndt.matsebutler.enums;

import com.niklasarndt.matsebutler.util.Emojis;

/**
 * Created by Niklas on 2020/07/25.
 */
public enum ResultType {
    SUCCESS(Emojis.WHITE_CHECK_MARK), ERROR(Emojis.X),
    NOT_FOUND(Emojis.QUESTION_MARK), UNAUTHORIZED(Emojis.LOCK),
    WARNING(Emojis.WARNING);

    public final String emoji;

    ResultType(String emoji) {
        this.emoji = emoji;
    }


}
