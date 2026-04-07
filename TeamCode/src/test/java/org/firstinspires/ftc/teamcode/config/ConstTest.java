package org.firstinspires.ftc.teamcode.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@link Const} の純粋関数 / 定数のテスト。
 * 距離 → RPM の段階切り替えロジックを境界値も含めて検証する。
 */
public class ConstTest {

    private static final double DELTA = 0.001;

    @Test
    public void rpmFromDistance_belowShortThreshold_returnsLowest() {
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.SHORT - 1);
        assertEquals(Const.Shooter.Velocity.LOWEST_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_atShortThreshold_returnsNormal() {
        // SHORT 以上 MEDIUM 未満は NORMAL
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.SHORT);
        assertEquals(Const.Shooter.Velocity.NORMAL_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_belowMediumThreshold_returnsNormal() {
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.MEDIUM - 1);
        assertEquals(Const.Shooter.Velocity.NORMAL_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_atMediumThreshold_returnsMediumHigh() {
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.MEDIUM);
        assertEquals(Const.Shooter.Velocity.MEDIUM_HIGH_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_belowLongThreshold_returnsMediumHigh() {
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.LONG - 1);
        assertEquals(Const.Shooter.Velocity.MEDIUM_HIGH_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_atLongThreshold_returnsHighest() {
        double rpm = Const.Shooter.rpmFromDistance(Const.Shooter.DistanceThreshold.LONG);
        assertEquals(Const.Shooter.Velocity.HIGHEST_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_farAway_returnsHighest() {
        double rpm = Const.Shooter.rpmFromDistance(500);
        assertEquals(Const.Shooter.Velocity.HIGHEST_RPM, rpm, DELTA);
    }

    @Test
    public void rpmFromDistance_zero_returnsLowest() {
        double rpm = Const.Shooter.rpmFromDistance(0);
        assertEquals(Const.Shooter.Velocity.LOWEST_RPM, rpm, DELTA);
    }

    @Test
    public void velocityRanges_areMonotonic() {
        // 速度定数が論理的に正しい順序になっていることを保証
        assertTrue("LOWEST < NORMAL",
                Const.Shooter.Velocity.LOWEST_RPM < Const.Shooter.Velocity.NORMAL_RPM);
        assertTrue("NORMAL < MEDIUM_HIGH",
                Const.Shooter.Velocity.NORMAL_RPM < Const.Shooter.Velocity.MEDIUM_HIGH_RPM);
        assertTrue("MEDIUM_HIGH < HIGHEST",
                Const.Shooter.Velocity.MEDIUM_HIGH_RPM < Const.Shooter.Velocity.HIGHEST_RPM);
    }

    @Test
    public void distanceThresholds_areMonotonic() {
        assertTrue("SHORT < MEDIUM",
                Const.Shooter.DistanceThreshold.SHORT < Const.Shooter.DistanceThreshold.MEDIUM);
        assertTrue("MEDIUM < LONG",
                Const.Shooter.DistanceThreshold.MEDIUM < Const.Shooter.DistanceThreshold.LONG);
    }

    private static void assertTrue(String message, boolean condition) {
        org.junit.Assert.assertTrue(message, condition);
    }
}
