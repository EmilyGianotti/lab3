// import statements here

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Stack;

public class Renamer {
    // global variables here
    public static int index = 1;
    public static int maxLive;
    public static InternalRep current;

    /**
     * Renames source registers to virtual registers in given ILOC block
     * @param numOps integer representing number of operations in given ILOC block
     * @param srMax largest source register found in ILOC block
     */
    public static OpList rename(int numOps, int srMax, OpList internalOpList) {
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
            int operation = current.getOperation();

            // Skip output and nop
            if (operation == 8 || operation == 9) {
                index--;
                current = current.prev;
                continue;
            }

            // Look at def for those that are eligible to have it (store is 2 uses)
            if (!(operation == 2)) {
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
                if (!(operation == 2)) {
                    SRtoVR[op3[0]] = -1;
                    LU[op3[0]] = -1;
                }
                // update internal representation of op3
                current.setOperand3(op3);
            }
            
            // Look at uses L->R (OP 1)
            Integer[] op1 = current.getOperand1();
            // Do infinity for loadI
            if (operation == 1) {
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
            if (!(operation == 0 || operation == 2 
                || operation == 1)) {
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

            if (operation == 2) {
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
        internalOpList.setMaxVR(VRName);
        return internalOpList;
    }
}