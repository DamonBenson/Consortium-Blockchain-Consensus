package pbft.status;

import lombok.Getter;
import pojo.msg.ReqMsg;

import java.util.concurrent.ConcurrentHashMap;

public class ReqSet {

    @Getter
    private static int currSeq = 0;

    /**
     * seq->msg
     */
    private static ConcurrentHashMap<Integer, ReqMsg> reqs = new ConcurrentHashMap<>();

    public static void addReq(ReqMsg reqMsg) {
        int seq = reqMsg.getSeq();
        reqs.put(seq, reqMsg);
    }

    public static ReqMsg getCurrReq() {
        return reqs.get(currSeq);
    }

    public static void removeReq(int seq) {
        reqs.remove(seq);
    }

    public static void removeCurrReq() {
        reqs.remove(currSeq);
    }

    public static void addCurrSeq() {
        currSeq++;
    }

}
