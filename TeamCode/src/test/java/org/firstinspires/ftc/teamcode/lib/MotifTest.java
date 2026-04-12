package org.firstinspires.ftc.teamcode.lib;

import org.junit.Test;

import static org.junit.Assert.*;

public class MotifTest {

    @Test
    public void fromTagId_returnsCorrectMotif() {
        assertEquals(Motif.GPP, Motif.fromTagId(21));
        assertEquals(Motif.PGP, Motif.fromTagId(22));
        assertEquals(Motif.PPG, Motif.fromTagId(23));
    }

    @Test
    public void fromTagId_returnsNullForGoalTags() {
        assertNull(Motif.fromTagId(20));
        assertNull(Motif.fromTagId(24));
    }

    @Test
    public void fromTagId_returnsNullForUnknownId() {
        assertNull(Motif.fromTagId(0));
        assertNull(Motif.fromTagId(99));
    }

    @Test
    public void getPatternString_formatsCorrectly() {
        assertEquals("G P P | G P P | G P P", Motif.GPP.getPatternString());
        assertEquals("P G P | P G P | P G P", Motif.PGP.getPatternString());
        assertEquals("P P G | P P G | P P G", Motif.PPG.getPatternString());
    }

    @Test
    public void getPattern_returnsDefensiveCopy() {
        char[] pattern = Motif.GPP.getPattern();
        pattern[0] = 'X';
        assertEquals('G', Motif.GPP.getPattern()[0]);
    }
}
