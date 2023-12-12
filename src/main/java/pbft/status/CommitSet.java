package pbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class CommitSet {

    /**
     * seq->view->digest->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashSet<Byte>>>> commits = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, int view, String digest, byte index) {
        if (!commits.containsKey(seq)) {
            commits.put(seq, new ConcurrentHashMap<>());
        }
        if (!commits.get(seq).containsKey(view)) {
            commits.get(seq).put(view, new ConcurrentHashMap<>());
        }
        if (!commits.get(seq).get(view).containsKey(digest)) {
            commits.get(seq).get(view).put(digest, new ConcurrentHashSet<>());
        }
        commits.get(seq).get(view).get(digest).add(index);
    }

    public static boolean contains(int seq, int view, String digest, byte index) {
        if(commits.containsKey(seq)
                && commits.get(seq).containsKey(view)
                && commits.get(seq).get(view).containsKey(digest)
                && commits.get(seq).get(view).get(digest).contains(index)) {
            return true;
        }
        return false;
    }

    public static int size(int seq, int view, String digest) {
        if(commits.containsKey(seq)
                && commits.get(seq).containsKey(view)
                && commits.get(seq).get(view).containsKey(digest)) {
            return commits.get(seq).get(view).get(digest).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        commits.remove(seq);
    }

}
