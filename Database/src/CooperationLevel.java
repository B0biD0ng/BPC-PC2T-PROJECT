public enum CooperationLevel {
    POOR("spatna"),
    AVERAGE("prumerna"),
    GOOD("dobra");

    private final String label;

    CooperationLevel(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    public static CooperationLevel fromString(String s) {
        for (CooperationLevel lvl : values()) {
            if (lvl.label.equalsIgnoreCase(s) || lvl.name().equalsIgnoreCase(s)) return lvl;
        }
        throw new IllegalArgumentException("Neznámá úroveň: " + s);
    }

    @Override
    public String toString() { return label; }
}
