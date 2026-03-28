package model;

public class Collaboration {
    private int colleagueId;
    private String colleagueFullName;
    private CollaborationLevel level;

    public Collaboration(int colleagueId, String colleagueFullName, CollaborationLevel level) {
        this.colleagueId = colleagueId;
        this.colleagueFullName = colleagueFullName;
        this.level = level;
    }

    public int getColleagueId() { return colleagueId; }
    public String getColleagueFullName() { return colleagueFullName; }
    public CollaborationLevel getLevel() { return level; }

    public void setColleagueFullName(String name) { this.colleagueFullName = name; }

    @Override
    public String toString() {
        return String.format("  -> ID:%d %-25s [%s]", colleagueId, colleagueFullName, level);
    }
}
