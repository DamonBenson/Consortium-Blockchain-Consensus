package pbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class SendCommitSet {

    /**
     * seq->view
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashSet<Integer>> sendCommits = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, int view) {
        if (!sendCommits.containsKey(seq)) {
            sendCommits.put(seq, new ConcurrentHashSet<>());
        }
        sendCommits.get(seq).add(view);
    }

    public static boolean contains(int seq, int view) {
        if(sendCommits.containsKey(seq)
                && sendCommits.get(seq).contains(view)) {
            return true;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        sendCommits.remove(seq);
    }

}
