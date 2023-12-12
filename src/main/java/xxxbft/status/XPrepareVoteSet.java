package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;
import p2p.NetworkInfo;

import java.util.concurrent.ConcurrentHashMap;

public class XPrepareVoteSet {

    // TODO: 为了实现门限签名，这个集合中还需要记录所有节点PREPARE-VOTE消息的签名
    /**
     * seq->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashSet<Byte>> xPrepareVotes = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte index) {
        if (!xPrepareVotes.containsKey(seq)) {
            xPrepareVotes.put(seq, new ConcurrentHashSet<>());
        }
        xPrepareVotes.get(seq).add(index);
    }

    public static boolean contains(int seq, byte index) {
        if (xPrepareVotes.containsKey(seq)
                && xPrepareVotes.get(seq).contains(index)) {
            return true;
        }
        return false;
    }

    public static int size(int seq) {
        if (xPrepareVotes.containsKey(seq)) {
            return xPrepareVotes.get(seq).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        xPrepareVotes.remove(seq);
    }

}
