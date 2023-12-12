package xxxbft.msg;

import lombok.Getter;

public class XCommitMsg {

    /**
     * 请求序号（共识轮次）
     */
    @Getter
    private int seq;
    /**
     * 摘要
     */
    @Getter
    private String digest;
    /**
     * 门限签名
     */
    @Getter
    private String combinedSign;

    public XCommitMsg(int seq, String digest, String combinedSign) {
        this.seq = seq;
        this.digest = digest;
        this.combinedSign = combinedSign;
    }

    @Override
    public String toString() {
        return "XCommitMsg{" +
                "seq=" + seq +
                ", digest='" + digest + '\'' +
                ", combinedSign='" + combinedSign + '\'' +
                '}';
    }

}
