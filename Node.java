public class Node {
    private int type;
    private int delay;
    
    public Node() {
        this.type = -1;
        this.delay = -1;
    }

    public Node(int type, int delay) {
        this.type = type;
        this.delay = delay;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String toString() {
        return "( type: " + Integer.toString(this.type) + ", delay: " + Integer.toString(this.delay) + " )";
    }
}
