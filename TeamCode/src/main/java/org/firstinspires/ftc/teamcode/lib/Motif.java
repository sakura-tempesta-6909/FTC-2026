package org.firstinspires.ftc.teamcode.lib;

/**
 * OBELISK の各面に対応する MOTIF (ARTIFACT の色パターン)。
 * <p>
 * 各 MOTIF は 3 色 (P=Purple, G=Green) の繰り返しで RAMP 上の 9 スロットを定義する。
 * AprilTag ID と MOTIF の対応は DECODE Competition Manual Section 9.6 / 10.5.2 に基づく。
 */
public enum Motif {

    GPP(21, "GPP", new char[]{'G', 'P', 'P', 'G', 'P', 'P', 'G', 'P', 'P'}),
    PGP(22, "PGP", new char[]{'P', 'G', 'P', 'P', 'G', 'P', 'P', 'G', 'P'}),
    PPG(23, "PPG", new char[]{'P', 'P', 'G', 'P', 'P', 'G', 'P', 'P', 'G'});

    private final int tagId;
    private final String name;
    private final char[] pattern;

    Motif(int tagId, String name, char[] pattern) {
        this.tagId = tagId;
        this.name = name;
        this.pattern = pattern;
    }

    /** OBELISK 上の AprilTag ID。 */
    public int getTagId() {
        return tagId;
    }

    /** 表示用の短縮名 (例: "GPP")。 */
    public String getMotifName() {
        return name;
    }

    /**
     * RAMP 上の 9 スロットに対応する色パターン。
     * index 0 = GATE 側、index 8 = SQUARE 側。
     */
    public char[] getPattern() {
        return pattern.clone();
    }

    /** パターンを読みやすい文字列で返す (例: "G P P | G P P | G P P")。 */
    public String getPatternString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            if (i > 0 && i % 3 == 0) sb.append(" | ");
            else if (i > 0) sb.append(' ');
            sb.append(pattern[i]);
        }
        return sb.toString();
    }

    /**
     * AprilTag ID から対応する Motif を返す。該当なしなら null。
     */
    public static Motif fromTagId(int tagId) {
        for (Motif m : values()) {
            if (m.tagId == tagId) return m;
        }
        return null;
    }
}