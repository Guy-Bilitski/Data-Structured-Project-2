import javax.print.attribute.HashPrintJobAttributeSet;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    private int size;
    private HeapNode leftRoot;

    public FibonacciHeap(HeapNode node) {
        this.min = node;
        this.size = node.size;
        this.leftRoot = node;
    }

    public FibonacciHeap() {
        this.min = null;
        this.leftRoot = null;
        this.size = 0;
    }

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return this.min == null;
    }

		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {
        HeapNode newNode = new HeapNode(key);
        if (this.min == null) {
            this.min = newNode;
            this.leftRoot = this.min;
        }

        else {
            newNode.next = this.leftRoot;
            newNode.prev = this.leftRoot.prev;
            this.leftRoot.prev = newNode;
            newNode.prev.next = newNode;
            this.leftRoot = newNode;
            if (this.min.key > newNode.key) { // check if min should change
                this.min = newNode;
            }
        }

        this.size ++;
        return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin() {
        if (this.size == 1) {
            this.min.prev = this.min.next = this.min.child = null;
            this.min = null;
            this.leftRoot = null;
        }

        else {
            if (this.min == this.leftRoot) {
                if (this.min.child != null) {
                    this.leftRoot = this.min.child;
                }
                else {
                    this.leftRoot = this.min.next;
                }
            }

            if (this.min.child != null){  // remove the min node and update the pointers (prev, next, child)
                HeapNode childNode = this.min.child;
                childNode.prev.next = this.min.next; // update the right child node, then update the left child node
                this.min.next.prev = childNode.prev;
                childNode.prev = this.min.prev;
                childNode.parent = null;
                this.min.prev.next = childNode;
            }
            else {
                this.min.prev.next = this.min.next;   // if dont have a child - just remove the min node.
                this.min.next.prev = this.min.prev;
            }
            this.min = findNewMin();
        }
        this.size --;
    }


    private HeapNode findNewMin() {  // find minimum in circular doubly linked list
        HeapNode newMin = this.leftRoot;
        HeapNode curr = this.leftRoot.next;

        while (curr != this.leftRoot) {  // stop at the most right node
            curr = curr.next;
            if (curr.key < newMin.key){
                newMin = curr;    // update the min.
            }
        }
        return newMin;
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
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
        HeapNode leftNodeHeap2 = heap2.leftRoot.prev;

        rightNodeHeap1.next = leftNodeHeap2;
        leftNodeHeap2.prev = rightNodeHeap1;

        leftNodeHeap1.prev = rightNodeHeap2;
        rightNodeHeap2.next = leftNodeHeap1;

        this.size += heap2.size;
    }

    // @pre key of root1 is the smallest
    // @pre rank of both roots is equal
    private void concatenateRoots(HeapNode root1, HeapNode root2) {
        if (root2.child == null) {
            root2.child = root1;
            root1.parent = root2;
            root1.next = root1;
            root1.prev = root1;
        }

        else {
            root1.next = root2.child;
            root1.prev = root2.child.prev;
            root2.child.prev = root1;
            root1.parent = root2;
            root2.child = root1;
        }

        root2.rank ++;
        root2.size += root1.size;
    }
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2){
        concatenateHeaps(heap2);
        HeapNode node = this.leftRoot;
        HeapNode stable = this.leftRoot;

        HeapNode[] basket = new HeapNode[this.size];

        while (node.next != stable) {
            if (basket[node.rank] == null) {
                basket[node.rank] = node;
                node = node.next;
            }
            else {
                int rankToDelete = node.rank;
                if (node.key > basket[node.rank].key) {
                    if (node == this.leftRoot) {
                        this.leftRoot = this.leftRoot.next;
                    }
                    HeapNode changeNode = node;
                    node = node.next;
                    changeNode.prev.next = changeNode.next;
                    changeNode.next.prev = changeNode.prev;
                    concatenateRoots(changeNode, basket[node.rank]);
                }
                else {
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
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return -123; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * 
    */
    public int[] countersRep()
    {
    	int[] arr = new int[100];
        return arr; //	 to be replaced by student code
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {    
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	return; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return -234; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return -345; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return -456; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
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
        public HeapNode getChild() {return this.child;}
       public HeapNode getNext() {return this.next;}

    }

    public static void printNode(HeapNode node) {
        System.out.println("(");
        if (node == null) {
            System.out.println(")");
            return;
        }
        else {
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
        h2.insert(-17);
        h.meld(h2);
        h.deleteMin();
        h.deleteMin();
        System.out.println(h.min.key);
        System.out.println(h.size);
        System.out.println(h.leftRoot.key);
        System.out.println(h.leftRoot.child.key);
    }
}
