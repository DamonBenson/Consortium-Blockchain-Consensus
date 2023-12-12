package pbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PrePrepareSet {

    /**
     * seq->view
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashSet<Integer>> prePrepares = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, int view) {
        if (!prePrepares.containsKey(seq)) {
            prePrepares.put(seq, new ConcurrentHashSet<>());
        }
        prePrepares.get(seq).add(view);
    }

    public static boolean contains(int seq, int view) {
        if(prePrepares.containsKey(seq)
                && prePrepares.get(seq).contains(view)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        prePrepares.remove(seq);
    }

}
