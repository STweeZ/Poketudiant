public enum Direction {
    LEFT("left"),RIGHT("right"),UP("up"),DOWN("down");

    public final String label;

    private Direction(String label) {
        this.label = label;
    }
}
