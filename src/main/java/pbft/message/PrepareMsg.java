package pbft.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PrepareMsg {

    @Getter
    private int seq;
    @Getter
    private int view;
    @Getter
    private String digest;

    public PrepareMsg(int seq, int view, String digest) {
        this.seq = seq;
        this.view = view;
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "PrepareMsg{" +
                "seq=" + seq +
                ", view=" + view +
                ", digest='" + digest + '\'' +
                '}';
    }

}
