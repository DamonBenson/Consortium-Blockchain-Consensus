package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;
import honeybadger.status.ConsensusStatus;
import p2p.NetworkInfo;

import java.util.concurrent.ConcurrentHashMap;

public class XBvalSet {

    /**
     * seq->src->round->est->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashMap<Boolean, ConcurrentHashSet<Byte>>>>> xBvals = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, byte round, boolean est, byte index) {
        if (!xBvals.containsKey(seq)) {
            xBvals.put(seq, new ConcurrentHashMap<>());
        }
        if (!xBvals.get(seq).containsKey(src)) {
            xBvals.get(seq).put(src, new ConcurrentHashMap<>());
        }
        if (!xBvals.get(seq).get(src).containsKey(round)) {
            xBvals.get(seq).get(src).put(round, new ConcurrentHashMap<>());
        }
        if (!xBvals.get(seq).get(src).get(round).containsKey(est)) {
            xBvals.get(seq).get(src).get(round).put(est, new ConcurrentHashSet<>());
        }
        xBvals.get(seq).get(src).get(round).get(est).add(index);
    }

    public static boolean contains(int seq, byte src, byte round, boolean est, byte index) {
        if (xBvals.containsKey(seq)
                && xBvals.get(seq).containsKey(src)
                && xBvals.get(seq).get(src).containsKey(round)
                && xBvals.get(seq).get(src).get(round).containsKey(est)
                && xBvals.get(seq).get(src).get(round).get(est).contains(index)) {
            return true;
        }
        return false;
    }

    public static int size(int seq, byte src, byte round, boolean est) {
        if (xBvals.containsKey(seq)
                && xBvals.get(seq).containsKey(src)
                && xBvals.get(seq).get(src).containsKey(round)
                && xBvals.get(seq).get(src).get(round).containsKey(est)) {
            return xBvals.get(seq).get(src).get(round).get(est).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        xBvals.remove(seq);
    }

}
