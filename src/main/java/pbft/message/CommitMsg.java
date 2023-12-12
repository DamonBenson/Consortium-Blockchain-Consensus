package pbft.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommitMsg {

    @Getter
    private int seq;
    @Getter
    private int view;
    @Getter
    private String digest;

    public CommitMsg(int seq, int view, String digest) {
        this.seq = seq;
        this.view = view;
        this.digest = digest;
    }

    @Override
    public String toString() {
        return "CommitMsg{" +
                "seq=" + seq +
                ", view=" + view +
                ", digest='" + digest + '\'' +
                '}';
    }

}
