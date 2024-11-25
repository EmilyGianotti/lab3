// import statements here

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;

public class Scheduler {
    public static OpList internalOpList;
    public static InternalRep DGToIR[];
    public static Integer latencies[] = {6, 1, 6, 1, 1, 3, 1, 1, 1, 1};
    public static Map<Integer, Map<Integer, Node>> graph;

    public static void main(String[] args) throws Exception {
        int i = 0;
        String filename = "";
        if (args.length == 0) { // no filename provided
            System.err.println("ERROR: No flag or filename provided.");
            System.out.println("COMP 412, Instruction Scheduling (lab 3)\n" + //
                                "Command Syntax:\n" + //
                                "        schedule filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n"); 
        } else {
            while (i < args.length) { // run through args
                if (args[i].equals("-h")) { // help flag
                    System.out.println("COMP 412, Instruction Scheduling (lab 3)\n" + //
                                "Command Syntax:\n" + //
                                "        schedule filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n");
                    return;
                } else if (args[i].charAt(0) == ('-')) { // invalid flag
                    System.err.println("ERROR: The " + args[i] + " flag is not allowed.");
                    System.out.println("COMP 412, Instruction Scheduling (lab 3)\n" + //
                                "Command Syntax:\n" + //
                                "        schedule filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n");
                    return;
                } else if (args[i].charAt(0) != '-'){
                    filename = args[i];
                }
                i++;
            }
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename)); 
            FrontEnd frontEnd = new FrontEnd();
            internalOpList = frontEnd.doParse(br); // parse ILOC block
            internalOpList = Renamer.rename(internalOpList.size(), internalOpList.findMaxSR(), internalOpList);
            internalOpList.traverseILOC();
            DGToIR = new InternalRep[internalOpList.size()];
            graph = buildGraph();
            graphToString(graph);
            drawGraph(graph);
            ArrayList<Integer> roots = findRoots(graph);
            for(int r = 0; r < roots.size(); r++) {
                prioritize(roots.get(r), 0);
            }
        } catch (Exception e) {
            System.err.println("ERROR:");
            e.printStackTrace();
        }
    }

    /**
     * Creates dependency graph representative of ILOC Block
     * @return Map whose keys are integers that represent X and values are Maps of integers to nodes that represent Y
     */
    public static Map<Integer, Map<Integer, Node>> buildGraph() {
        // create an empty map, M
        InternalRep M[] = new InternalRep[internalOpList.getMaxVR()+1];
        Map<Integer, Map<Integer, Node>> DG = new HashMap<>();
        // create linkedlists to store stores, outputs, and loads
        ArrayList<Integer> stores = new ArrayList<Integer>();
        ArrayList<Integer> outputs = new ArrayList<Integer>();
        ArrayList<Integer> loads = new ArrayList<Integer>();
        // walk the block, top to bottom
        InternalRep current = internalOpList.getHead();
        int line = 0;
        int defLine = -1;
        InternalRep defOp = new InternalRep();
        Node defNode;
        int currOp;
        // op is operation, o is node
        while (current != null) {
            // at each operation op
            currOp = current.getOperation();
            current.setLine(line);
            if (currOp != 9) {
                Map<Integer, Node> currentEdges = new HashMap<>();
                // if op defines VRi:
                if (currOp != 2) {
                    if (current.getOperand3()[1] != -1) {
                        System.out.println("in def");
                        // set M(VRi) to op
                        System.out.println("def is r" + Integer.toString(current.getOperand3()[1]) + " and line is " + Integer.toString(current.getLine()));
                        M[current.getOperand3()[1]] = current;
                    }

                }
                // for each VRj used in op
                int useOne = current.getOperand1()[1];
                System.out.println("in use 1");
                if (useOne != -1) {
                    if (useOne != -1) {
                        // add an edge from o use to the def node in M(VRj)
                        defOp = M[useOne];
                        defLine = defOp.getLine();
                        defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.get(defLine).put(line, new Node(currOp, latencies[currOp], -1));
                    }
                }

                int useTwo = current.getOperand2()[1];
                System.out.println("in use 2");
                if (useTwo != -1) {
                    if (useTwo != -1) {
                        // add an edge from o to the node in M(VRj)
                        defOp = M[useTwo];
                        defLine = defOp.getLine();
                        System.out.println("defline is " + Integer.toString(defLine));
                        defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.toString();
                        DG.get(defLine).put(line, new Node(currOp, latencies[currOp], -1));
                    }
                }

                // store
                if (currOp == 2) {
                    int useThree = current.getOperand2()[1];
                    if (useThree != -1) {
                        if (useThree != -1) {
                            // add an edge from o to the node in M(VRj)
                            defOp = M[useThree];
                            defLine = defOp.getLine();
                            defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                            currentEdges.put(defLine, defNode);
                            // add reverse edge from def node to o use
                            DG.get(defLine).put(line, new Node(currOp, latencies[currOp], -1));
                        }
                    }
                }

                // if o is a load, store, or output
                if (currOp == 0) { // load
                    // add edge from op to most recent store
                    if (stores.size() > 0) {
                        // want line, type, latency, and direction of most recent store
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(DGToIR[stores.get(stores.size()-1)].getOperation(), 
                                        latencies[DGToIR[stores.get(stores.size()-1)].getOperation()], 1));
                        // add reverse edge from most recent store to op
                        DG.get(stores.get(stores.size()-1)).put(line, new Node(currOp, latencies[currOp], -1));
                    }
                    loads.add(line);
                }
                
                if (currOp == 2) { // store
                    // add edge from op to most recent store
                    if (stores.size() > 0) {
                        // want line, type, latency, and direction of most recent store
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(DGToIR[stores.get(stores.size()-1)].getOperation(), 
                                        latencies[DGToIR[stores.get(stores.size()-1)].getOperation()], 1));
                        // add reverse edge from most recent store to op
                        DG.get(stores.get(stores.size()-1)).put(line, new Node(currOp, latencies[currOp], -1));
                    }
                    // add edges from op to each previous load
                    if (loads.size() > 0) {
                        for (int i = 0; i < loads.size(); i++) {
                            // want line, type, latency, and direction of load
                            currentEdges.put(loads.get(i), new Node(DGToIR[loads.get(i)].getOperation(),
                                            latencies[DGToIR[loads.get(i)].getOperation()], 1));
                            // add reverse edge from most recent store to op
                            DG.get(loads.get(i)).put(line, new Node(currOp, latencies[currOp], -1));
                        }
                    }
                    // add edges from op to each previous output
                    if (outputs.size() > 0) {
                        for (int i = 0; i < outputs.size(); i++) {
                            // want line, type, latency, and direction of load
                            currentEdges.put(outputs.get(i), new Node(DGToIR[outputs.get(i)].getOperation(),
                                            latencies[DGToIR[outputs.get(i)].getOperation()], 1));
                            // add reverse edge from most recent store to op
                            DG.get(outputs.get(i)).put(line, new Node(currOp, latencies[currOp], -1));
                        }
                    }
                    stores.add(line);
                }

                if (currOp == 8) { //output
                    // add edge from op to most recent store
                    if (stores.size() > 0) {
                        // want line, type, latency, and direction of most recent store
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(DGToIR[stores.get(stores.size()-1)].getOperation(), 
                                        latencies[DGToIR[stores.get(stores.size()-1)].getOperation()], 1));
                    }
                    // add reverse edge from most recent store to op
                    DG.get(stores.get(stores.size()-1)).put(line, new Node(currOp, latencies[currOp], -1));

                    // add edge from op to most recent output
                    if (outputs.size() > 0) {
                        // want line, type, latency, and direction of most recent output
                        currentEdges.put(outputs.get(outputs.size()-1), 
                                        new Node(DGToIR[outputs.get(outputs.size()-1)].getOperation(), 
                                        latencies[DGToIR[outputs.get(outputs.size()-1)].getOperation()], 1));
                        // add reverse edge from most recent store to op
                        DG.get(outputs.get(outputs.size()-1)).put(line, new Node(currOp, latencies[currOp], -1));
                    }
                }
                // insert current node and it's associated edges into the directed graph
                DG.put(line, currentEdges);
                // add IR to quick reference table to convert from node to IR of op
                DGToIR[line] = current;
                // add current op to appropriate op list
                if (currOp == 0) {
                    loads.add(line);
                } else if (currOp == 2) {
                    stores.add(line);
                } else if (currOp == 8) {
                    outputs.add(line);
                }
            }
            System.out.println("made it past " + current.printILOCCP1() + "\n");
            current = current.next;
            line++;
        }
        return DG;
    }

    /**
     * Create .dot file for Graphviz from directional graph that represents dependencies between ILOC ops
     * @param graph Map<Integer, Map<Integer, Node>> that represents dependencies between ILOC ops in a given block
     * @throws IOException in case something goes wrong with creating, writing to, and closing the .dot file
     */
    public static void drawGraph(Map<Integer, Map<Integer, Node>> graph) throws IOException{
        try {
            // create graph file
            FileWriter graphWriter = new FileWriter("ILOCblock.dot");
            graphWriter.write("digraph slides\n{\n");
            // create Sting to store edges
            String edges = "";
            // write to graph file
            for(Map.Entry<Integer, Map<Integer, Node>> nodeEntry : graph.entrySet()) {
                Integer nodeLine = nodeEntry.getKey();
                // create node for current OP
                graphWriter.write(nodeLine.toString() + " [label = \"" + nodeLine.toString() + "\n" 
                                + DGToIR[nodeLine].printILOCCP1() + "\"];\n");
                // edge work, will put in a string to concatenate at the end
                for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                    Integer otherNodeLine = edgeEntry.getKey();
                    Node otherNode = edgeEntry.getValue();
                    Integer edgeDirection = otherNode.getDependency();
                    if (edgeDirection == 1) {
                        edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[color=\"blue\"];\n";
                    } else {
                        edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[color=\"red\"];\n";
                    }
                    
                    // if (edgeDirection == 1) {
                    //     System.out.println("forward edge from " + nodeLine.toString() + " to " + otherNodeLine.toString());
                    //     edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + ";\n";
                    // } else {
                    //     System.out.println("reverse edge from " + nodeLine.toString() + " to " + otherNodeLine.toString());
                    //     edges = edges + otherNodeLine.toString() + " -> " + nodeLine.toString() + ";\n";
                    // }
                }
            }
            // add edges to bottom of file
            graphWriter.write(edges + "}");
            graphWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Prints out graph with nodes and edge directions
     * @param graph Map<Integer, Map<Integer, Node>> that represents dependencies between ILOC ops in a given block
     */
    public static void graphToString(Map<Integer, Map<Integer, Node>> graph) {
        for(Map.Entry<Integer, Map<Integer, Node>> nodeEntry : graph.entrySet()) {
            System.out.println("Node " + nodeEntry.getKey() + "\t" + DGToIR[nodeEntry.getKey()].printILOCCP1());
            // Integer nodeLine = nodeEntry.getKey();
            for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                Integer otherNodeLine = edgeEntry.getKey();
                Node otherNode = edgeEntry.getValue();
                Integer edgeDirection = otherNode.getDependency();
                if (edgeDirection == 1) {
                    System.out.println("forward edge to " + otherNodeLine.toString());
                } else {
                    System.out.println("reverse edge to " + otherNodeLine.toString());
                }
                System.out.println("priority of " + Integer.toString(otherNode.getPriority()));
            }
            System.out.println("\n");
        }
    }

    /**
     * Finds any independent (no other nodes depend on them) nodes in graph
     * @param graph Map<Integer, Map<Integer, Node>> that represents dependencies between ILOC ops in a given block
     * @return an ArrayList of integers that represent any independent nodes in graph
     */
    public static ArrayList<Integer> findRoots(Map<Integer, Map<Integer, Node>> graph) {
        ArrayList<Integer> roots = new ArrayList<Integer>();
        int independent;
        for(Map.Entry<Integer, Map<Integer, Node>> nodeEntry : graph.entrySet()) {
            independent = 1;
            for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                // if there is a reverse edge in the current node's POV (a forward edge from another node)
                if (edgeEntry.getValue().getDependency() == -1) {
                    // the node is not independent
                    independent = 0;
                    break;
                }
            }
            // check independence for root
            if (independent == 1) {
                roots.add(nodeEntry.getKey());
            }
        }
        return roots;
    }

    /**
     * DFS tree-walk algorithm that computes latency-weighted distance priorities of nodes in graph
     * @param node an integer representing a node in the ILOC block dependency graph
     * @param latency
     */
    public static void prioritize(int node, int latency) {
        // TODO: Do this ITERATIVELY
        
        // retrieve all nodes connected to current node
        Map<Integer, Node> edgeNodes = graph.get(node);
        // iterate over all connected nodes
        for (Map.Entry<Integer, Node> edgeEntry : edgeNodes.entrySet()) {
            // Check if edge is forward or reverse (we're only considering forward edges)
            Node otherNode = edgeEntry.getValue();
            // if (otherNode.getDependency() == -1) {
            //     continue;
            // }
            
            // If the node is valid, we need to check if its latency < the new latency
            if (otherNode.getPriority() < (otherNode.getPriority() + latency)) {
                otherNode.setPriority(otherNode.getPriority() + latency);
            } else {
                // We've already maxed out this path
                continue;
            }
            // Recurse on valid edges
            prioritize(edgeEntry.getKey(), otherNode.getPriority());
        }
    }
}
