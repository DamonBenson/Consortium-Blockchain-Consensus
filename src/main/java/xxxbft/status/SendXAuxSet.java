package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class SendXAuxSet {

    /**
     * seq->src->round
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, ConcurrentHashSet<Byte>>> sendXAuxs = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, byte round) {
        if (!sendXAuxs.containsKey(seq)) {
            sendXAuxs.put(seq, new ConcurrentHashMap<>());
        }
        if (!sendXAuxs.get(seq).containsKey(src)) {
            sendXAuxs.get(seq).put(src, new ConcurrentHashSet<>());
        }
        sendXAuxs.get(seq).get(src).add(round);
    }

    public static boolean contains(int seq, byte src, byte round) {
        if (sendXAuxs.containsKey(seq)
                && sendXAuxs.get(seq).containsKey(src)
                && sendXAuxs.get(seq).get(src).contains(round)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        sendXAuxs.remove(seq);
    }

}
