package model;

public enum CollaborationLevel {
    POOR("špatná"),
    AVERAGE("průměrná"),
    GOOD("dobrá");

    private final String display;

    CollaborationLevel(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static CollaborationLevel fromString(String s) {
        for (CollaborationLevel lvl : values()) {
            if (lvl.name().equalsIgnoreCase(s) || lvl.display.equalsIgnoreCase(s)) {
                return lvl;
            }
        }
        throw new IllegalArgumentException("Neznámá úroveň spolupráce: " + s);
    }

    @Override
    public String toString() {
        return display;
    }
}
