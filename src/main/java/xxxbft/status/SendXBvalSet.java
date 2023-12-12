package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class SendXBvalSet {

    /**
     * seq->src->round->est
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, ConcurrentHashSet<Boolean>>>> sendXBvals = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, byte round, boolean est) {
        if (!sendXBvals.containsKey(seq)) {
            sendXBvals.put(seq, new ConcurrentHashMap<>());
        }
        if (!sendXBvals.get(seq).containsKey(src)) {
            sendXBvals.get(seq).put(src, new ConcurrentHashMap<>());
        }
        if (!sendXBvals.get(seq).get(src).containsKey(round)) {
            sendXBvals.get(seq).get(src).put(round, new ConcurrentHashSet<>());
        }
        sendXBvals.get(seq).get(src).get(round).add(est);
    }

    public static boolean contains(int seq, byte src, byte round, boolean est) {
        if (sendXBvals.containsKey(seq)
                && sendXBvals.get(seq).containsKey(src)
                && sendXBvals.get(seq).get(src).containsKey(round)
                && sendXBvals.get(seq).get(src).get(round).contains(est)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        sendXBvals.remove(seq);
    }

}
