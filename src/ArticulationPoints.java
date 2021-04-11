import java.util.*;

public class ArticulationPoints {
    public Node root;
    public int numSubTrees = 0;
    public HashMap<Integer, APObject> APObjects = new HashMap();
    public Stack<APObject> stack = new Stack<>();
    public ArrayList<Node> APs = new ArrayList<>();

//      Initialise all the depths and rB to -1 for every node in the graph and the children to empty
    public HashMap<Integer,APObject> SetAllUnvisited(Graph graph) {
        for (Node n : graph.nodes.values()) {
            ArrayList<Integer> children = new ArrayList<>();
            APObject newNode = new APObject(n, -1, -1, null, children);
            APObjects.put(n.nodeID, newNode);
        }
        return APObjects;
    }

    public ArrayList<Node> FindAPs(Node root) {

//      Initialise the set of APs as empty = {}, none yet, this will be the output
;

//      Create an empty fringe and push the root node into it
        APObject rootAP = APObjects.get(root.nodeID);
        rootAP.depth = 0;
        rootAP.reachBack = 0;
        rootAP.parent = null;

        //  find all the neighbours of the root node = its children
        //  using the two way edges provided (Segments).
        ArrayList<Integer> neighbours = findChildren(rootAP);
        rootAP.children = neighbours;
        stack.push(rootAP);
//        the first nodes with a parent are these neighbours so they are vital
        if (neighbours.size() != 0) {
//        or should I use neighbours != null or empty?
            System.out.println("AP39 " + neighbours.size() + " neighbours of the root node " + root.nodeID);
            // iterate through all the neighbours via their nodeIDs in neighbours
            for (int ne : neighbours) {
                APObject neighAP = APObjects.get(ne);
//                if its depth is -1, it is unvisited; send it to iterAPs
//                TODO find out why I am iterating this neighbour when it equals the original root
                if (neighAP.depth == -1) {
                    iterAPs(neighAP.n, 1, root);
                    numSubTrees++;
                    System.out.println("AP 46 subtrees " + numSubTrees);
                }
            }
            if (numSubTrees > 1) {
                System.out.println("AP 51 subtrees " + numSubTrees + root.nodeID);
                APs.add(root);
            }
        }
        else {
          //  the root node has no children, you've randomly chosen an isolated node - there shouldn't be one in this dataset
//        if its an isolated node, is it an AP? I propose not, that it is an error om the graph data
//        Ideally, randomly select another unvisited node from graph.nodes.values, and continue but for now...
            System.err.println("Root Node has no neighbouring nodes try another root node " + root.nodeID);
        }
        return APs;
    }

    private void iterAPs(Node firstNode, Integer depth, Node root) {
        System.out.println("iter66 firstNode ID " + firstNode.nodeID + " depth " + depth + " root ID " + root.nodeID);
        // Initialise stack with first element for the child of the root node
        //  update the firstNode APObject from existing APObject with the new
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        firstNodeAP.depth = depth;
        firstNodeAP.reachBack = depth;
        firstNodeAP.parent = root;
        ArrayList<Integer> fchildren = findChildren(firstNodeAP);
        firstNodeAP.children = fchildren;
        APObjects.put(firstNode.nodeID, firstNodeAP);
        firstNodeAP = APObjects.get(firstNode.nodeID);
        System.out.println("iter76 firstNode push into stack " + firstNodeAP.depth + " rB " + firstNodeAP.reachBack + " num children  " + firstNodeAP.children.size());
        // push firstNode into the stack as the first element
        stack.push(firstNodeAP);

        while (!stack.empty()) {
            APObject elem = stack.peek();
            System.out.println("iterAPs74 peek: depth " + elem.depth + " rB " + elem.reachBack + " children  " + elem.children.size());

//        Case 1 the element is unvisited
            if (elem.depth == -1) {
                elem.depth = depth;
                elem.reachBack = depth;
//              First initialise the children list to empty so we don't add other nodes children
//              now populate children with the nodeIDs of all the child nodes to this one, that are not the parent.
                ArrayList<Integer> echildren = findChildren(elem);
                elem.children = echildren;
                System.out.println("Case 1 iterAP 90 " + elem.depth + elem.reachBack + echildren.size() + elem.children);
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 2 the element has children
            else if (!elem.children.isEmpty()){
                for (int i = 0; i < elem.children.size()-1; i++){
//                  children is a list of child nodeIDs
                    int childID = elem.children.get(i);
                    APObject child = APObjects.get(childID);
//                    if child is visited, check its reachBack isn't less than the current node rB
                    if (child.depth != -1){
                        System.out.println("101 child " + child.n.nodeID + " take min of depth " + child.depth + " and n rB " + elem.reachBack);
                        elem.reachBack = Math.min(child.depth, elem.reachBack);
                        APObjects.put(elem.n.nodeID, elem);
//                        I really want to write elem.n.node.p !should have called the parent nodeP
                    }
                    else{
//                      child is unvisited; set its depth to n+1;
                        child.depth = elem.depth + 1;
                        child.parent = elem.n;
                        APObjects.put(child.n.nodeID, child);
                        child = APObjects.get(childID);
//                      push the child into the stack
                        stack.push(child);
                        System.out.println("116 child " + childID + "added to stack, size " + stack.size());
                    }
                }
//              once through the children, remove all children from n.children
                elem.children.clear();
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 3 the element has no children and its a visited node
            else{
//            even if it is the firstNode it should have a  parent
                if (elem.parent != null && elem.n.nodeID != firstNode.nodeID){
                    APObject parent = APObjects.get(elem.parent.nodeID);
//                  before doing the min, check if either has a negative reachback - they shouldn't as they should both be visited
                    if (parent.reachBack < 0 || elem.reachBack < 0) {
                        System.err.println("119 Negative reachback!  parent RB" + parent.reachBack + " node rB" + elem.reachBack);
                    }
                    else {
                        parent.reachBack = Math.min(elem.reachBack, parent.reachBack);
                        APObjects.put(parent.n.nodeID, parent);
                        System.out.println("97 parent depth and elem rB " + parent.depth + elem.depth);
                        if(elem.reachBack >= parent.depth){
                            APs.add(parent.n);
                        }
                        elem.reachBack = depth;
                        APObjects.put(elem.n.nodeID, elem);
                    }
                }
                else {
                    System.err.println("144 Unvisited node when it shouldn't be! No parent" + elem.n.nodeID);
                }
            }
            // pop the stack to remove the peeked element
            APObject stackTop = stack.pop();
        }
    }

    private ArrayList<Integer> findChildren(APObject elem) {
        System.out.println("Finding children for " + elem.n.nodeID + " of parent" + elem.parent.nodeID);
        ArrayList<Integer> children = new ArrayList<>();
        for (Segment s : elem.n.segments) {
//                  test if this node n is the start or end of the segment and set its neighbour to the other end
//                  if (elem.n.nodeID = s.start.nodeID) then
            int childID = s.end.nodeID;
//                  otherwise it must be the other end
            if (elem.n.nodeID == childID) {
                childID = s.start.nodeID;
            }
            System.out.println(elem.n.nodeID + "'s child = " + childID );
//            don't test for parent if parent is null
//            if(elem.parent == null) {
//                children.add(childID);
//            }
//            else {
                // add all the children except the parent to the child list
                if (childID != elem.parent.nodeID) {
                    System.out.println(elem.n.nodeID + "'s child = " + childID );
                    children.add(childID);
                }
//            }
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
