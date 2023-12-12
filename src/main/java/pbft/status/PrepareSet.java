package pbft.status;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;

public class PrepareSet {

    /**
     * seq->view->digest->index
     */
    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashSet<Byte>>>> prepares = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, int view, String digest, byte index) {
        if (!prepares.containsKey(seq)) {
            prepares.put(seq, new ConcurrentHashMap<>());
        }
        if (!prepares.get(seq).containsKey(view)) {
            prepares.get(seq).put(view, new ConcurrentHashMap<>());
        }
        if (!prepares.get(seq).get(view).containsKey(digest)) {
            prepares.get(seq).get(view).put(digest, new ConcurrentHashSet<>());
        }
        prepares.get(seq).get(view).get(digest).add(index);
    }

    public static boolean contains(int seq, int view, String digest, byte index) {
        if(prepares.containsKey(seq)
                && prepares.get(seq).containsKey(view)
                && prepares.get(seq).get(view).containsKey(digest)
                && prepares.get(seq).get(view).get(digest).contains(index)) {
            return true;
        }
        return false;
    }

    public static int size(int seq, int view, String digest) {
        if(prepares.containsKey(seq)
                && prepares.get(seq).containsKey(view)
                && prepares.get(seq).get(view).containsKey(digest)) {
            return prepares.get(seq).get(view).get(digest).size();
        }
        return 0;
    }

    synchronized public static void remove(int seq) {
        prepares.remove(seq);
    }

}
