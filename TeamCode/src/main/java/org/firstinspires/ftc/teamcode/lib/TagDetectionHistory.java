package org.firstinspires.ftc.teamcode.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AprilTag 検出履歴を蓄積し、OBELISK タグの多数決で MOTIF を判定するクラス。
 * <p>
 * 検出は毎フレーム {@link #record(TagDetectionRecord)} で蓄積し、
 * MOTIF 判定は {@link #getVotedMotif()} で取得する。
 */
public class TagDetectionHistory {

    /** OBELISK タグ (ID 21-23) の多数決に必要な最小検出回数。 */
    private static final int MIN_VOTES_FOR_CONFIRMATION = 5;

    private final List<TagDetectionRecord> records = new ArrayList<>();
    private final Map<Integer, Integer> obeliskVotes = new HashMap<>();

    /** 検出記録を 1 件追加する。OBELISK タグならカウンタも更新。 */
    public void record(TagDetectionRecord record) {
        records.add(record);
        Motif motif = Motif.fromTagId(record.getTagId());
        if (motif != null) {
            obeliskVotes.merge(record.getTagId(), 1, Integer::sum);
        }
    }

    /** 全検出履歴 (読み取り専用)。 */
    public List<TagDetectionRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    /** OBELISK タグの検出回数マップ (tagId → count)。 */
    public Map<Integer, Integer> getObeliskVotes() {
        return Collections.unmodifiableMap(obeliskVotes);
    }

    /**
     * 多数決で最も検出された OBELISK タグに対応する Motif を返す。
     * 最小検出回数に達していなければ null。
     */
    public Motif getVotedMotif() {
        int bestId = -1;
        int bestCount = 0;
        for (Map.Entry<Integer, Integer> entry : obeliskVotes.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                bestId = entry.getKey();
            }
        }
        if (bestCount < MIN_VOTES_FOR_CONFIRMATION) return null;
        return Motif.fromTagId(bestId);
    }

    /** OBELISK タグの総検出回数。 */
    public int getObeliskDetectionCount() {
        int total = 0;
        for (int count : obeliskVotes.values()) {
            total += count;
        }
        return total;
    }

    /**
     * 最も検出された OBELISK タグの信頼度 (0.0〜1.0)。
     * 検出なしなら 0.0。
     */
    public double getConfidence() {
        int total = getObeliskDetectionCount();
        if (total == 0) return 0.0;
        int bestCount = 0;
        for (int count : obeliskVotes.values()) {
            bestCount = Math.max(bestCount, count);
        }
        return (double) bestCount / total;
    }

    /** 全履歴・カウンタをクリアする。 */
    public void clear() {
        records.clear();
        obeliskVotes.clear();
    }
}
