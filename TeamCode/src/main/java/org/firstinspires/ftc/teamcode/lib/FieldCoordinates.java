package org.firstinspires.ftc.teamcode.lib;

/**
 * フィールド座標系の変換ユーティリティ。
 *
 * <p>このプロジェクトには 2 つの座標系がある:
 * <ul>
 *   <li><b>Pedro 系</b> (PedroPathing 内部表現)
 *     <ul>
 *       <li>原点: フィールドの特定コーナー (0, 0)</li>
 *       <li>単位: inches</li>
 *       <li>範囲: (0..144, 0..144) inch</li>
 *     </ul>
 *   </li>
 *   <li><b>通常系 / Field 系</b> (AdvantageScope / WPILib / FTC 公式)
 *     <ul>
 *       <li>原点: フィールド中心 (0, 0)</li>
 *       <li>単位: meters</li>
 *       <li>Y 軸の向きが Pedro と逆 (X 軸方向にミラー)</li>
 *       <li>範囲: (±1.8288, ±1.8288) m</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Pedro 系 → 通常系 の変換規則:
 * <ul>
 *   <li>x_field = {@code 1.8288 - y_pedro * 0.0254}  (Pedro Y を反転して Field X に)</li>
 *   <li>y_field = {@code x_pedro * 0.0254 - 1.8288}  (Pedro X を Field Y に)</li>
 *   <li>heading_field = {@code θ_pedro + π/2}  (90°回転のみ。回転方向は Pedro と一致)</li>
 * </ul>
 *
 * <p><b>参考: Panels FieldPresets.PEDRO_PATHING との違い</b><br>
 * Panels プリセットは {@code offset(-72,-72) + flipY + rotate 90° CCW} で
 * Pedro (x, y) → (y-72, x-72)、heading は π/2 - θ。<br>
 * 我々 (AdvantageScope 向け) は Panels から X を追加反転 + heading convention が異なり符号反転。
 * 実機とロガーの計測で調整済み。
 */
public final class FieldCoordinates {
    private FieldCoordinates() {
    }

    /**
     * inches → m
     */
    public static final double PEDRO_TO_M = 0.0254;

    /**
     * FTC フィールド半辺 (144 inch の半分 = 72 inch = 1.8288 m)
     */
    public static final double FIELD_HALF_M = 72.0 * PEDRO_TO_M;

    // === Pedro 系 → 通常系 (Field 系) ===
    // 合成変換:
    //   x_field = 72 - y_pedro             (inch、中心原点、Pedro の Y 成分を Field X に反転で写す)
    //   y_field = x_pedro - 72             (inch、中心原点、Pedro の X 成分を Field Y に写す)
    //   heading_field = -θ_pedro           (Pedro と AS で heading 符号が逆)
    //
    // 数学的には offset(-72,-72) + rotate 90° CCW を行い、heading は Pedro が別 convention (CW か reference axis が +Y) のため符号反転。

    public static double pedroToFieldX(double pedroX, double pedroY) {
        return FIELD_HALF_M - pedroY * PEDRO_TO_M;
    }

    public static double pedroToFieldY(double pedroX, double pedroY) {
        return pedroX * PEDRO_TO_M - FIELD_HALF_M;
    }

    public static double pedroToFieldHeading(double pedroHeadingRad) {
        // 90°回転のみ (符号反転なし)。BlueGoal (Pedro θ=π) → Field θ=+3π/2 ≡ -π/2 (-Y 方向 = Blue Goal 側)
        // 回転方向は Pedro と同じ (CCW が CCW)。
        return pedroHeadingRad + Math.PI / 2.0;
    }

    // === 通常系 (Field 系) → Pedro 系 (往復用) ===

    public static double fieldToPedroX(double fieldX, double fieldY) {
        return (fieldY + FIELD_HALF_M) / PEDRO_TO_M;
    }

    public static double fieldToPedroY(double fieldX, double fieldY) {
        return (FIELD_HALF_M - fieldX) / PEDRO_TO_M;
    }

    public static double fieldToPedroHeading(double fieldHeadingRad) {
        return fieldHeadingRad - Math.PI / 2.0;
    }

    // === 距離スカラー変換 (原点シフト不要のベクトル量) ===

    /**
     * Pedro の距離 (inch) → m
     */
    public static double pedroLenToM(double pedroLen) {
        return pedroLen * PEDRO_TO_M;
    }
}
