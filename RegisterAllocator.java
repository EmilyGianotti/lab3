// import statements here

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Stack;

public class RegisterAllocator {
    // global variables here
    public static int prCt = 0;
    public static boolean RENAME = false;
    public static String[] wordList = {"MEMOP", "LOADI", "ARITHOP", "OUTPUT", "NOP", "CONSTANT", "REGISTER", "COMMA", "INTO", "ENDFILE", "NEWLINE"};
    public static int idx = 0;
    public static int lineNum = 1;
    public static int errCt = 0;
    public static int opCt = 0;
    public static int index = 1;
    public static int spillLoc = 32768;
    public static OpList internalOpList;
    public static Stack<Integer> availPRs = new Stack<>();
    public static int PRtoVR[];
    public static int PRNU[];
    public static int VRtoPR[];
    public static int VRtoSpillLoc[];
    public static int maxLive;
    public static InternalRep current;

    public static void main (String[] args) throws Exception {
        int i = 0;
        String filename = "";
        if (args.length == 0) { // no filename provided
            System.err.println("ERROR: No flag or register count + filename provided.");
            System.out.println("COMP 412, Reference Allocator (lab 2)\n" + //
                                "Command Syntax:\n" + //
                                "        412alloc [k] [-x] filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        k        specifies the number of registers available to the allocator\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n" + //
                                "        -x        scans and parses input block, renames code on input block, prints results to stdout");
            return;
        } else {
            if (args[0].charAt(0) != '-') { // getting register count
                try { // checking for valid k
                    prCt = Integer.parseInt(args[0]);
                    if (!(3 <= prCt && prCt <= 64)) {
                        System.err.println("ERROR: Register count is out of bounds. k must be in the range of [3, 64].");
                        return;
                    }
                    if (i == args.length -1) {
                        System.err.println("ERROR: No filename provided.");
                    }
                    i++;
                } catch (Exception e) {
                    System.err.println("ERROR: " + e.getMessage());
                    System.err.println("ERROR: No register count provided.");
                    return;
                }
            }
            while (i < args.length) { // run through args
                if (args[i].equals("-h")) { // help flag
                    System.out.println("COMP 412, Reference Allocator (lab 2)\n" + //
                                "Command Syntax:\n" + //
                                "        412alloc [k] [-x] filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        k        specifies the number of registers available to the allocator\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n" + //
                                "        -x        scans and parses input block, renames code on input block, prints results to stdout");
                    return;
                } else if (args[i].equals("-x")) { // rename flag
                    if (i == args.length -1) {
                        System.err.println("ERROR: No filename provided or incorrect placement of -x.");
                        return;
                    } else {
                        RENAME = true;
                    }
                } else if (args[i].charAt(0) == ('-')) { // invalid flag
                    System.err.println("ERROR: The " + args[i] + " flag is not allowed.");
                    System.out.println("COMP 412, Reference Allocator (lab 2)\n" + //
                                "Command Syntax:\n" + //
                                "        412alloc [k] [-x] filename [-h]\n" + //
                                "\n" + //
                                "Required arguments:\n" + //
                                "        k        specifies the number of registers available to the allocator\n" + //
                                "        filename  is the pathname (absolute or relative) to the input file\n" + //
                                "\n" + //
                                "Optional flags:\n" + //
                                "        -h        prints this message\n" + //
                                "        -x        scans and parses input block, renames code on input block, prints results to stdout\n");
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
            int maxVr = rename(internalOpList.size(), internalOpList.findMaxSR());
            internalOpList.traverseILOC();
        } catch (Exception e) {
            System.err.println("ERROR:");
            e.printStackTrace();
        }
    }

    /**
     * Renames source registers to virtual registers in given ILOC block
     * @param numOps integer representing number of operations in given ILOC block
     * @param srMax largest source register found in ILOC block
     */
    public static int rename(int numOps, int srMax) {
        int SRtoVR[] = new int[srMax+1];
        int LU[] = new int[srMax+1];
        int VRName = 0;
        int index = numOps - 1;
        int currLive;
        current = internalOpList.getTail();
        
        // Initialize last use and SRtoVR mappings
        for (int i = 0; i < srMax+1; i++) {
            SRtoVR[i] = -1;
            LU[i] = -1;
        }

        // going from bottom of ILOC block to top
        while (index > -1) {
            String operation = current.getOperation();

            // Skip output and nop
            if (operation.equals("output") || operation.equals("nop")) {
                index--;
                current = current.prev;
                continue;
            }

            // Look at def for those that are eligible to have it (store is 2 uses)
            if (!operation.equals("store")) {
                // grab OP3 -> def
                Integer[] op3 = current.getOperand3();
                // Unused DEF
                if (SRtoVR[op3[0]] == -1) {
                    SRtoVR[op3[0]] = VRName;
                    VRName++;
                }
                // set VR to SRtoVR mapping of SR
                op3[1] = SRtoVR[op3[0]];
                // set NU to LU mapping of SR
                op3[3] = LU[op3[0]];
                // kill OP3
                if (!operation.equals("store")) {
                    SRtoVR[op3[0]] = -1;
                    LU[op3[0]] = -1;
                }
                // update internal representation of op3
                current.setOperand3(op3);
            }
            
            // Look at uses L->R (OP 1)
            Integer[] op1 = current.getOperand1();
            // Do infinity for loadI
            if (operation.equals("loadI")) {
                op1[1] = -1;
                op1[3] = -1;
                current.setOperand1(op1);
            } else {
                // Last USE
                if (SRtoVR[op1[0]] == -1) {
                    SRtoVR[op1[0]] = VRName;
                    VRName++;
                }
                // set VR to SRtoVR mapping of SR
                op1[1] = SRtoVR[op1[0]];
                // set NU to LU mapping of SR
                op1[3] = LU[op1[0]];
                LU[op1[0]] = index;
                // update internal representation of op1
                current.setOperand1(op1);
            }

            // Look at uses (OP 2)
            if (!(operation.equals("load") || operation.equals("store") 
                || operation.equals("loadI"))) {
                    Integer[] op2 = current.getOperand2();
                    // Last USE
                    if (SRtoVR[op2[0]] == -1) {
                        SRtoVR[op2[0]] = VRName;
                        VRName++;
                    }
                    // set VR to SRtoVR mapping of SR
                    op2[1] = SRtoVR[op2[0]];
                    // set NU to LU mapping of SR
                    op2[3] = LU[op2[0]];
                    LU[op2[0]] = index;
                    // update internal representation of op 2
                    current.setOperand2(op2);
            }

            if (operation.equals("store")) {
                // grab OP3 -> use
                Integer[] op3 = current.getOperand3();
                // Last USE
                if (SRtoVR[op3[0]] == -1) {
                    SRtoVR[op3[0]] = VRName;
                    VRName++;
                }
                // set VR to SRtoVR mapping of SR
                op3[1] = SRtoVR[op3[0]];
                // set NU to LU mapping of SR
                op3[3] = LU[op3[0]];
                LU[op3[0]] = index;
                // update internal representation of op3
                current.setOperand3(op3);
            }
            // Continue up to the next ILOC operation
            index--;
            current = current.prev;
            // calculate # of live registers
            currLive = 0;
            for (int i = 0; i < SRtoVR.length; i++) {
                if (SRtoVR[i] != -1) {
                    currLive++;
                }
            }
            if (currLive > maxLive) {
                maxLive = currLive;
            }
        }
        return VRName;
    }
}