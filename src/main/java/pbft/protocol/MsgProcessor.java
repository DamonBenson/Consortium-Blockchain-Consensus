package pbft.protocol;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import p2p.NetworkInfo;
import pbft.message.CommitMsg;
import pbft.message.PrePrepareMsg;
import pbft.message.PrepareMsg;
import pbft.status.*;
import pojo.Node;
import pojo.msg.MsgType;
import pojo.msg.RawMsg;
import pojo.msg.ReqMsg;
import utils.CryptoUtils;
import utils.LocalUtils;
import utils.SendUtils;

@Slf4j
public class MsgProcessor {

    private static Node node = Node.getInstance();
    private static Gson gson = new Gson();
    private final static int N = NetworkInfo.getN();
    private final static int F = NetworkInfo.getF();

    public static void req(ReqMsg reqMsg) {

        if (!MsgValidator.isReqValid(reqMsg)) {
            return;
        }

        int seq = reqMsg.getSeq();
        String req = reqMsg.getBody();
        byte[] digestByteArr = CryptoUtils.digest(node.getDigestAlgorithm(), req.getBytes());
        String digest = LocalUtils.bytes2Hex(digestByteArr);

        PrePrepareMsg prePrepareMsg = new PrePrepareMsg(seq, 0, req, digest);
        String json = gson.toJson(prePrepareMsg);
        RawMsg rawMsg = new RawMsg(MsgType.PRE_PREPARE, json, null);

        SendUtils.publishToServer(rawMsg);

        MsgGC.afterReq();

    }

    public static void prePrepare(PrePrepareMsg prePrepareMsg) {

        if (!MsgValidator.isPrePrepareValid(prePrepareMsg)) {
            return;
        }

        int seq = prePrepareMsg.getSeq();
        int view = prePrepareMsg.getView();
        String digest = prePrepareMsg.getDigest();

        PrepareMsg prepareMsg = new PrepareMsg(seq, view, digest);
        String json = gson.toJson(prepareMsg);
        RawMsg rawMsg = new RawMsg(MsgType.PREPARE, json, null);

        SendUtils.publishToServer(rawMsg);

    }

    public static void prepare(PrepareMsg prepareMsg, byte index) {

        if (!MsgValidator.isPrepareValid(prepareMsg, index)) {
            return;
        }

        int seq = prepareMsg.getSeq();
        int view = prepareMsg.getView();
        String digest = prepareMsg.getDigest();

        if (PrepareSet.size(seq, view, digest) >= N - F) {
            if (!SendCommitSet.contains(seq, view)) {
                MsgGC.afterSendCommit(seq, view);
                CommitMsg commitMsg = new CommitMsg(seq, view, digest);
                String json = gson.toJson(commitMsg);
                RawMsg rawMsg = new RawMsg(MsgType.COMMIT, json, null);
                SendUtils.publishToServer(rawMsg);
            }
        }

    }

    public static void commit(CommitMsg commitMsg, byte index) {

        if (!MsgValidator.isCommitValid(commitMsg, index)) {
            return;
        }

        int seq = commitMsg.getSeq();
        int view = commitMsg.getView();
        String digest = commitMsg.getDigest();

        if (CommitSet.size(seq, view, digest) >= N - F) {
            if (!OutputSet.contains(seq)) {
                log.info(String.format("[OUTPUT]: seq=%s", seq));
                OutputSet.add(seq);
                MsgGC.afterFinishRound();
                if (ReqSet.getCurrReq() != null) {
                    req(ReqSet.getCurrReq());
                }
            }
        }

    }



}
