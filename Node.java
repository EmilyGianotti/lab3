public class Node {
    private int type;
    private int latency;
    private int dependency;
    
    public Node() {
        this.type = -1;
        this.latency = -1;
        this.dependency = 0;

    }

    public Node(int type, int latency, int dependency) {
        this.type = type;
        this.latency = latency;
        this.dependency = dependency;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getDependency() {
        return dependency;
    }

    public void setDependency(int dependency) {
        this.dependency = dependency;
    }

    public String toString() {
        return "( type: " + Integer.toString(this.type) + ", latency: " + Integer.toString(this.latency) + " )";
    }
}
