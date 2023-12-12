package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class XCommitSet {

    /**
     * seq->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashSet<Byte>> xCommits = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte index) {
        if (!xCommits.containsKey(seq)) {
            xCommits.put(seq, new ConcurrentHashSet<>());
        }
        xCommits.get(seq).add(index);
    }

    synchronized public static boolean contains(int seq, byte index) {
        if (xCommits.containsKey(seq)
                && xCommits.get(seq).contains(index)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        xCommits.remove(seq);
    }

}
