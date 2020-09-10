package com.niklasarndt.matsebutler.util;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import java.util.Optional;

/**
 * Created by Niklas on 2020/07/26.
 */
public class Emojis {

    public static final String WHITE_CHECK_MARK = getUnicode("white_check_mark");
    public static final String X = getUnicode("x");
    public static final String LOCK = getUnicode("lock");
    public static final String TOOLS = getUnicode("hammer_and_pick");
    public static final String QUESTION_MARK = getUnicode("question");
    public static final String PARTYING_FACE = getUnicode("partying_face");
    public static final String TABLE_TENNIS = getUnicode("table_tennis");
    public static final String WARNING = getUnicode("warning");
    public static final String HOURGLASS = getUnicode("hourglass_flowing_sand");
    public static final String WASTEBASKET = "\uD83D\uDDD1️";
    public static final String WAVE = getUnicode("wave");

    private Emojis() {
    }

    public static String getUnicode(String alias) {
        Emoji em = EmojiManager.getForAlias(alias);
        return em != null ? em.getUnicode() : "";
    }

    public static Optional<Emoji> getEmoji(String alias) {
        return Optional.of(EmojiManager.getForAlias(alias));
    }

    public static Optional<Emoji> getByUnicode(String unicode) {
        return Optional.of(EmojiManager.getByUnicode(unicode));
    }

    public static boolean isEmoji(String input) {
        return EmojiManager.isEmoji(input);
    }

    public static boolean isOnlyEmojis(String input) {
        //This is a rather dirty workaround because the default Discord response is not recognized
        // by emoji-java :(
        return EmojiManager.isOnlyEmojis(input) || input.equals(Emojis.WASTEBASKET);
    }
}
