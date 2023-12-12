package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class XAuxSet {

    /**
     * seq->src->round->est->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Boolean, ConcurrentHashSet<Byte>>>>> xAuxs = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, byte round, boolean est, byte index) {
        if (!xAuxs.containsKey(seq)) {
            xAuxs.put(seq, new ConcurrentHashMap<>());
        }
        if (!xAuxs.get(seq).containsKey(src)) {
            xAuxs.get(seq).put(src, new ConcurrentHashMap<>());
        }
        if (!xAuxs.get(seq).get(src).containsKey(round)) {
            xAuxs.get(seq).get(src).put(round, new ConcurrentHashMap<>());
        }
        if (!xAuxs.get(seq).get(src).get(round).containsKey(est)) {
            xAuxs.get(seq).get(src).get(round).put(est, new ConcurrentHashSet<>());
        }
        xAuxs.get(seq).get(src).get(round).get(est).add(index);
    }

    public static boolean contains(int seq, byte src, byte round, boolean est, byte index) {
        if (xAuxs.containsKey(seq)
                && xAuxs.get(seq).containsKey(src)
                && xAuxs.get(seq).get(src).containsKey(round)
                && xAuxs.get(seq).get(src).get(round).containsKey(est)
                && xAuxs.get(seq).get(src).get(round).get(est).contains(index)) {
            return true;
        }
        return false;
    }

    public static int size(int seq, byte src, byte round, boolean est) {
        if (xAuxs.containsKey(seq)
                && xAuxs.get(seq).containsKey(src)
                && xAuxs.get(seq).get(src).containsKey(round)
                && xAuxs.get(seq).get(src).get(round).containsKey(est)) {
            return xAuxs.get(seq).get(src).get(round).get(est).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        xAuxs.remove(seq);
    }

}
