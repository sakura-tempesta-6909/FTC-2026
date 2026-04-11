package org.firstinspires.ftc.teamcode.testutil;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Subsystem / Command / Routine の単体テスト用基底クラス。
 * <p>
 * 本ハーネスがやること:
 * <ul>
 *   <li>{@link MotorEx} のコンストラクタを mockito-inline で intercept してモック化</li>
 *   <li>{@link ActiveOpMode#hardwareMap()} を mockStatic で intercept してフェイク HardwareMap を返す</li>
 *   <li>各テスト後にシングルトンの内部フィールドをリフレクションで null クリアして状態リーク防止</li>
 * </ul>
 * <p>
 * テストクラスはこれを継承し、{@link #initAllSubsystems()} を呼んでから検証ロジックを書く。
 * 個別のモータモックは {@link #motorMock} の {@code constructed()} で取得できる。
 */
public abstract class SubsystemTestBase {

    /** MotorEx のコンストラクタ呼び出しを intercept するモック。 */
    protected MockedConstruction<MotorEx> motorMock;
    /** ActiveOpMode.hardwareMap() の static 呼び出しを intercept するモック。 */
    protected MockedStatic<ActiveOpMode> activeOpModeMock;

    /** ShooterSubsystem 初期化時に Limelight が hardwareMap から取得される。 */
    protected Limelight3A fakeLimelight;
    /** ShooterSubsystem 初期化時に IMU が hardwareMap から取得される。 */
    protected IMU fakeImu;
    /** フェイク HardwareMap 本体。 */
    protected HardwareMap fakeHardwareMap;

    @Before
    public void setUpHarness() {
        // (1) ActiveOpMode.hardwareMap() を intercept
        fakeHardwareMap = mock(HardwareMap.class);
        fakeLimelight = mock(Limelight3A.class);
        fakeImu = mock(IMU.class);
        when(fakeHardwareMap.get(Limelight3A.class, "limelight")).thenReturn(fakeLimelight);
        when(fakeHardwareMap.get(IMU.class, Const.Imu.NAME)).thenReturn(fakeImu);

        activeOpModeMock = Mockito.mockStatic(ActiveOpMode.class);
        activeOpModeMock.when(ActiveOpMode::hardwareMap).thenReturn(fakeHardwareMap);

        // (2) new MotorEx(...) を intercept
        motorMock = Mockito.mockConstruction(MotorEx.class);
    }

    @After
    public void tearDownHarness() throws Exception {
        // モックを閉じる
        if (motorMock != null) motorMock.close();
        if (activeOpModeMock != null) activeOpModeMock.close();

        // シングルトン状態クリア (テスト間リーク防止)
        resetField(IntakeSubsystem.INSTANCE, "intakeMotor");
        resetField(FeederSubsystem.INSTANCE, "feederMotor");
        resetField(ShooterSubsystem.INSTANCE, "shooterMotor");
        resetField(ShooterSubsystem.INSTANCE, "controller");

        // CommandManager の running も clear
        CommandManager.INSTANCE.cancelAll();
    }

    /**
     * 全 Subsystem の {@code initialize()} を順番に呼び、ハードウェアモックを生成する。
     * MotorEx は呼び出し順に {@link #motorMock}.constructed() に追加される:
     * <ol>
     *   <li>index 0: Intake モータ</li>
     *   <li>index 1: Feeder モータ</li>
     *   <li>index 2: Shooter モータ</li>
     * </ol>
     */
    protected void initAllSubsystems() {
        IntakeSubsystem.INSTANCE.initialize();
        FeederSubsystem.INSTANCE.initialize();
        ShooterSubsystem.INSTANCE.initialize();
    }

    /** initAllSubsystems() で生成された Intake モータモック。 */
    protected MotorEx intakeMotor() {
        return motorMock.constructed().get(0);
    }

    /** initAllSubsystems() で生成された Feeder モータモック。 */
    protected MotorEx feederMotor() {
        return motorMock.constructed().get(1);
    }

    /** initAllSubsystems() で生成された Shooter モータモック。 */
    protected MotorEx shooterMotor() {
        return motorMock.constructed().get(2);
    }

    private static void resetField(Object instance, String fieldName) throws Exception {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            // primitive 対応
            Class<?> type = f.getType();
            if (type == double.class) {
                f.setDouble(instance, 0.0);
            } else if (type == int.class) {
                f.setInt(instance, 0);
            } else if (type == boolean.class) {
                f.setBoolean(instance, false);
            } else {
                f.set(instance, null);
            }
        } catch (NoSuchFieldException e) {
            // フィールドが存在しなければ無視 (リファクタ耐性)
        }
    }
}
