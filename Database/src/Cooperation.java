import java.io.Serializable;

public class Cooperation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int colleagueId;
    private CooperationLevel level;

    public Cooperation(int colleagueId, CooperationLevel level) {
        this.colleagueId = colleagueId;
        this.level = level;
    }

    public int getColleagueId() { return colleagueId; }
    public CooperationLevel getLevel() { return level; }
    public void setLevel(CooperationLevel level) { this.level = level; }

    @Override
    public String toString() {
        return "KolégaID=" + colleagueId + ", úroveň=" + level;
    }
}
