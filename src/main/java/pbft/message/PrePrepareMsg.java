package pbft.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrePrepareMsg {

    @Getter
    private int seq;
    @Getter
    private int view;
    @Getter
    private String data;
    @Getter
    private String digest;

    public PrePrepareMsg(int seq, int view, String data, String digest) {
        this.seq = seq;
        this.view = view;
        this.data = data;
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "PrePrepareMsg{" +
                "seq=" + seq +
                ", view=" + view +
                ", data=char[" + data.length() + "]" +
                ", digest='" + digest + '\'' +
                '}';
    }

}
