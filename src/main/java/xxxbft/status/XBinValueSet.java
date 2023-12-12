package xxxbft.status;

import java.util.concurrent.ConcurrentHashMap;

public class XBinValueSet {

    /**
     * seq->src->round->bin_values
     * bin_values=0：空集；1：只包含0；2：只包含1；3：包含0和1
     */
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, ConcurrentHashMap<Byte, Byte>>> xBinValues = new ConcurrentHashMap<>();

    synchronized public static void add(int seq, byte src, byte round, boolean est) {
        if (!xBinValues.containsKey(seq)) {
            xBinValues.put(seq, new ConcurrentHashMap<>());
        }
        if (!xBinValues.get(seq).containsKey(src)) {
            xBinValues.get(seq).put(src, new ConcurrentHashMap<>());
        }
        if (!xBinValues.get(seq).get(src).containsKey(round)) {
            xBinValues.get(seq).get(src).put(round, (byte) 0);
        }
        int binValue = xBinValues.get(seq).get(src).get(round);
        binValue |= (est ? 2 : 1);
        xBinValues.get(seq).get(src).put(round, (byte) binValue);
    }

    public static boolean contains(int seq, byte src, byte round, boolean est) {
        if (xBinValues.containsKey(seq)
                && xBinValues.get(seq).containsKey(src)
                && xBinValues.get(seq).get(src).containsKey(round)) {
            int binValue = xBinValues.get(seq).get(src).get(round);
            return (binValue & (est ? 2 : 1)) != 0;
        }
        return false;
    }

    synchronized public static void remove(int seq) {
        xBinValues.remove(seq);
    }

}
