package xxxbft.msg;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class XPrepareMsg {

    /**
     * 请求序号（共识轮次）
     */
    @Getter
    private int seq;
    /**
     * proposal
     */
    @Getter
    private String data;
    /**
     * 摘要
     */
    @Getter
    private String digest;

    public XPrepareMsg(int seq, String data, String digest) {
        this.seq = seq;
        this.data = data;
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "XPrepareMsg{" +
                "seq=" + seq +
                ", data=char[" + data.length() + "]" +
                ", digest='" + digest + '\'' +
                '}';
    }

}
