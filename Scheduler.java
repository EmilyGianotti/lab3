// import statements here

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class Scheduler {
    public static OpList internalOpList;
    public static InternalRep DGToIR[];
    public static Integer latencies[] = {6, 1, 6, 1, 1, 3, 1, 1, 1, 1};

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
            System.out.println(internalOpList.size());
            // buildGraph(maxVR);
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
        // walk the block, top to bottom
        InternalRep current = internalOpList.getHead();
        int line = 0;
        int defLine = -1;
        InternalRep defOp = new InternalRep();
        Node defNode;
        // op is operation, o is node
        while (current != null) {
            // at each operation op
            if (current.getOperation() != 9) {
                Map<Integer, Node> currentEdges = new HashMap<>();
                // if op defines VRi:
                if (current.getOperation() != 2) {
                    if (current.getOperand3()[1] != -1) {
                        // set M(VRi) to op
                        M[current.getOperand3()[1]] = current;
                    }

                }
                // for each VRj used in op
                int useOne = current.getOperand1()[1];
                if (useOne != -1) {
                    if (useOne != -1) {
                        // add an edge from o use to the def node in M(VRj)
                        defOp = M[useOne];
                        defLine = defOp.getLine();
                        defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.get(defLine).put(line, new Node(current.getOperation(), latencies[current.getOperation()], -1));
                    }
                }

                int useTwo = current.getOperand2()[1];
                if (useTwo != -1) {
                    if (useTwo != -1) {
                        // add an edge from o to the node in M(VRj)
                        defOp = M[useTwo];
                        defLine = defOp.getLine();
                        defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                        currentEdges.put(defLine, defNode);
                        // add reverse edge from def node to o use
                        DG.get(defLine).put(line, new Node(current.getOperation(), latencies[current.getOperation()], -1));
                    }
                }

                // store
                if (current.getOperation() == 2) {
                    int useThree = current.getOperand2()[1];
                    if (useThree != -1) {
                        if (useThree != -1) {
                            // add an edge from o to the node in M(VRj)
                            defOp = M[useThree];
                            defLine = defOp.getLine();
                            defNode = new Node(defOp.getOperation(), latencies[defOp.getOperation()], 1);
                            currentEdges.put(defLine, defNode);
                            // add reverse edge from def node to o use
                            DG.get(defLine).put(line, new Node(current.getOperation(), latencies[current.getOperation()], -1));
                        }
                    }
                }
                // if o is a load, store, or output
                    // add serial and conflict edges to other memory ops
                // insert current node and it's associated edges into the directed graph
                DG.put(line, currentEdges);
                // add IR to quick reference table to convert from node to IR of op
                DGToIR[line] = current;
            }
            current = current.next;
            line++;
        }
        return DG;
    }
}
