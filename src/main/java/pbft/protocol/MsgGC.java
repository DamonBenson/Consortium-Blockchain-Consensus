package pbft.protocol;

import pbft.status.*;
import pojo.Node;
import xxxbft.status.*;

public class MsgGC {

    public static void afterReq() {
        XReqSet.removeCurrReq();
    }

    public static void afterSendCommit(int seq, int view) {
        SendCommitSet.add(seq, view);
    }

    public static void afterFinishRound() {
        int currSeq = ReqSet.getCurrSeq();
        int discardSeq = currSeq - Node.getInstance().getWaterMark();
        ReqSet.removeReq(discardSeq);
        PrePrepareSet.remove(discardSeq);
        PrepareSet.remove(discardSeq);
        SendCommitSet.remove(discardSeq);
        CommitSet.remove(discardSeq);
        ReqSet.addCurrSeq();
    }

}
