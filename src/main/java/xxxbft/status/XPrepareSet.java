package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class XPrepareSet {

    /**
     * seq->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashSet<Byte>> xPrepares = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte index) {
        if (!xPrepares.containsKey(seq)) {
            xPrepares.put(seq, new ConcurrentHashSet<>());
        }
        xPrepares.get(seq).add(index);
    }

    synchronized public static boolean contains(int seq, byte index) {
        if (xPrepares.containsKey(seq)
                && xPrepares.get(seq).contains(index)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        xPrepares.remove(seq);
    }

}
