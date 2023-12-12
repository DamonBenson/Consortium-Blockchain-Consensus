package xxxbft.protocol;

import pojo.Node;
import xxxbft.status.*;

public class MsgGC {

    public static void afterReq() {
        XReqSet.removeCurrReq();
    }

    public static void afterSendXCommit(int seq) {
        SendXCommitSet.add(seq);
    }

    public static void afterSendXBval(int seq, byte src, byte round, boolean est) {
        SendXBvalSet.add(seq, src, round, est);
    }

    public static void afterSendXAux(int seq, byte src, byte round) {
        SendXAuxSet.add(seq, src, round);
    }

    public static void afterFinishRound() {
        int currSeq = XReqSet.getCurrSeq();
        int discardSeq = currSeq - Node.getInstance().getWaterMark();
        XReqSet.removeReq(discardSeq);
        XPrepareSet.remove(discardSeq);
        XPrepareVoteSet.remove(discardSeq);
        SendXCommitSet.remove(discardSeq);
        XCommitSet.remove(discardSeq);
        XReqSet.addCurrSeq();
    }

}
