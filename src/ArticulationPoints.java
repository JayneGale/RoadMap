import java.util.*;

public class ArticulationPoints {
    public HashMap<Integer, APObject> APObjects = new HashMap<>();
    public Stack<APObject> stack = new Stack<>();
//    public HashSet<Node> APs = new HashSet<>();
    public Boolean verbose = false;
//      Initialise all the depths and rB to -1 for every node in the graph and the children to empty

    public HashMap<Integer,APObject> SetAllUnvisited(Graph graph) {
        for (Node n : graph.nodes.values()) {
            n.addAdjacencyLists(n);
            ArrayList<APObject> children = new ArrayList<>();
            APObject newNode = new APObject(n, -1, -1, null, children);
            APObjects.put(n.nodeID, newNode);
        }
        return APObjects;
    }
    public Node checkDisjointSets (Graph graph){
        for (Node n : graph.nodes.values()) {
            if (APObjects.get(n.nodeID).depth == -1){
                return n;
            }
        }
        return null;
    }

    public HashSet<Node> FindAPs(Node root, HashSet APs) {
//        Start with an empty set of AP Nodes
        //      Create the root APObject and use it
        CreateRoot(root);
        APObject rootAP = APObjects.get(root.nodeID);
        int numSubTrees = 0;
        //  the first nodes with a parent are these neighbours - its vital at least one exists
        ArrayList<APObject> neighbours = rootAP.children;
        if (neighbours.size() != 0) {
            System.out.println("AP28 root node has " + neighbours.size() + " neighbours");
            // iterate through all the neighbours via their nodeIDs in neighbours
            for (APObject neighAP : neighbours) {
//                if its depth is -1, it is unvisited; send it to iterAPs
                if (neighAP.depth == -1) {
                    neighAP.parent = rootAP;
                    stack.clear();
                    iterAPs(neighAP.n, neighAP.depth, root, APs);
                    numSubTrees++;
                    System.out.println("AP 46 subtrees " + numSubTrees);
                }
            }
            if (numSubTrees > 1) {
                System.out.println("AP51 subtrees: " + numSubTrees +  " root node: " +root.nodeID);
                APs.add(root);
            }
        }
        else {
          //  the root node has no children, you've randomly chosen an isolated node - there shouldn't be one in this dataset
//        if its an isolated node, is it an AP? I propose not, that it is an error om the graph data
//        Ideally, randomly select another unvisited node from graph.nodes.values, and continue but for now...
            System.err.println("Root Node "  + root.nodeID + " has no neighbouring nodes try another root node") ;
        }
        return APs;
    }


    private void iterAPs(Node firstNode, Integer depth, Node root, HashSet<Node> APs) {
        // Initialise stack with first element (neighbour/ child of the root node)
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        if(verbose) System.out.println("iter56 push firstNode into stack " + firstNodeAP.depth + " rB " + firstNodeAP.reachBack + " num children  " + firstNodeAP.children.size());
        // push firstNode into the stack as the first element
        stack.push(firstNodeAP);

        while (!stack.empty()) {
            APObject elem = stack.peek();
            if(verbose) System.out.println("iterAPs62 peek:" + elem.n.nodeID +" depth:" + elem.depth + " rB:" + elem.reachBack + " num children:" + elem.children.size());
//        Case 1 the stack element is unvisited - populate the APObject for this node with children
            if (elem.depth <= -1) {
                if(verbose)System.out.println("Case 1 elem is unvisited  " + elem.n.nodeID );
                elem.depth = elem.parent.depth + 1;
                elem.reachBack = elem.parent.depth + 1;
                if(verbose) System.out.println("Case 1 and parent is null");
//              Populate children with the nodeIDs of all the child nodes to this one, that are not the parent.
                ArrayList<APObject> children = findChildren(elem, root);
                elem.children = children;
                if(verbose) System.out.println("Case 1 iterAP 90 depth rb childsize " + elem.depth + elem.reachBack + children.size());
                APObjects.put(elem.n.nodeID, elem);
            }
//          Case 2 the element is visited and has children
            else if (!elem.children.isEmpty()){
                ListIterator<APObject> iter = elem.children.listIterator();
                if(verbose) System.out.println("Case 2 elem " + elem.n.nodeID + " visited and has "  + elem.children.size() + " children rB:" + elem.reachBack);
                while(iter.hasNext()){
                    APObject child = iter.next();
                    iter.remove();
//                    if child is visited, check its reachBack isn't less than the current node rB
                    if (child.depth != -1){
                        if(verbose)System.out.println("101 child has been visited " + child.n.nodeID + " take min of depth " + child.depth + " and n rB " + elem.reachBack);
                        elem.reachBack = Math.min(child.depth, elem.reachBack);
//                        I so want to be able to write elem.n.nodeP! I should have called the parent nodeP
                    }
                    else{
                        child.parent = elem;
//                      push the child into the stack
                        stack.push(child);
                        if(verbose)System.out.println("104 add child " + child.n.nodeID + " to stack" + " it's num children:" + child.children.size() + " stack size now " + stack.size());
                    }
                }
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 3 the element has no children and its a visited node
            else{
                if(verbose)System.out.println("Case 3 elem has no children  " + elem.n.nodeID + " and its visited RB " + elem.reachBack);
                if (elem.n.nodeID != firstNode.nodeID){
                    APObject parent = APObjects.get(elem.parent.n.nodeID);
//                  before doing the min, check if either has a negative reachback - they shouldn't as they should both be visited
                    if (parent.reachBack < 0){
                        if (elem.reachBack < 0) { // unvisited node, should not be happening
                            System.out.println("Case 3 reached unvisited " + elem.n.nodeID + " rB: " + elem.reachBack + " parent " + parent.n.nodeID + " rB: " + parent.reachBack );
                        }
                        else parent.reachBack = elem.reachBack;
                    }
                    else if (elem.reachBack <= 0) parent.reachBack = parent.reachBack; // unvisited node
                    else {
                        parent.reachBack = Math.min(elem.reachBack, parent.reachBack);
                    }
                    APObjects.put(parent.n.nodeID, parent);
                    if(verbose)System.out.println(" Case 3 97 parent depth and elem rB " + parent.depth + elem.depth);
                    if(elem.reachBack >= parent.depth){
                        System.out.println("Adding to APs parent: " + parent.n.nodeID);
                        APs.add(parent.n);
                    }
                    elem.reachBack = depth;
                    APObjects.put(elem.n.nodeID, elem);
                }
                else {
                    System.out.println("136 Case 3 Unvisited node has null parent" + elem.n.nodeID);
                }
                // pop the stack to remove the peeked element
                APObject stackTop = stack.pop();
                if(verbose)System.out.println("140 elem pop from stack " + stackTop.n.nodeID + " stack size now " + stack.size());
            }
        }
    }

    private void CreateRoot(Node root) {
        APObject rootAP = APObjects.get(root.nodeID);
        rootAP.depth = 0;
        rootAP.reachBack = 0;
        rootAP.parent = null;
        ArrayList<APObject> children = new ArrayList<>();
        for (int child : root.nextNodeIDs){
            APObject childAP = APObjects.get(child);
            children.add(childAP);
            }
        rootAP.children = children;
        System.out.println("Neighbours = " + root.nextNodeIDs );
        APObjects.put(root.nodeID, rootAP);
    }

    private ArrayList<APObject> findChildren(APObject elem, Node root) {
        ArrayList<APObject> children = new ArrayList<>();
        for (int child : elem.n.nextNodeIDs){
            APObject childAP = APObjects.get(child);
            if(verbose)System.out.println("177 adding " + elem.n.nodeID + "'s child = " + child + " unless root:" + root.nodeID + " or parent:" + elem.parent.n.nodeID);
            if (child != elem.parent.n.nodeID){
                    children.add(childAP);
            }
        }
        return children;
    }
    // pseudocode
    // initialise all the nodes in the graph to unvisited ie set depth = -1
    // pick a random node in the graph, if it is unvisited set its depth to 0 (because there is more than one tree, if it is visited it is in another tree)
    // set the fringe to empty for this new sub-tree
    // create an APObject Class to hold <Node neighbour, int depth, Node root> and constructor

    // do a depth first search from that first node
    // iterate (n, depth, root) where n is the neighbour, depth is -1, and root is the root of that sub-tree
    // push <n, depth, root> to the fringe

//    while (fringe !=empty)
    //    fringeElem = fringe.peek()
    //    peekNode = fringeElem.node n, the neighbour
    //    there are 3 cases:
    //    Case 1 Peek node is unvisited
    //    if(peekNode != visited) (depth = -1){
    //    peekNode depth and peekNode rB = fringeElem depth (rB = reachBack)
    //    add all peekNode's nieghbours except the parent/root to its children set
    //    }
    //    Case 2 peekNode has children
    //    PeekNode has children{
    //    remove a child from its children set
    //    if child is visited {
    //      peekNode rB = min(child depth, peekNode rB)}
    //      else{
    //          push <child, fringeElem depth + 1, peekNode> } to fringe
    //      }
    //    Case 3 PeekNode has no children

    //    else{
    //    if peekNode is not n
    //    {
    //    parent = fringeElem parent
    //    parent rB = min(peekNode rB, parent rB)
//    if (peekNode rB >= parent depth{ parent is an AP, add to AP list})
    //    }
    //    }
//    pop from fringe
//    end of while loop and end of iterate method.

//    Now increase the number of root node subtrees by 1
//    If root node has more than 1 subtree, its an AP

}
