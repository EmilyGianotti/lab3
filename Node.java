public class Node {
    private int type; // type of edge
    private int latency; // latency of edge
    private int dependency; // direction of edge
    
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

     // 1 = data, 2 = conflict, 3 = serialization
    public int getType() {
        return type;
    }

    // 1 = data, 2 = conflict, 3 = serialization
    public void setType(int type) {
        this.type = type;
    }

    // 1 = data, 2 = conflict, 3 = serialization
    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    // 1 = forward, -1 = reverse
    public int getDependency() {
        return dependency;
    }

    // 1 = forward, -1 = reverse
    public void setDependency(int dependency) {
        this.dependency = dependency;
    }

    public String toString() {
        return "( type: " + Integer.toString(this.type) + ", latency: " + Integer.toString(this.latency) + " )";
    }
}
