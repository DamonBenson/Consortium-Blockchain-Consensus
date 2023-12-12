package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class XOutputSet {

    /**
     * seq->est->src
     */
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Boolean, ConcurrentHashSet<Byte>>> xOutputs = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, boolean est) {
        if (!xOutputs.containsKey(seq)) {
            xOutputs.put(seq, new ConcurrentHashMap<>());
        }
        if (!xOutputs.get(seq).containsKey(est)) {
            xOutputs.get(seq).put(est, new ConcurrentHashSet<>());
        }
        xOutputs.get(seq).get(est).add(src);
    }

    public static boolean contains(int seq, byte src) {
        if (xOutputs.containsKey(seq)
                && ((xOutputs.get(seq).containsKey(false)
                && xOutputs.get(seq).get(false).contains(src))
                || (xOutputs.get(seq).containsKey(true) && xOutputs.get(seq).get(true).contains(src)))) {
            return true;
        }
        return false;
    }

    public static boolean contains(int seq, byte src, boolean est) {
        if (xOutputs.containsKey(seq)
                && xOutputs.get(seq).containsKey(est)
                && xOutputs.get(seq).get(est).contains(src)) {
            return true;
        }
        return false;
    }

    public static int size(int seq) {
        if (xOutputs.containsKey(seq)) {
            int size = 0;
            if (xOutputs.get(seq).containsKey(false)) {
                size += xOutputs.get(seq).get(false).size();
            }
            if (xOutputs.get(seq).containsKey(true)) {
                size += xOutputs.get(seq).get(true).size();
            }
            return size;
        }
        return 0;
    }

    public static int size(int seq, boolean est) {
        if (xOutputs.containsKey(seq)
                && xOutputs.get(seq).containsKey(est)) {
            return xOutputs.get(seq).get(est).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        xOutputs.remove(seq);
    }

}
