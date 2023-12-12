package xxxbft.protocol;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import xxxbft.msg.*;
import p2p.NetworkInfo;
import pojo.Node;
import pojo.msg.MsgType;
import pojo.msg.RawMsg;
import pojo.msg.ReqMsg;
import utils.*;
import xxxbft.status.*;

import java.util.Arrays;

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

        int seg = (int) Math.ceil(req.length() / NetworkInfo.getN());
        String proposed = req.substring(node.getIndex() * seg, Math.min(node.getIndex() * seg + seg, req.length()));
        byte[] digestByteArr = CryptoUtils.digest(node.getDigestAlgorithm(), proposed.getBytes());
        String digest = LocalUtils.bytes2Hex(digestByteArr);

        XPrepareMsg xPrepareMsg = new XPrepareMsg(seq, proposed, digest);
        String json = gson.toJson(xPrepareMsg);
        RawMsg rawMsg = new RawMsg(MsgType.X_PREPARE, json, null);

        SendUtils.publishToServer(rawMsg);

        MsgGC.afterReq();

    }

    public static void prepare(XPrepareMsg xPrepareMsg, byte index) {

        if (!MsgValidator.isXPrepareValid(xPrepareMsg, index)) {
            return;
        }

        int seq = xPrepareMsg.getSeq();
        String digest = xPrepareMsg.getDigest();

        XPrepareVoteMsg xPrepareVoteMsg = new XPrepareVoteMsg(seq, digest);
        String json = gson.toJson(xPrepareVoteMsg);
        RawMsg rawMsg = new RawMsg(MsgType.X_PREPARE_VOTE, json, null);

        SendUtils.publishToServer(rawMsg);

    }

    public static void prepareVote(XPrepareVoteMsg xPrepareVoteMsg, byte index) {

        if (!MsgValidator.isXPrepareVoteValid(xPrepareVoteMsg, index)) {
            return;
        }

        int seq = xPrepareVoteMsg.getSeq();
        String digest = xPrepareVoteMsg.getDigest();

        if (XPrepareVoteSet.size(seq) >= N - F) {
            if (!SendXCommitSet.contains(seq)) {
                MsgGC.afterSendXCommit(seq);
                // TODO: 门限签名
                byte[] bytes = new byte[1024];
                Arrays.fill(bytes, (byte) 0);
                String combinedSign = LocalUtils.bytes2Hex(bytes);
                XCommitMsg xCommitMsg = new XCommitMsg(seq, digest, combinedSign);
                String json = gson.toJson(xCommitMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_COMMIT, json, null);
                SendUtils.publishToServer(rawMsg);
            }
        }

    }

    public static void commit(XCommitMsg xCommitMsg, byte index) {

        if (!MsgValidator.isXCommitValid(xCommitMsg, index)) {
            return;
        }

        int seq = xCommitMsg.getSeq();

        if (!SendXBvalSet.contains(seq, index, (byte) 0, true)) {
            MsgGC.afterSendXBval(seq, index, (byte) 0, true);
            XBvalMsg xBvalMsg = new XBvalMsg(seq, index, (byte) 0, true);
            String json = gson.toJson(xBvalMsg);
            RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
            SendUtils.publishToServer(rawMsg);
        }

    }

    public static void bval(XBvalMsg xBvalMsg, byte index) {

        if (!MsgValidator.isXBvalValid(xBvalMsg, index)) {
            return;
        }

        int seq = xBvalMsg.getSeq();
        byte src = xBvalMsg.getSrc();
        byte round = xBvalMsg.getRound();
        boolean est = xBvalMsg.isEst();

        int received = XBvalSet.size(seq, src, round, est);
        if (received >= F + 1) {
            if (!SendXBvalSet.contains(seq, src, round, est)) {
                MsgGC.afterSendXBval(seq, src, round, est);
                XBvalMsg myXBvalMsg = new XBvalMsg(seq, src, round, est);
                String json = gson.toJson(myXBvalMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                SendUtils.publishToServer(rawMsg);
            }
            if (received >= N - F) {
                if (!SendXAuxSet.contains(seq, src, round)) {
                    MsgGC.afterSendXAux(seq, src, round);
                    XBinValueSet.add(seq, src, round, est);
                    XAuxMsg xAuxMsg = new XAuxMsg(seq, src, round, est);
                    String json = gson.toJson(xAuxMsg);
                    RawMsg rawMsg = new RawMsg(MsgType.X_AUX, json, null);
                    SendUtils.publishToServer(rawMsg);
                }
            }
        }

        int valid = 0;
        if (XBinValueSet.contains(seq, src, round, false)) {
            valid += XAuxSet.size(seq, src, round, false);
        }
        if (XBinValueSet.contains(seq, src, round, true)) {
            valid += XAuxSet.size(seq, src, round, true);
        }
        if (valid >= N - F) {
            finishRound(seq, src, round);
        }

    }

    public static void aux(XAuxMsg xAuxMsg, byte index) {

        if (!MsgValidator.isXAuxValid(xAuxMsg, index)) {
            return;
        }

        int seq = xAuxMsg.getSeq();
        byte src = xAuxMsg.getSrc();
        byte round = xAuxMsg.getRound();

        int valid = 0;
        if (XBinValueSet.contains(seq, src, round, false)) {
            valid += XAuxSet.size(seq, src, round, false);
        }
        if (XBinValueSet.contains(seq, src, round, true)) {
            valid += XAuxSet.size(seq, src, round, true);
        }
        if (valid >= N - F) {
            finishRound(seq, src, round);
        }

    }

    synchronized public static void finishRound(int seq, byte src, byte round) {

        // TODO: 公共随机硬币算法
        boolean coin = (round % 2) == 1;

        if (XOutputSet.contains(seq, src, coin)) {
            return;
        }

        if (!XBinValueSet.contains(seq, src, round, false) && !XBinValueSet.contains(seq, src, round, true)) {
            return;
        }

        if (XBinValueSet.contains(seq, src, round, false) && XBinValueSet.contains(seq, src, round, true)) {
            if (!coin && !SendXBvalSet.contains(seq, src, (byte) (round + 1), false)) {
                MsgGC.afterSendXBval(seq, src, (byte) (round + 1), false);
                XBvalMsg xBvalMsg = new XBvalMsg(seq, src, (byte) (round + 1), false);
                String json = gson.toJson(xBvalMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                SendUtils.publishToServer(rawMsg);
                return;
            }
            if (coin && !SendXBvalSet.contains(seq, src, (byte) (round + 1), true)) {
                MsgGC.afterSendXBval(seq, src, (byte) (round + 1), true);
                XBvalMsg xBvalMsg = new XBvalMsg(seq, src, (byte) (round + 1), true);
                String json = gson.toJson(xBvalMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                SendUtils.publishToServer(rawMsg);
                return;
            }
            return;
        }

        if (XBinValueSet.contains(seq, src, round, false)) {
            if (!coin && !XOutputSet.contains(seq, src)) {
                log.info(String.format("[OUTPUT]: seq=%s, src=%s, est=false", seq, src));
                XOutputSet.add(seq, src, false);
                // 如果一轮Honey Badger共识中，所有节点提议对应的BA共识都完成，则可以进入下一轮Honey Badger共识
                if (XOutputSet.size(seq) == N) {
                    MsgGC.afterFinishRound();
                    if (XReqSet.getCurrReq() != null) {
                        req(XReqSet.getCurrReq());
                    }
                }
            }
            if(!SendXBvalSet.contains(seq, src, (byte) (round + 1), false)) {
                MsgGC.afterSendXBval(seq, src, (byte) (round + 1), false);
                XBvalMsg xBvalMsg = new XBvalMsg(seq, src, (byte) (round + 1), false);
                String json = gson.toJson(xBvalMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                SendUtils.publishToServer(rawMsg);
            }
            return;
        }

        if (XBinValueSet.contains(seq, src, round, true)) {
            if (coin && !XOutputSet.contains(seq, src)) {
                log.info(String.format("[OUTPUT]: seq=%s, src=%s, est=true", seq, src));
                XOutputSet.add(seq, src, true);
                // 与case 1不同的部分，有一个BA共识output了1，那可能使output了1的BA共识总数超过N-f
                // 这时其他的BA共识如果还没有开始，那么设置est为0并强制开始（为了防止有f个恶意静默节点的情况）
                if (XOutputSet.size(seq) >= N - F) {
                    for (byte i = 0; i < N; i++) {
                        if (!XOutputSet.contains(seq, i)
                                && !SendXBvalSet.contains(seq, i, (byte) 0, false)
                                && !SendXBvalSet.contains(seq, i, (byte) 0, true)) {
                            MsgGC.afterSendXBval(seq, i, (byte) 0, false);
                            XBvalMsg xBvalMsg = new XBvalMsg(seq, i, (byte) 0, false);
                            String json = gson.toJson(xBvalMsg);
                            RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                            SendUtils.publishToServer(rawMsg);
                        }
                    }
                }
                if (XOutputSet.size(seq) == N) {
                    MsgGC.afterFinishRound();
                    if (XReqSet.getCurrReq() != null) {
                        req(XReqSet.getCurrReq());
                    }
                }
            }
            if(!SendXBvalSet.contains(seq, src, (byte) (round + 1), true)) {
                MsgGC.afterSendXBval(seq, src, (byte) (round + 1), true);
                XBvalMsg xBvalMsg = new XBvalMsg(seq, src, (byte) (round + 1), true);
                String json = gson.toJson(xBvalMsg);
                RawMsg rawMsg = new RawMsg(MsgType.X_BVAL, json, null);
                SendUtils.publishToServer(rawMsg);
            }
            return;
        }

    }

}
