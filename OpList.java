public class OpList {
    private InternalRep head;
    private InternalRep tail;

    public OpList() {
        this.head = null;
        this.tail = null;
    }

    public InternalRep getHead() {
        return head;
    }

    public InternalRep getTail() {
        return tail;
    }

    public void traverseForward(){
        InternalRep current = head;
        while (current != null) {
            System.out.println(current.toString());
            current = current.next;
        }
    }

    public void traverseILOC() {
        InternalRep current = head;
        while (current != null) {
            System.out.println(current.printILOCCP2());
            current = current.next;
        }
    }

    public int size() {
        int size = 0;
        InternalRep current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    public void insertAtEnd(InternalRep newOp) {
        InternalRep temp = newOp;
        if (tail == null) {
            head = temp;
            tail = temp;
        } else {
            tail.next = temp;
            temp.prev = tail;
            tail = temp;
        }
    }

    public void insertDummyHead() {
        InternalRep prevHead = this.head;
        InternalRep dummyHead = new InternalRep();
        dummyHead.setOperation("//dummy head");
        this.head = dummyHead;
        this.head.prev = null;
        this.head.next = prevHead;
        prevHead.prev = this.head;
    }

    public void remove(InternalRep currNode) {
        currNode.next.prev = currNode.prev;
        currNode.prev.next = currNode.next;
        currNode.next = null;
        currNode.prev = null;
    }

    /**
     * Finds maximum source register in internal representation of input ILOC block
     * @return int representing max SR of input ILOC block
     */
    public int findMaxSR() {
        int maxSR = -1; // set max register to -1 (in case of only output or nop blocks)
        InternalRep current = head.getNext();
        while (current != null) {
            String operation = current.getOperation();
            if (operation.equals("output") || operation.equals("nop")) { // skip these
                current = current.next;
                continue;
            } else { // literally anything else
                // get the def register first
                if (current.getOperand3()[0] > maxSR) {
                    maxSR = current.getOperand3()[0];
                }
                
                // get the second use register for eligible operations
                if (operation.equals("add") || operation.equals("sub") || operation.equals("mult")
                    || operation.equals("lshift") || operation.equals("rshift")) { // get second register for eligible operations
                        if (current.getOperand2()[0] > maxSR) {
                            maxSR = current.getOperand2()[0];
                        }
                }
                if (!operation.equals("loadI")) {
                    // get the first use register
                    if (current.getOperand1()[0] > maxSR) {
                        maxSR = current.getOperand1()[0];
                    }
                }
            }
            // continue to next operation
            current = current.next;
        }
        return maxSR;
    }
}
