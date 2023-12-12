package pbft.protocol;

import p2p.NetworkInfo;
import pbft.message.CommitMsg;
import pbft.message.PrePrepareMsg;
import pbft.message.PrepareMsg;
import pbft.status.CommitSet;
import pbft.status.PrePrepareSet;
import pbft.status.PrepareSet;
import pbft.status.ReqSet;
import pojo.Node;
import pojo.msg.ReqMsg;
import utils.CryptoUtils;
import utils.LocalUtils;

public class MsgValidator {

    private static Node node = Node.getInstance();

    public static boolean isReqValid(ReqMsg reqMsg) {
        if (node.getIndex() != 0) {
            return false;
        }
        int seq = reqMsg.getSeq();
        if (seq < ReqSet.getCurrSeq()) {
            return false;
        }
        if (seq == ReqSet.getCurrSeq()) {
            return true;
        }
        ReqSet.addReq(reqMsg);
        return false;
    }

    public static boolean isPrePrepareValid(PrePrepareMsg prePrepareMsg) {
        int seq = prePrepareMsg.getSeq();
        int view = prePrepareMsg.getView();
        String proposed = prePrepareMsg.getData();
        String digest = prePrepareMsg.getDigest();
        byte[] vDigestByteArr = CryptoUtils.digest(node.getDigestAlgorithm(), proposed.getBytes());
        String vDigest = LocalUtils.bytes2Hex(vDigestByteArr);
        if (!digest.equals(vDigest)) {
            return false;
        }
        if (PrePrepareSet.contains(seq, view)) {
            return false;
        }
        PrePrepareSet.add(seq, view);
        return true;
    }

    public static boolean isPrepareValid(PrepareMsg prepareMsg, byte index) {
        int seq = prepareMsg.getSeq();
        int view = prepareMsg.getView();
        String digest = prepareMsg.getDigest();
        if (PrepareSet.size(seq, view, digest) >= NetworkInfo.getN() - NetworkInfo.getF()
                || PrepareSet.contains(seq, view, digest, index)) {
            return false;
        }
        PrepareSet.add(seq, view, digest, index);
        return true;
    }

    public static boolean isCommitValid(CommitMsg commitMsg, byte index) {
        int seq = commitMsg.getSeq();
        int view = commitMsg.getView();
        String digest = commitMsg.getDigest();
        if (CommitSet.size(seq, view, digest) >= NetworkInfo.getN() - NetworkInfo.getF()
                || CommitSet.contains(seq, view, digest, index)) {
            return false;
        }
        CommitSet.add(seq, view, digest, index);
        return true;
    }

}
