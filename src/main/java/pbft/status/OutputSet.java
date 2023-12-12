package pbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

public class OutputSet {

    /**
     * seq
     */
    private static ConcurrentHashSet<Integer> outputs = new ConcurrentHashSet<>();

    synchronized public static void add(int seq) {
        outputs.add(seq);
    }

    public static boolean contains(int seq) {
        return outputs.contains(seq);
    }

    synchronized public static void remove(int seq) {
        outputs.remove(seq);
    }

}
