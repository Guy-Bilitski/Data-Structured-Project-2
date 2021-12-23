import javax.print.attribute.HashPrintJobAttributeSet;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {

    private HeapNode min;
    private HeapNode leftRoot;
    private int size;
    private int numOfTrees;
    private int numOfMarks;
    private static int numOfLinks = 0;
    private static int numOfCuts = 0;  // Use it by static function.



    public FibonacciHeap(HeapNode node) {
        this.min = node;
        this.size = node.size;
        this.leftRoot = node;
        this.numOfMarks = 0;
        this.numOfTrees = 0;

    }

    public FibonacciHeap() {
        this.min = null;
        this.leftRoot = null;
        this.size = 0;
    }

    /**
     * public boolean isEmpty()
     * <p>
     * Returns true if and only if the heap is empty.
     */
    public boolean isEmpty() {
        return this.min == null;
    }


    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     * <p>
     * Returns the newly created node.
     */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        if (this.min == null) {
            this.min = newNode;
            this.leftRoot = this.min;
        } else {
            newNode.next = this.leftRoot;
            newNode.prev = this.leftRoot.prev;
            this.leftRoot.prev = newNode;
            newNode.prev.next = newNode;
            this.leftRoot = newNode;
            if (this.min.key > newNode.key) { // check if min should change
                this.min = newNode;
            }
        }

        this.size++;
        this.numOfTrees ++;
        return newNode;
    }

    /**
     * public void deleteMin()
     * <p>
     * Deletes the node containing the minimum key.
     */
    public void deleteMin() {
        if (this.size == 1) {
            this.min.prev = this.min.next = this.min.child = null;
            this.min = null;
            this.leftRoot = null;
        } else {
            if (this.min == this.leftRoot) {
                if (this.min.child != null) {
                    this.leftRoot = this.min.child;
                } else {
                    this.leftRoot = this.min.next;
                }
            }

            if (this.min.child != null) {  // remove the min node and update the pointers (prev, next, child)
                HeapNode childNode = this.min.child;
                childNode.prev.next = this.min.next; // update the right child node, then update the left child node
                this.min.next.prev = childNode.prev;
                childNode.prev = this.min.prev;
                childNode.parent = null;
                this.min.prev.next = childNode;
            } else {
                this.numOfTrees += this.min.rank; //The number of trees increases by the number of children of min.
                this.min.prev.next = this.min.next;   // if dont have a child - just remove the min node.
                this.min.next.prev = this.min.prev;
            }
            this.min = findNewMin();
        }
        this.size--;
        this.numOfTrees --;   // The number of trees is reduced by 1 (line 97: increased by the number of children)
    }


    private HeapNode findNewMin() {  // find minimum in circular doubly linked list
        HeapNode newMin = this.leftRoot;
        HeapNode curr = this.leftRoot;

        do {
            if (curr.key < newMin.key) {
                newMin = curr;    // update the min.
            }
            curr = curr.next;
        }
        while (curr != this.leftRoot);


        return newMin;
    }

    /**
     * public HeapNode findMin()
     * <p>
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     */
    public HeapNode findMin() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.min;
        }
    }

    private void concatenateHeaps(FibonacciHeap heap2) {
        if (this.min.key > heap2.min.key) {
            this.min = heap2.min;
        }

        HeapNode rightNodeHeap1 = this.leftRoot.prev;
        HeapNode leftNodeHeap1 = this.leftRoot;
        HeapNode rightNodeHeap2 = heap2.leftRoot.prev;
        HeapNode leftNodeHeap2 = heap2.leftRoot;

        rightNodeHeap1.next = leftNodeHeap2;
        leftNodeHeap2.prev = rightNodeHeap1;

        leftNodeHeap1.prev = rightNodeHeap2;
        rightNodeHeap2.next = leftNodeHeap1;

        this.size += heap2.size;
        this.numOfTrees += heap2.numOfTrees;
    }

    // @pre key of root1 is the smallest
    // @pre rank of both roots is equal
    private void concatenateRoots(HeapNode root1, HeapNode root2) {
        if (root2.child == null) {
            root2.child = root1;
            root1.parent = root2;
            root1.next = root1;
            root1.prev = root1;
        } else {
            root1.next = root2.child;
            root1.prev = root2.child.prev;
            root2.child.prev = root1;
            root1.parent = root2;
            root2.child = root1;
        }

        root2.rank++;
        root2.size += root1.size;
        numOfLinks ++;
        this.numOfTrees --; // Turn 2 trees into 1
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Melds heap2 with the current heap.
     */
    public void meld(FibonacciHeap heap2) {
        concatenateHeaps(heap2);
        HeapNode node = this.leftRoot;
        HeapNode stable = this.leftRoot;

        HeapNode[] basket = new HeapNode[this.numOfTrees];

        do {
            if (basket[node.rank] == null) {
                basket[node.rank] = node;
                node = node.next;
            } else {
                int rankToDelete = node.rank;
                if (node.key > basket[node.rank].key) {
                    if (node == this.leftRoot) {
                        this.leftRoot = this.leftRoot.next;
                    }
                    HeapNode changeNode = node;
                    node = node.next;
                    changeNode.prev.next = changeNode.next;
                    changeNode.next.prev = changeNode.prev;
                    concatenateRoots(changeNode, basket[changeNode.rank]);
                } else {
                    if (basket[node.rank] == this.leftRoot) {
                        this.leftRoot = this.leftRoot.next;
                    }
                    basket[node.rank].prev.next = basket[node.rank].next;
                    basket[node.rank].next.prev = basket[node.rank].prev;
                    concatenateRoots(basket[node.rank], node);
                    node = node.next;
                }
                basket[rankToDelete] = null;
            }
        }
        while (node != this.leftRoot);
    }

    /**
     * public int size()
     * <p>
     * Returns the number of elements in the heap.
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     */
    public int[] countersRep() {
        int reqSize = (int) Math.log(this.size) * 2; // upper bound for biggest tree rank (real one using the golden ratio)
        int[] arr = new int[reqSize];
        HeapNode node = this.leftRoot;
        do {
            arr[node.rank]++;
            node = node.next;
        }
        while (node != this.leftRoot);
        return arr;
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     */
    public void delete(HeapNode x) {
        if (x != this.min) {
            actualDecreaseKey(x, (Math.abs(this.min.key) + Math.abs(x.key) + 1), true);
        }
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        actualDecreaseKey(x, delta, false);  //There is no intention to delete the node at the end of the decrease
    }

    /**
     * used both for Delete(x) and for decreaseKey(x, delta)
     * toBeDeleted represents whether the intention is to delete the node
     * at the end of the process or not.
     */
    public void actualDecreaseKey(HeapNode x, int delta, boolean toBeDeleted){
        x.key -= delta;
        if ((x.key < this.min.key) && !toBeDeleted){
            this.min = x;
        }
        if (x.parent != null){  // if x is not a root:
            if (x.key < x.parent.key){
                cascadingCut(x, x.parent);
            }
        }
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * <p>
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return (this.numOfTrees + 2 * this.numOfMarks);
    }

    /**
     * public static int totalLinks()
     * <p>
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks() {
        return numOfLinks;
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return numOfCuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     * <p>
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }
    /* ___________________________________________________________________________*/
    /* ___________________________________________________________________________*/
    private void cascadingCut(HeapNode x, HeapNode y){
        cut(x, y);                     // cut x from y.
        if (y.parent != null){        // if y is not a root
            if (!y.mark){            // if y is not marked yet:
                y.mark = true;      // then mark y
                this.numOfMarks ++;
            }
            else{                 // else move up the cascadingCut.
                cascadingCut(y, y.parent);
            }
        }
    }

    private void cut(HeapNode x, HeapNode y){   // x becomes a root
        // Cut x:
        x.parent = null;
        x.mark = false;              // root never marked
        this.numOfMarks --;

        // update y's pointers and fields:
        y.rank --;
        y.size -= x.size;
        if (x.next == x){         // if x was the only child of y --> then y has no children right now.
            y.child = null;
        }
        else{                        // updates the connections between y's children
            y.child = x.next;       // the second left node becomes the child.
            x.prev.next = x.next;  // come full circle (after cut off x)
            x.next.prev = x.prev;
        }

        // place x to the left of the heap:
        x.prev = this.leftRoot.prev;
        x.next = this.leftRoot;
        this.leftRoot.prev = x;
        this.leftRoot = x;          // update x to be leftRoot

        numOfCuts ++;
    }



    /* ___________________________________________________________________________*/
    /* ___________________________________________________________________________*/

    /**
     * public class HeapNode
     * <p>
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     */
    public static class HeapNode {
        public int key;
        public int size;
        public int rank;
        public boolean mark;
        public HeapNode child;
        public HeapNode prev;
        public HeapNode next;
        public HeapNode parent;

        public HeapNode(int key) {
            this.key = key;
            this.size = 1;
            this.rank = 0;
            this.mark = false;
            this.child = null;
            this.prev = this;
            this.next = this;
            this.parent = null;
        }

        public int getKey() {
            return this.key;
        }

        public HeapNode getChild() {
            return this.child;
        }

        public HeapNode getNext() {
            return this.next;
        }

    }

    public static void printNode(HeapNode node) {
        System.out.println("(");
        if (node == null) {
            System.out.println(")");
            return;
        } else {
            HeapNode t = node;
            do {
                System.out.println(t.getKey());
                HeapNode child = t.getChild();
                printNode(child);
                System.out.println("->");
                t = t.getNext();
            } while (t.getKey() != node.getKey());
            System.out.println(")");
        }
    }

    public void printHeap() {
        printNode(this.leftRoot);
    }


    public static void main(String[] args) {
        FibonacciHeap h = new FibonacciHeap();
        FibonacciHeap h2 = new FibonacciHeap();
        h.insert(5);
        h.insert(10);
        h.insert(4);
        h.insert(2);
        h2.insert(15);
        h2.insert(-17);
        h.meld(h2);
//        h.deleteMin();
//        h.deleteMin();
//        System.out.println(h.min.key);
//        System.out.println(h.size);
//        System.out.println(h.leftRoot.key);
//        System.out.println(h.leftRoot.child);

        for (int val : h.countersRep()) {
            System.out.println(val);
        }
    }

}