package org.firstinspires.ftc.teamcode.lib;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagDetectionHistoryTest {

    private TagDetectionHistory history;

    @Before
    public void setUp() {
        history = new TagDetectionHistory();
    }

    @Test
    public void emptyHistory_returnsNullMotif() {
        assertNull(history.getVotedMotif());
        assertEquals(0, history.getObeliskDetectionCount());
        assertEquals(0.0, history.getConfidence(), 0.001);
    }

    @Test
    public void goalTags_doNotCountAsObelisk() {
        // GOAL タグ (ID 20, 24) は MOTIF 判定に影響しない
        record(20, 5);
        record(24, 3);
        assertNull(history.getVotedMotif());
        assertEquals(0, history.getObeliskDetectionCount());
    }

    @Test
    public void belowThreshold_returnsNullMotif() {
        // 最小検出回数 (5) 未満では未確定
        record(22, 4);
        assertNull(history.getVotedMotif());
    }

    @Test
    public void atThreshold_returnsMotif() {
        record(22, 5);
        assertEquals(Motif.PGP, history.getVotedMotif());
    }

    @Test
    public void majorityVote_selectsMostDetected() {
        record(21, 10);
        record(22, 3);
        record(23, 2);
        assertEquals(Motif.GPP, history.getVotedMotif());
    }

    @Test
    public void confidence_calculatesCorrectly() {
        record(21, 8);
        record(22, 2);
        // 8 / 10 = 0.8
        assertEquals(0.8, history.getConfidence(), 0.001);
    }

    @Test
    public void clear_resetsEverything() {
        record(22, 10);
        history.clear();
        assertNull(history.getVotedMotif());
        assertEquals(0, history.getObeliskDetectionCount());
        assertTrue(history.getRecords().isEmpty());
    }

    @Test
    public void records_areImmutable() {
        record(22, 1);
        try {
            history.getRecords().clear();
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void nonObeliskTags_recordedButNotVoted() {
        record(20, 10);  // GOAL Blue
        record(22, 6);   // OBELISK PGP
        // 全履歴には 16 件
        assertEquals(16, history.getRecords().size());
        // OBELISK カウントは 6 件のみ
        assertEquals(6, history.getObeliskDetectionCount());
        assertEquals(Motif.PGP, history.getVotedMotif());
    }

    private void record(int tagId, int count) {
        for (int i = 0; i < count; i++) {
            history.record(new TagDetectionRecord(
                    i * 0.033, tagId, 0, 0, 1.0, 0, 0, 0));
        }
    }
}
