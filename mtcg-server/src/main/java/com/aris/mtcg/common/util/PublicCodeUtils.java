package com.aris.mtcg.common.util;

import java.security.SecureRandom;

/**
 * 对外业务编码生成（短码、难遍历）。
 *
 * <p>格式：前缀 + 8 位 Base32（去掉易混字符 0/O/I/L/1），如 {@code D-7K2M9XPQ}、{@code G-A3F8Q2NW}。
 *
 * @author pengYuJun
 */
public final class PublicCodeUtils {

    public static final String DECK_PREFIX = "D-";

    public static final String GAME_PREFIX = "G-";

    /** 去掉 0/O/I/L/1，降低口播与手输歧义 */
    private static final char[] ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ".toCharArray();

    private static final int BODY_LEN = 8;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PublicCodeUtils() {}

    /** 生成卡组编码 */
    public static String newDeckCode() {
        return DECK_PREFIX + randomBody();
    }

    /** 生成对局编码 */
    public static String newGameCode() {
        return GAME_PREFIX + randomBody();
    }

    public static boolean isDeckCode(String raw) {
        return raw != null && raw.regionMatches(true, 0, DECK_PREFIX, 0, DECK_PREFIX.length());
    }

    public static boolean isGameCode(String raw) {
        return raw != null && raw.regionMatches(true, 0, GAME_PREFIX, 0, GAME_PREFIX.length());
    }

    /** 规范化：去空白、大写 */
    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        return raw.trim().toUpperCase();
    }

    private static String randomBody() {
        char[] buf = new char[BODY_LEN];
        for (int i = 0; i < BODY_LEN; i++) {
            buf[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(buf);
    }
}
