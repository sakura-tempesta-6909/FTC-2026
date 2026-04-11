package org.firstinspires.ftc.teamcode.config;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * {@link Const} の定数の整合性テスト。
 * rpmFromDistance ロジックは ShooterSubsystem に移動済み (private)。
 */
public class ConstTest {

    @Test
    public void velocityRanges_areMonotonic() {
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
}
