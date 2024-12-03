// import statements here

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Stack;

public class Scheduler {
    public static OpList internalOpList;
    public static InternalRep DGToIR[];
    public static Integer latencies[] = {6, 1, 6, 1, 1, 3, 1, 1, 1, 1};
    public static Map<Integer, Map<Integer, Node>> graph;
    public static int priorities[];

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
            // internalOpList.traverseILOC();
            DGToIR = new InternalRep[internalOpList.size()];
            graph = buildGraph();
            // graphToString(graph);
            ArrayList<Integer> roots = findRoots(graph);
            ArrayList<Integer> leaves = findLeaves(graph);
            priorities = new int[graph.size()];
            for (int x = 0; x < priorities.length; x++) {
                priorities[x] = 1;
            }
            // prioritize(roots);
            drawGraph(graph);
            // System.out.println(java.util.Arrays.toString(priorities));
            schedule(leaves);
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
                        // System.out.println("in def");
                        // set M(VRi) to op
                        // System.out.println("def is r" + Integer.toString(current.getOperand3()[1]) + " and line is " + Integer.toString(current.getLine()));
                        M[current.getOperand3()[1]] = current;
                    }

                }
                // for each VRj used in op
                int useOne = current.getOperand1()[1];
                // System.out.println("in use 1");
                if (useOne != -1) {
                    if (useOne != -1) {
                        // add an edge from o use to the def node in M(VRj)
                        defOp = M[useOne];
                        defLine = defOp.getLine();
                        defNode = new Node(1, latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.get(defLine).put(line, new Node(1, latencies[currOp], -1));
                    }
                }

                int useTwo = current.getOperand2()[1];
                // System.out.println("in use 2");
                if (useTwo != -1) {
                    if (useTwo != -1) {
                        // add an edge from o to the node in M(VRj)
                        defOp = M[useTwo];
                        defLine = defOp.getLine();
                        // System.out.println("defline is " + Integer.toString(defLine));
                        defNode = new Node(1, latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.toString();
                        DG.get(defLine).put(line, new Node(1, latencies[currOp], -1));
                    }
                }

                // store
                if (currOp == 2) {
                    int useThree = current.getOperand3()[1];
                    if (useThree != -1) {
                        if (useThree != -1) {
                            // add an edge from o to the node in M(VRj)
                            defOp = M[useThree];
                            defLine = defOp.getLine();
                            defNode = new Node(1, latencies[defOp.getOperation()], 1);
                            currentEdges.put(defLine, defNode);
                            // add reverse edge from def node to o use
                            DG.get(defLine).put(line, new Node(1, latencies[currOp], -1));
                        }
                    }
                }

                // if o is a load, store, or output
                if (currOp == 0) { // load
                    // add conflict edge from load op to most recent store
                    if (stores.size() > 0 && currentEdges.get(stores.get(stores.size()-1)) == null) {
                        // want line, type(conflict), latency, and direction of most recent store --> conflict
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(2, 
                                        latencies[DGToIR[stores.get(stores.size()-1)].getOperation()], 1));
                        // add reverse conflict edge from most recent store to op
                        DG.get(stores.get(stores.size()-1)).put(line, new Node(2, latencies[currOp], -1));
                    }
                    loads.add(line);
                }
                
                if (currOp == 2) { // store
                    // add serialization edge from store op to most recent store
                    if (stores.size() > 0 && currentEdges.get(stores.get(stores.size()-1)) == null) {
                        // want line, type, latency, and direction of most recent store
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(3, 1, 1));
                        // add reverse edge from most recent store to op
                        DG.get(stores.get(stores.size()-1)).put(line, new Node(3, 1, -1));
                    }
                    // add serialization edges from op to each previous load
                    if (loads.size() > 0) {
                        for (int i = 0; i < loads.size(); i++) {
                            if (currentEdges.get(loads.get(i)) == null) {
                                // want line, type, latency, and direction of load
                                currentEdges.put(loads.get(i), new Node(3,1, 1));
                                // add reverse edge from most recent store to op
                                DG.get(loads.get(i)).put(line, new Node(3, 1, -1));
                            }
                        }
                    }
                    // add serialization edges from op to each previous output
                    if (outputs.size() > 0) {
                        for (int i = 0; i < outputs.size(); i++) {
                            if (currentEdges.get(outputs.get(i)) == null) {
                                // want line, type, latency, and direction of load
                                currentEdges.put(outputs.get(i), new Node(3, 1, 1));
                                // add reverse edge from most recent store to op 
                                DG.get(outputs.get(i)).put(line, new Node(3, 1, -1));
                            }
                        }
                    }
                    stores.add(line);
                }

                if (currOp == 8) { //output
                    // add edge from op to most recent store
                    if (stores.size() > 0 && currentEdges.get(stores.get(stores.size()-1)) == null) {
                        // want line, type, latency, and direction of most recent store
                        currentEdges.put(stores.get(stores.size()-1), 
                                        new Node(2, 
                                        latencies[DGToIR[stores.get(stores.size()-1)].getOperation()], 1));
                        // add reverse edge from most recent store to op
                    DG.get(stores.get(stores.size()-1)).put(line, new Node(2, latencies[currOp], -1));
                    }

                    // add edge from op to most recent output
                    if (outputs.size() > 0 && currentEdges.get(outputs.get(outputs.size()-1)) == null) {
                        // want line, type, latency, and direction of most recent output
                        currentEdges.put(outputs.get(outputs.size()-1), 
                                        new Node(3, 1, 1));
                        // add reverse edge from most recent store to op
                        DG.get(outputs.get(outputs.size()-1)).put(line, new Node(3, 1, -1));
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
            // System.out.println("made it past " + current.printILOCCP1() + "\n");
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
                graphWriter.write(nodeLine.toString() + " [label = \"" + nodeLine.toString() + ": " 
                                + DGToIR[nodeLine].printILOCCP1() + "\nP: " + Integer.toString(priorities[nodeLine]) + "\"];\n");
                // edge work, will put in a string to concatenate at the end
                for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                    Integer otherNodeLine = edgeEntry.getKey();
                    Node otherNode = edgeEntry.getValue();
                    Integer edgeDirection = otherNode.getDependency();
                    if (edgeDirection == -1) {
                        // edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[color=\"firebrick3\"];\n";
                    } else if (otherNode.getType() == 3) { // serialization
                        edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[label=\" Serialization\" color=\"forestgreen\"];\n";
                    } else if (otherNode.getType() == 2) { // conflict
                        edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[label=\" Conflict\" color=\"darkgoldenrod1\"];\n";
                    } else if (otherNode.getType() == 1) { // data
                        edges = edges + nodeLine.toString() + " -> " + otherNodeLine.toString() + "[label=\" Data\" color=\"dodgerblue\"];\n";
                    }
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
            // System.out.println("Node " + nodeEntry.getKey() + "\t" + DGToIR[nodeEntry.getKey()].printILOCCP1());
            for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                Integer otherNodeLine = edgeEntry.getKey();
                Node otherNode = edgeEntry.getValue();
                Integer edgeDirection = otherNode.getDependency();
                if (edgeDirection == 1 && otherNode.getType() == 3) {
                    System.out.println("forward serialization edge to " + otherNodeLine.toString());
                } else if (edgeDirection == 1 && otherNode.getType() == 2) {
                    System.out.println("forward conflict edge to " + otherNodeLine.toString());
                } else if (edgeDirection == 1 && otherNode.getType() == 1) {
                    System.out.println("forward data edge to " + otherNodeLine.toString());
                } else if (edgeDirection == -1 && otherNode.getType() == 3) {
                    System.out.println("reverse serialization edge to " + otherNodeLine.toString());
                } else if (edgeDirection == -1 && otherNode.getType() == 2) {
                    System.out.println("reverse conflict edge to " + otherNodeLine.toString());
                } else if (edgeDirection == -1 && otherNode.getType() == 1) {
                    System.out.println("reverse data edge to " + otherNodeLine.toString());
                }
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
        // System.out.println(Integer.toString(roots.size()));
        // System.out.println(Integer.toString(roots.get(0)));
        return roots;
    }

    /**
     * DFS tree-walk algorithm that computes latency-weighted distance priorities of nodes in graph
     * @param roots ArrayList<Integer> representing the root nodes in the ILOC block dependency graph
     */
    public static void prioritize(ArrayList<Integer> roots) {
        // System.out.println("heheheheheh sorcery! concoction! witch! truman it's a show good afternoon good evening and goodnight heheheheh");
        Stack<int[]> stack = new Stack<int[]>();
        int node = 0;
        int latency = 0;
        for (int r = 0; r < roots.size(); r++) {
            // push root node onto stack
            stack.push(new int[] {roots.get(r), latencies[DGToIR[roots.get(r)].getOperation()]});
            // array representing node # and latency
            int[] nodeInfo;
            // while there's still a graph to explore
            while (!stack.isEmpty()) {
                // visit next node
                nodeInfo = stack.pop();
                node = nodeInfo[0];
                latency = nodeInfo[1];
                // if the current latency weight is less than what we've accumulated so far
                if (priorities[node] <= latency) {
                    // set priority of node to accumulated latency
                    priorities[node] = latency;
                    // System.out.println("priority for node " + Integer.toString(node) + " is " + Integer.toString(latency) + "\n");
                } else { // we've already maximized this part of the graph, onto the next node!
                    continue;
                }
                // go through all nodes that current node has a forward edge to and add them to the stack
                for (Map.Entry<Integer, Node> edgeEntry : graph.get(node).entrySet()) {
                    if (edgeEntry.getValue().getDependency() == 1) {
                        stack.push(new int[] {edgeEntry.getKey(), latency + edgeEntry.getValue().getLatency()});
                    }
                }
            }
        }
    }


    /**
     * Finds any dependent (all nodes depend on them) nodes in graph
     * @param graph Map<Integer, Map<Integer, Node>> that represents dependencies between ILOC ops in a given block
     * @return an ArrayList of integers that represent any dependent nodes in graph
     */
    public static ArrayList<Integer> findLeaves(Map<Integer, Map<Integer, Node>> graph) {
        ArrayList<Integer> leaves = new ArrayList<Integer>();
        int dependent;
        for(Map.Entry<Integer, Map<Integer, Node>> nodeEntry : graph.entrySet()) {
            dependent = 1;
            for (Map.Entry<Integer, Node> edgeEntry : nodeEntry.getValue().entrySet()) {
                // if there is a reverse edge in the current node's POV (a forward edge from another node)
                if (edgeEntry.getValue().getDependency() == 1) {
                    // the node is not dependent
                    dependent = 0;
                    break;
                }
            }
            // check independence for root
            if (dependent == 1) {
                leaves.add(nodeEntry.getKey());
            }
        }
        // System.out.println("leaves");
        // System.out.println(Integer.toString(leaves.size()));
        // System.out.println(Integer.toString(leaves.get(0)));
        // System.out.println(Integer.toString(leaves.get(1)));
        return leaves;
    }

    public static void schedule (ArrayList<Integer> leaves) {
        int cycle = 1;
        ArrayList<int[]> active = new ArrayList<int[]>();
        ArrayList<int[]> retiredActive = new ArrayList<int[]>();
        ArrayList<Integer> readyF0 = new ArrayList<Integer>();
        ArrayList<Integer> readyF1 = new ArrayList<Integer>();
        ArrayList<Integer> readyOutput = new ArrayList<Integer>();
        ArrayList<Integer> readyMisc = new ArrayList<Integer>();
        ArrayList<Integer> retired = new ArrayList<Integer>();
        int[] pickedOps;
        int ready = 1;

        // Sort leaves into appropriate ready sets
        for (int l = 0; l < leaves.size(); l++) {
            sortOp(leaves.get(l), readyF0, readyF1, readyOutput, readyMisc);
        }

        // schedule all ops
        while(readyF0.size() + readyF1.size() + readyOutput.size() + readyMisc.size() + active.size() != 0) {
            // System.out.println("onto the next cycle");
            // System.out.println("readyF0: " + readyF0.toString());
            // System.out.println("readyF1: " + readyF1.toString());
            // System.out.println("readyOutput: " + readyOutput.toString());
            // System.out.println("readyMisc: " + readyMisc.toString());
            // pick ops for each functional unit
            pickedOps = pickOp(readyF0, readyF1, readyOutput, readyMisc);
            // move ops from ready set to active set
            for (int i = 0; i < pickedOps.length; i++) {
                if(pickedOps[i] != -1) {
                    active.add(new int[] {pickedOps[i], cycle + latencies[DGToIR[pickedOps[i]].getOperation()]});
                }
            }
            // move onto next cycle
            cycle++;
            // System.out.println("The size of active is " + Integer.toString(active.size()));
            // System.out.println(java.util.Arrays.toString(active.get(0)));
            retiredActive.clear();
            // find each active operation that retires on this cycle
            for (int a = 0; a < active.size(); a++) {
                if (active.get(a)[1] == cycle) {
                    retiredActive.add(active.get(a));
                }
            }

            // go through each active operation at retirement age
            for (int r = 0; r < retiredActive.size(); r++) {
                int retiredOp = retiredActive.get(r)[0];
                // add to retired set
                retired.add(retiredOp);
                // remove from active set
                // System.out.println("Active:");
                // for(int i = 0; i < active.size(); i++) {
                //     System.out.println(java.util.Arrays.toString(active.get(i)));
                // }
                active.remove(retiredActive.get(r));
                // System.out.println("Removed " + java.util.Arrays.toString(retiredActive.get(r)));
                // System.out.println("Active:");
                // for(int i = 0; i < active.size(); i++) {
                //     System.out.println(java.util.Arrays.toString(active.get(i)));
                // }
                // iterate through all nodes that depend on retiredOp
                for (Map.Entry<Integer, Node> edgeEntry : graph.get(retiredOp).entrySet()) {
                    // if node depends on retiredOp
                    if (edgeEntry.getValue().getDependency() == -1) {
                        // System.out.println("Found a dependent node " + Integer.toString(edgeEntry.getKey()));
                        // determine if node is "ready" (all defs have retired)
                        ready = 1;
                        for (Map.Entry<Integer, Node> otherEdgeEntry : graph.get(edgeEntry.getKey()).entrySet()) {
                            // if the edge is a def
                            if (otherEdgeEntry.getValue().getDependency() == 1) {
                                // if the def is NOT retired
                                // System.out.println("Node " + Integer.toString(otherEdgeEntry.getKey()) + " is a donor");
                                if (!retired.contains(otherEdgeEntry.getKey())) {
                                    // System.out.println("Node " + Integer.toString(otherEdgeEntry.getKey()) + " is retired");
                                    // then the other node is not ready
                                    ready = 0;
                                }
                            }
                        }
                        // if the dependent node is ready, then add it to the appropriate ready set
                        if (ready == 1) {
                            // System.out.println("Dependent node " + Integer.toString(edgeEntry.getKey()) + " is ready.");
                            sortOp(edgeEntry.getKey(), readyF0, readyF1, readyOutput, readyMisc);
                        }
                    } 
                }
            }
        }
    }

    /**
     * Sorts op into appropriate ready set
     * @param op 
     * @param readyF0
     * @param readyF1
     * @param readyOutput
     * @param readyMisc
     */
    public static void sortOp (int op, ArrayList<Integer> readyF0, ArrayList<Integer> readyF1, ArrayList<Integer> readyOutput, ArrayList<Integer> readyMisc) {
        int opCode = DGToIR[op].getOperation();
        if (opCode == 0 || opCode == 2) { // load or store
            readyF0.add(op);
        } else if (opCode == 5) { // mult
            readyF1.add(op);
        } else if (opCode == 8) {
            readyOutput.add(op);
        } else {
            readyMisc.add(op);
        }
    }

    public static int[] pickOp (ArrayList<Integer> readyF0, ArrayList<Integer> readyF1, ArrayList<Integer> readyOutput, ArrayList<Integer> readyMisc) {
        // never looking at the active set
        // FIRST priority is looking to see if there's anything in F0 or F1 bc those take the longest
        int maxP = 0;
        int maxOp = -1;
        int maxIdx = -1;
        int[] pickedOps = new int[] {-1, -1};
        int output0 = 0;
        String f0, f1;
        // grabbing highest priority op for f0
        if (readyF0.size() > 0) { // if there are any high latency operations that can only fill f0
            // find max priority load or store op
            for (int z = 0; z < readyF0.size(); z++) {
                // System.out.println("Node " + Integer.toString(readyF0.get(z)) + " w/ priority: " + priorities[readyF0.get(z)]);
                if (priorities[readyF0.get(z)] > maxP) {
                    maxP = priorities[readyF0.get(z)];
                    maxOp = readyF0.get(z);
                    maxIdx = z;
                }
            }
            // set f0 to highest priority high latency op
            pickedOps[0] = maxOp;
            // remove op from readyF0
            readyF0.remove(maxIdx);
        } else if (readyOutput.size() > 0) {
            // reset maxes
            maxP = 0;
            maxOp = -1;
            maxIdx = -1;
            // find max priority output op
            for (int o = 0; o < readyOutput.size(); o++) {
                // System.out.println("Node " + Integer.toString(readyOutput.get(o)) + " w/ priority: " + priorities[readyOutput.get(o)]);
                if (priorities[readyOutput.get(o)] > maxP) {
                    maxP = priorities[readyOutput.get(o)];
                    maxOp = readyOutput.get(o);
                    maxIdx = o;
                }
            }
            // set f0 to highest priority output op
            pickedOps[0] = maxOp;
            // indicate that the singular output for the cycle has been chosen
            output0 = 1;
            // remove op from readyOutput
            readyOutput.remove(maxIdx);
        } else if (readyMisc.size() > 0) {
            // reset maxes
            maxP = 0;
            maxOp = -1;
            maxIdx = -1;
            // find max priority output op
            for (int m = 0; m < readyMisc.size(); m++) {
                // System.out.println("Node " + Integer.toString(readyMisc.get(m)) + " w/ priority: " + priorities[readyMisc.get(m)]);
                if (priorities[readyMisc.get(m)] > maxP) {
                    maxP = priorities[readyMisc.get(m)];
                    maxOp = readyMisc.get(m);
                    maxIdx = m;
                }
            }
            // set f0 to highest priority misc op
            pickedOps[0] = maxOp;
            // remove op from readyMisc
            readyMisc.remove(maxIdx);
        }

        // grabbing highest priority op for f1
        if (readyF1.size() > 0) { // if there are any high latency operations that can only fill f1
            // reset maxes
            maxP = 0;
            maxOp = -1;
            maxIdx = -1;
            // find max priority op
            for (int z = 0; z < readyF1.size(); z++) {
                // System.out.println("Node " + Integer.toString(readyF1.get(z)) + " w/ priority: " + priorities[readyF1.get(z)]);
                if (priorities[readyF1.get(z)] > maxP) {
                    maxP = priorities[readyF1.get(z)];
                    maxOp = readyF1.get(z);
                    maxIdx = z;
                }
            }
            // set f1 to the highest priority high latency op
            pickedOps[1] = maxOp;
            // remove op from readyF1
            readyF1.remove(maxIdx);
        } else if (output0 == 0 && readyOutput.size() > 0) {
            // reset maxes
            maxP = 0;
            maxOp = -1;
            maxIdx = -1;
            // find max priority output op
            for (int o = 0; o < readyOutput.size(); o++) {
                if (priorities[readyOutput.get(o)] > maxP) {
                    maxP = priorities[readyOutput.get(o)];
                    maxOp = readyOutput.get(o);
                    maxIdx = o;
                }
            }
            // set f1 to highest priority output op
            pickedOps[1] = maxOp;
            // remove op from readyOutput
            readyOutput.remove(maxIdx);
        } else if (readyMisc.size() > 0) {
            // reset maxes
            maxP = 0;
            maxOp = -1;
            maxIdx = -1;
            // find max priority output op
            for (int m = 0; m < readyMisc.size(); m++) {
                if (priorities[readyMisc.get(m)] > maxP) {
                    maxP = priorities[readyMisc.get(m)];
                    maxOp = readyMisc.get(m);
                    maxIdx = m;
                }
            }
            // set f1 to highest priority misc op
            pickedOps[1] = maxOp;
            // remove op from readyMisc
            readyMisc.remove(maxIdx);
        }
        if (pickedOps[0] == -1) {
            f0 = "nop";
        } else {
            f0 = DGToIR[pickedOps[0]].printILOCCP1();
        }
        if (pickedOps[1] == -1) {
            f1 = "nop";
        } else {
            f1 = DGToIR[pickedOps[1]].printILOCCP1();
        }
        System.out.println("[ " + f0 + "\t; " + f1 + " ]");
        return pickedOps;
    }
}
