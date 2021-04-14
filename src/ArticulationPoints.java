import java.util.*;

public class ArticulationPoints {
    public HashMap<Integer, APObject> APObjects = new HashMap<>();
    public Stack<APObject> stack = new Stack<>();

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
            if (APObjects.get(n.nodeID).depth <= -1){
                return n;
            }
        }
        return null;
    }

    public HashSet<Node> FindAPs(Node root, HashSet APs) {
        //      Create the root APObject and use it
        CreateRoot(root);
        APObject rootAP = APObjects.get(root.nodeID);
        int numSubTrees = 0;
        //  the first nodes with a parent are these neighbours - its vital at least one exists
        ArrayList<APObject> neighbours = rootAP.children;
        if (neighbours.size() != 0) {
            System.out.println("AP34 root node " + root.nodeID + " has " + neighbours.size() + " neighbours");
            // iterate through all the neighbours via their nodeIDs in neighbours
            for (APObject neighAP : neighbours) {
//                if its depth is -1, it is unvisited; send it to iterAPs
                if (neighAP.depth <= -1) {
                    neighAP.parent = rootAP;
                    stack.clear();
                    iterAPs(neighAP.n, neighAP.depth, root, APs);
                    numSubTrees++;
                    System.out.println("AP 46 subtrees " + numSubTrees);
                }
            }
            if (numSubTrees > 1) {
                APs.add(root);
            }
        }
        else {
//          the root node has no children, randomly chosen an isolated node, it is an error in this graph data
//          go back and loop again ie randomly select another unvisited node from graph.nodes.values, and continue but for now...
            System.err.println("Root Node "  + root.nodeID + " has no neighbouring nodes try another root node") ;
        }
        return APs;
    }

    private void iterAPs(Node firstNode, Integer depth, Node root, HashSet<Node> APs) {
        // Initialise stack with first element (neighbour/ child of the root node)
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        stack.push(firstNodeAP); // push firstNode into the stack as the first element

        while (!stack.empty()) {
            APObject elem = stack.peek();

//        Case 1: the stack element is unvisited - populate the APObject for this node with children
            if (elem.depth <= -1) {
                elem.depth = elem.parent.depth + 1;
                elem.reachBack = elem.parent.depth + 1;
//              Populate children with the nodeIDs of all the child nodes to this one, that are not the parent.
                elem.children = findChildren(elem);
                APObjects.put(elem.n.nodeID, elem);
            }

//          Case 2 the element is visited and has children
            else if (!elem.children.isEmpty()){
                    APObject child = elem.children.remove(0);
//                    if child is visited, if its reachBack is less than the current elem rB, update the elem rB = child depth
                    if (child.depth >= 0 && elem.reachBack >= 0){ // ie both have been visited
                        elem.reachBack = Math.min(child.depth, elem.reachBack);
//                        I so want to be able to write elem.n.nodeP! I should have called the parent nodeP
                    }
                    else if(elem.reachBack <= -1){
                        System.out.println("Whoop whoop pull up node " + elem.n.nodeID + " has -ve rB " + elem.reachBack);
                        elem.reachBack = child.depth;
                    }
                    else{
                        child.parent = elem;
                        stack.push(child); // push the child into the stack
                    }
            }
//            Case 3 the element has no children and its a visited node
            else{
                if (elem.n.nodeID != firstNode.nodeID){
                    APObject parent = APObjects.get(elem.parent.n.nodeID);
//                  before doing the min, check if either rB is -ve - both elem and parent should both be visited
                    if (parent.reachBack >= 0 && elem.reachBack >= 0) {
                        parent.reachBack = Math.min(elem.reachBack, parent.reachBack);
                    }
                    APObjects.put(parent.n.nodeID, parent);
                    if(elem.reachBack >= parent.depth){
                        System.out.println("Adding to APs parent: " + parent.n.nodeID);
                        APs.add(parent.n);
                    }
                    elem.reachBack = depth;
                    APObjects.put(elem.n.nodeID, elem);
                }
                // pop the stack to remove the peeked element
                APObject stackTop = stack.pop();
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
        APObjects.put(root.nodeID, rootAP);
    }

    private ArrayList<APObject> findChildren(APObject elem) {
        ArrayList<APObject> children = new ArrayList<>();
        for (int child : elem.n.nextNodeIDs){
            APObject childAP = APObjects.get(child);
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
