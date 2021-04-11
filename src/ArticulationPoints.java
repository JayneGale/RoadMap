import java.util.*;

public class ArticulationPoints {
    public int numSubTrees = 0;
    public HashMap<Integer, APObject> APObjects = new HashMap();
    public Stack<APObject> stack = new Stack<>();
    public ArrayList<Node> APs = new ArrayList<>();
//      Initialise the set of APs as empty = {}, none yet, this will be the output

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
        //      Create the root APObject
        CreateRoot(root);
        APObject rootAP = APObjects.get(root.nodeID);

        //        the first nodes with a parent are these neighbours so they are vital
        ArrayList<Integer> neighbours = rootAP.children;
        if (neighbours.size() != 0) {
            System.out.println("AP28 root node has " + neighbours.size() + " neighbours");
            // iterate through all the neighbours via their nodeIDs in neighbours
            for (int ne : neighbours) {
                APObject neighAP = APObjects.get(ne);
//                if its depth is -1, it is unvisited; send it to iterAPs
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

        //  update the firstNode APObject from existing APObject with the new information
        CreateFirstNodeAP(firstNode, root);
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        System.out.println("iter76 firstNode push into stack " + firstNodeAP.depth + " rB " + firstNodeAP.reachBack + " num children  " + firstNodeAP.children.size());
        // push firstNode into the stack as the first element
        stack.push(firstNodeAP);

        while (!stack.empty()) {
            APObject elem = stack.peek();
            System.out.println("iterAPs67 peek: depth " + elem.depth + " rB " + elem.reachBack + " children  " + elem.children.size());

//        Case 1 the stack element is unvisited - populate the APObject for this node
            if (elem.depth == -1) {
                System.out.println("Case 1 elem is unvisited  " + elem.n.nodeID );
                elem.depth = depth;
                elem.reachBack = depth;
                if (elem.parent!= null){
                    System.out.println("Case 1 and parent exists" + elem.parent);
                }
                else{
                    System.out.println("Case 1 and parent is null");
                }
//              Populate children with the nodeIDs of all the child nodes to this one, that are not the parent.
                ArrayList<Integer> echildren = findChildren(elem, root);
                elem.children = echildren;
                System.out.println("Case 1 iterAP 90 " + elem.depth + elem.reachBack + echildren.size() + elem.children);
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 2 the element has children
            else if (!elem.children.isEmpty()){
                System.out.println("Case 2 elem has children  " + elem.n.nodeID + " size " + elem.children.size() + " rB " + elem.reachBack);
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
                        child.reachBack = child.depth;
                        child.parent = elem.n;
                        APObjects.put(child.n.nodeID, child);
                        ArrayList<Integer> cchildren = findChildren(child, root);
                        child.children = cchildren;
                        APObjects.put(childID, child);
                        child = APObjects.get(childID);
//                      push the child into the stack
                        stack.push(child);
                        System.out.println("111 child " + childID + "added to stack, size " + stack.size());
                    }
                }
//              once through the children, remove all children from n.children
                elem.children.clear();
//                TODO check if this is the place it is going wrong and maybe copy the children and remove them one by one
//                or put them in a queue or a stack
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 3 the element has no children and its a visited node
            else{
                System.out.println("Case 3 elem has no children  " + elem.n.nodeID + " and its visited " + elem.reachBack);
//            even if it is the firstNode it should have a parent
//                if (elem.parent != null && elem.n.nodeID != firstNode.nodeID){
                if (elem.parent != null){
                    APObject parent = APObjects.get(elem.parent.nodeID);
//                  before doing the min, check if either has a negative reachback - they shouldn't as they should both be visited
                    if (parent.reachBack < 0 || elem.reachBack < 0) {
                        System.out.println("119 -ve reachback ie unvisited: parent RB " + parent.reachBack + " node rB " + elem.reachBack);
                    }
                    else {
                        parent.reachBack = Math.min(elem.reachBack, parent.reachBack);
                        APObjects.put(parent.n.nodeID, parent);
                        System.out.println(" Case 3 97 parent depth and elem rB " + parent.depth + elem.depth);
                        if(elem.reachBack >= parent.depth){
                            System.out.println("Adding parent to APs " + parent.n.nodeID);
                            APs.add(parent.n);
                        }
                        elem.reachBack = depth;
                        APObjects.put(elem.n.nodeID, elem);
                    }
                }
                else {
                    System.err.println("144 Unvisited node when it shouldn't be. No parent" + elem.n.nodeID);
                }
            }
            // pop the stack to remove the peeked element
            APObject stackTop = stack.pop();
        }
    }

    private void CreateFirstNodeAP(Node firstNode, Node root) {
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        firstNodeAP.depth = 1;
        firstNodeAP.reachBack = 1;
        firstNodeAP.parent = root;
        ArrayList<Integer> fchildren = findChildren(firstNodeAP, root);
        firstNodeAP.children = fchildren;
        APObjects.put(firstNode.nodeID, firstNodeAP);
    }

    private void CreateRoot(Node root) {
        APObject rootAP = APObjects.get(root.nodeID);
        rootAP.depth = 0;
        rootAP.reachBack = 0;
        rootAP.parent = null;
        //  find all the neighbours of the root node = its children
        //  using the two way edges provided (Segments).
        ArrayList<Integer> neighbours = new ArrayList<>();
        System.out.println("Create root: Finding root's neighbours " + root.nodeID);
        for (Segment s : root.segments) {
//                  test if thr root is the start or end of the segment and set its neighbour to the other end
            int neighID = s.end.nodeID;
//                  otherwise it must be the other end
            if (root.nodeID == neighID) {
                neighID = s.start.nodeID;
            }
            System.out.println("Neighbour = " + neighID );
//                 add all the neighbours to the child list
            neighbours.add(neighID);
        }
        rootAP.children = neighbours;
        APObjects.put(root.nodeID, rootAP);
    }

    private ArrayList<Integer> findChildren(APObject elem, Node root) {
        System.out.println("168 Finding children for " + elem.n.nodeID);
        ArrayList<Integer> children = new ArrayList<>();
        for (Segment s : elem.n.segments) {
//                  test if this node n is the start or end of the segment and set its neighbour to the other end
//                  if (elem.n.nodeID = s.start.nodeID) then
            int childID = s.end.nodeID;
//                  otherwise it must be the other end
            if (elem.n.nodeID == childID) {
                childID = s.start.nodeID;
            }
            System.out.println("178 finding " + elem.n.nodeID + "'s child = " + childID );
//            don't test for parent if parent is null
            if(elem.parent == null) {
                System.err.println(elem.n.nodeID + "'s parent is null");
                continue;
                }
            else {
//              add all the children except the parent to the child list
                System.out.println("198 test nodeID " + elem.n.nodeID + " parentID " + elem.parent.nodeID +  " rootID " + root.nodeID);
                if (childID != elem.parent.nodeID && childID != root.nodeID) {
                    System.out.println("200 adding " + elem.n.nodeID + "'s child = " + childID );
                    children.add(childID);
                }
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
