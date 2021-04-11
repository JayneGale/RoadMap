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
            n.addAdjacencyLists(n);
            ArrayList<Integer> children = n.nextNodeIDs;
            APObject newNode = new APObject(n, -1, -1, null, children);
            APObjects.put(n.nodeID, newNode);
        }
        return APObjects;
    }

    public ArrayList<Node> FindAPs(Node root) {
        //      Create the root APObject and use it
        CreateRoot(root);
        APObject rootAP = APObjects.get(root.nodeID);
        //        the first nodes with a parent are these neighbours - its vital at least one exists
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
            System.err.println("Root Node "  + root.nodeID + " has no neighbouring nodes try another root node") ;
        }
        return APs;
    }


    private void iterAPs(Node firstNode, Integer depth, Node root) {

        System.out.println("iter56 firstNode ID " + firstNode.nodeID + " depth " + depth + " root ID " + root.nodeID);
        // Initialise stack with first element for the child of the root node

        //  update the firstNode APObject from existing APObject with the new information
        CreateFirstNodeAP(firstNode, root);
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        System.out.println("iter62 push firstNode into stack " + firstNodeAP.depth + " rB " + firstNodeAP.reachBack + " num children  " + firstNodeAP.children.size());
        // push firstNode into the stack as the first element
        stack.push(firstNodeAP);

        while (!stack.empty()) {
            APObject elem = stack.peek();
            System.out.println("iterAPs67 peek:" + elem.n.nodeID +" depth:" + elem.depth + " rB:" + elem.reachBack + " num children:" + elem.children.size());

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
//            Case 2 the element is visited and has children
            else if (!elem.children.isEmpty()){
                ListIterator<Integer> iter = elem.children.listIterator();
//                ArrayList<Integer> childCopy = elem.children;
                System.out.println("Case 2 elem " + elem.n.nodeID + " visited and has "  + elem.children.size() + " children rB:" + elem.reachBack);
//                  children is an int list of child nodeIDs
//                for (int i = 0; i < childCopy.size()-1; i++){
//                    int childID = elem.children.get(i);
                while(iter.hasNext()){
                    int childID = iter.next();
                    iter.remove();
                    APObject child = APObjects.get(childID);
//                    if child is visited, check its reachBack isn't less than the current node rB
                    if (child.depth != -1){
                        System.out.println("101 child has been visited " + child.n.nodeID + " take min of depth " + child.depth + " and n rB " + elem.reachBack);
                        elem.reachBack = Math.min(child.depth, elem.reachBack);
                        APObjects.put(elem.n.nodeID, elem);
//                        I really want to write elem.n.node.p !should have called the parent nodeP
                    }
                    else{
//                      child is unvisited; set its depth to n+1;
                        child.depth = elem.depth + 1;
                        child.reachBack = child.depth;
                        child.parent = elem.n;
//                        System.out.println("101 child has not been visited " + child.n.nodeID + " take min of depth " + child.depth + " and n rB " + elem.reachBack);
//                        APObjects.put(child.n.nodeID, child);
//                        ArrayList<Integer> children = findChildren(child, root);
                        child.children = findChildren(child, root);;
                        APObjects.put(childID, child);
                        child = APObjects.get(childID);
//                      push the child into the stack
                        stack.push(child);
                        System.out.println("111 add child " + childID + " to stack" + " it's num children:" + child.children.size() + " stack size now " + stack.size());
                    }
                }
//              once through the children, remove all children from n.children
//                elem.children.clear();
//                TODO check if this is the place it is going wrong and maybe copy the children and remove them one by one
//                or put them in a queue or a stack
                APObjects.put(elem.n.nodeID, elem);
            }
//            Case 3 the element has no children and its a visited node
            else{
                System.out.println("Case 3 elem has no children  " + elem.n.nodeID + " and its visited RB " + elem.reachBack);
//            even if it is the firstNode it should have a parent
//                if (elem.parent != null && elem.n.nodeID != firstNode.nodeID){
                if (elem.parent != null){
                    APObject parent = APObjects.get(elem.parent.nodeID);
//                  before doing the min, check if either has a negative reachback - they shouldn't as they should both be visited
                    if (parent.reachBack < 0 || elem.reachBack < 0) {
                        System.err.println("119 -ve reachback ie unvisited: parent RB " + parent.reachBack + " node rB " + elem.reachBack);
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
            System.out.println("151 elem pop from stack " + stackTop.n.nodeID + " stack size now " + stack.size());
        }
    }

    private void CreateRoot(Node root) {
        APObject rootAP = APObjects.get(root.nodeID);
        rootAP.depth = 0;
        rootAP.reachBack = 0;
        rootAP.parent = null;
        rootAP.children = root.nextNodeIDs;
        System.out.println("Neighbours = " + root.nextNodeIDs );
        APObjects.put(root.nodeID, rootAP);
    }

    private void CreateFirstNodeAP(Node firstNode, Node root) {
        APObject firstNodeAP = APObjects.get(firstNode.nodeID);
        firstNodeAP.depth = 1;
        firstNodeAP.reachBack = 1;
        firstNodeAP.parent = root;
        ArrayList<Integer> children = new ArrayList<>();
        for (int child : firstNode.nextNodeIDs){
            if (child != root.nodeID){
                children.add(child);
            }
        }
        firstNodeAP.children = children;
        APObjects.put(firstNode.nodeID, firstNodeAP);
    }

    private ArrayList<Integer> findChildren(APObject elem, Node root) {
        ArrayList<Integer> children = new ArrayList<>();
        if(elem.parent == null) {
            System.err.println(elem.n.nodeID + "'s parent is null");
            return children;
        }
        for (int child : elem.n.nextNodeIDs){
//            adding in the root node creates a child with a null parent
            System.out.println("186 adding " + elem.n.nodeID + "'s child = " + child + " unless root:" + root.nodeID + " or parent:" + elem.parent.nodeID);
            if (child != root.nodeID && child != elem.parent.nodeID){
                children.add(child);
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
