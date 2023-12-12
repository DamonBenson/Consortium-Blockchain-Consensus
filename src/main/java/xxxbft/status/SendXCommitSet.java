package xxxbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;
import p2p.NetworkInfo;

import java.util.concurrent.ConcurrentHashMap;

public class SendXCommitSet {

    /**
     * seq
     */
    private static ConcurrentHashSet<Integer> sendXCommits = new ConcurrentHashSet<>();

    synchronized public static void add(int seq) {
        sendXCommits.add(seq);
    }

    synchronized public static boolean contains(int seq) {
        return sendXCommits.contains(seq);
    }

    synchronized public static void remove(int seq) {
        sendXCommits.remove(seq);
    }

}
