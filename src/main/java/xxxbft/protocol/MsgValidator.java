package xxxbft.protocol;

import honeybadger.msg.BvalMsg;
import honeybadger.status.StatusSetUtils;
import p2p.NetworkInfo;
import pojo.Node;
import pojo.msg.ReqMsg;
import utils.CryptoUtils;
import utils.LocalUtils;
import xxxbft.msg.*;
import xxxbft.status.*;

public class MsgValidator {

    private static Node node = Node.getInstance();

    public static boolean isReqValid(ReqMsg reqMsg) {
        int seq = reqMsg.getSeq();
        if (seq < XReqSet.getCurrSeq()) {
            return false;
        }
        if (seq == XReqSet.getCurrSeq()) {
            return true;
        }
        XReqSet.addReq(reqMsg);
        return false;
    }

    public static boolean isXPrepareValid(XPrepareMsg xPrepareMsg, byte index) {
        int seq = xPrepareMsg.getSeq();
        String proposed = xPrepareMsg.getData();
        String digest = xPrepareMsg.getDigest();
        byte[] vDigestByteArr = CryptoUtils.digest(node.getDigestAlgorithm(), proposed.getBytes());
        String vDigest = LocalUtils.bytes2Hex(vDigestByteArr);
        if (!digest.equals(vDigest)) {
            return false;
        }
        if (XPrepareSet.contains(seq, index)) {
            return false;
        }
        XPrepareSet.add(seq, index);
        return true;
    }

    public static boolean isXPrepareVoteValid(XPrepareVoteMsg xPrepareVoteMsg, byte index) {
        int seq = xPrepareVoteMsg.getSeq();
        String digest = xPrepareVoteMsg.getDigest();
        if (XPrepareVoteSet.size(seq) >= NetworkInfo.getN() - NetworkInfo.getF()
                || XPrepareVoteSet.contains(seq, index)) {
            return false;
        }
        XPrepareVoteSet.add(seq, index);
        return true;
    }

    public static boolean isXCommitValid(XCommitMsg xCommitMsg, byte index) {
        int seq = xCommitMsg.getSeq();
        // TODO: 验证门限签名
        if (XCommitSet.contains(seq, index)) {
            return false;
        }
        XCommitSet.add(seq, index);
        return true;
    }

    public static boolean isXBvalValid(XBvalMsg xBvalMsg, byte index) {
        int seq = xBvalMsg.getSeq();
        byte src = xBvalMsg.getSrc();
        byte round = xBvalMsg.getRound();
        boolean est = xBvalMsg.isEst();
        if (XBvalSet.size(seq, src, round, est) >= NetworkInfo.getN() - NetworkInfo.getF()
                || XBvalSet.contains(seq, src, round, est, index)) {
            return false;
        }
        XBvalSet.add(seq, src, round, est, index);
        return true;
    }

    public static boolean isXAuxValid(XAuxMsg xAuxMsg, byte index) {
        int seq = xAuxMsg.getSeq();
        byte src = xAuxMsg.getSrc();
        byte round = xAuxMsg.getRound();
        boolean est = xAuxMsg.isEst();
        int valid = 0;
        if (XBinValueSet.contains(seq, src, round, false)) {
            valid += XAuxSet.size(seq, src, round, false);
        }
        if (XBinValueSet.contains(seq, src, round, true)) {
            valid += XAuxSet.size(seq, src, round, true);
        }
        if (valid >= NetworkInfo.getN() - NetworkInfo.getF()
                || XAuxSet.contains(seq, src, round, est, index)) {
            return false;
        }
        XAuxSet.add(seq, src, round, est, index);
        return true;
    }

}
