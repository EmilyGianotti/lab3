public class InternalRep {
    private Integer line;
    private Integer operation;
    private Integer[] operand1;
    private Integer[] operand2;
    private Integer[] operand3;
    InternalRep prev;
    InternalRep next;
    public static String[] opList = {"load", "loadI", "store", "add", "sub", "mult", "lshift", "rshift", "output", "nop"};

    public InternalRep() {
        this.line =  null;
        this.operation = -1;
        this.operand1 = new Integer[4];
        this.operand2 = new Integer[4];
        this.operand3 = new Integer[4];
        this.prev = null;
        this.next = null;
    }

    public InternalRep(int line, int operation, 
                        int sr1, int vr1, int pr1, int nu1,
                        int sr2, int vr2, int pr2, int nu2, 
                        int sr3, int vr3, int pr3, int nu3) {
        this.line = line;
        this.operation = operation;
        this.operand1 = new Integer[]{sr1, vr1, pr1, nu1};
        this.operand2 = new Integer[]{sr2, vr2, pr2, nu2};
        this.operand3 = new Integer[]{sr3, vr3, pr3, nu3};
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(int line) { this.line = line; }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public Integer[] getOperand1() {
        return operand1;
    }

    public void setOperand1(Integer[] op1) {
        this.operand1 = op1;
    }

    public Integer[] getOperand2() {
        return operand2;
    }

    public void setOperand2(Integer[] op2) {
        this.operand2 = op2;
    }

    public Integer[] getOperand3() {
        return operand3;
    }

    public void setOperand3(Integer[] op3) {
        this.operand3 = op3;
    }

    public InternalRep getPrev() {
        return prev;
    }

    public InternalRep getNext() {
        return next;
    }

    public String toString() {
        if (this.getLine() != null) {
            return "line: " + Integer.toString(line) + " " + operation + "\n//" +
                " OP 1\t" + "[ vr" + operand1[1].toString() + " ], " + "[ pr" + operand1[2].toString() + " ], " + "[ nu" + operand1[3].toString() + " ]" +
                "\tOP 2\t" + "[ vr" + operand2[1].toString() + " ], " + "[ pr" + operand2[2].toString() + " ], " + "[ nu" + operand2[3].toString() + " ]" +
                "\tOP 3\t" + "[ vr" + operand3[1].toString() + " ], " + "[ pr" + operand3[2].toString() + " ], " + "[ nu" + operand3[3].toString() + " ]";
        } else {
            return "// dummy head";
        }
    }

    public String printILOCCP1() {
        if (operation == 0 || operation == 2) {
            return opList[operation] + "\tr" + operand1[1].toString() + "\t=> r" + operand3[1].toString();
        } else if (operation == 1) {
            return opList[operation] + "\t" + operand1[0].toString() + "\t=> r" + operand3[1].toString();
        } else if (operation == 3 || operation == 4 || operation == 5 || operation == 6 || operation == 7) {
            return opList[operation] + "\tr" + operand1[1].toString() + ",r" + operand2[1].toString() + "\t=> r" + operand3[1].toString();
        } else if (operation == 8) {
            return opList[operation] + "\t" + operand1[0].toString();
        } else { //nop
            return opList[operation];
        }
    }

    public String printILOCCP2() {
        if (operation == 0 || operation == 2) {
            return opList[operation] + "\tr" + operand1[2].toString() + "\t=> r" + operand3[2].toString();
        } else if (operation == 1) {
            return opList[operation] + "\t" + operand1[0].toString() + "\t=> r" + operand3[2].toString();
        } else if (operation == 3 || operation == 4 || operation == 5 || operation == 6 || operation == 7) {
            return opList[operation] + "\tr" + operand1[2].toString() + ",r" + operand2[2].toString() + "\t=> r" + operand3[2].toString();
        } else if (operation == 8) {
            return opList[operation] + "\t" + operand1[0].toString();
        } else { //nop
            return opList[operation];
        }
    }
}
