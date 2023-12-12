package xxxbft.msg;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class XPrepareVoteMsg {

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

    public XPrepareVoteMsg(int seq, String digest) {
        this.seq = seq;
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "XPrepareVoteMsg{" +
                "seq=" + seq +
                ", digest='" + digest + '\'' +
                '}';
    }

}
