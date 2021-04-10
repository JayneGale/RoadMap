import java.util.*;

public class ArticulationPoints {
    public Node root;
    public int numSubTrees = 0;
    public Map<Integer, Integer> nodeDepth = new HashMap<>();

    public ArrayList<Node> FindAPs(Graph graph, Node root) {
//      Initialise all the depths to -1 for every node in the graph
//        various ways but lets try a hashmap of nodeID : depth
        for (Node node : graph.nodes.values()) {
            nodeDepth.put(node.nodeID, -1);
        }
        if (nodeDepth.get(root.nodeID) != -1) {
            System.err.println("Root node has been visited, choose another");
        }

//      Initialise the set of APs as empty = {}, none yet
        ArrayList<Node> APs = new ArrayList<>();

//      Create an empty fringe and push the root node into it

        //  find all the neighbours of the root node
        List<Integer> neighbours = new ArrayList<>();
//        create a list of neighbours to the root Node using the two way edges provided (Segments).
        for (Segment s : root.segments) {
            int neighID = s.start.nodeID;
            if (root.nodeID == neighID) {
                neighID = s.end.nodeID;
            }
            neighbours.add(neighID);
        }
//        or should I use neighbours != null or empty?

        if (neighbours.size() != 0) {
            System.out.println("AP 35 " + neighbours.size() + " root node " + root.nodeID);
// iterate through all the neighbours via their nodeIDs in neighbours
            for (int n : neighbours) {
                int depth = nodeDepth.get(n);
//                if its depth is -1, it is unvisited; send it to IterAPs
                if (depth == -1) {
                    iterAPs(graph, graph.nodes.get(n), 1, root);
                    numSubTrees++;
                    System.out.println("AP 43 subtrees " + numSubTrees);
                }
            }
            if (numSubTrees > 1) {
                System.out.println("AP 43 subtrees " + numSubTrees + root.nodeID);
                APs.add(root);
            }
        }
        else {
          //  the root node has no children, you've randomly chosen an isolated node - there shouldn't be one in this dataset
//        if its an isolated node, is it an AP? I propose not, I propose it is an error
//        if it is an AP then I would randomly select another unvisited node from graph.nodes.values, and continue but for now...
            System.err.println("Root Node %i has no neighbouring nodes try another root node " + root.nodeID);
        }
        return APs;
    }

    private void iterAPs(Graph graph, Node firstNode, Integer depth, Node root) {
        System.out.println("iterAP 60 firstNode.nodeID" + firstNode.nodeID + " depth " + depth + " root.nodeID " + root.nodeID);
        int reachBack = -1;
// Initialise stack with first element for the root node
        Stack<APObject> stack = new Stack<>();
        //  create the first APObject from the root node
//        single element node, depth, parent
        ArrayList<Integer> children = new ArrayList<>();
        APObject stackElem = new APObject(firstNode, depth, reachBack, root, children);
//        TODO why is children zero??

        System.out.println("iterAP 68  " + " depth " + depth + " rB " + reachBack + " num children  " + children.size());

        // push the root Node into the stack as the first element
        stack.push(stackElem);
        while (!stack.empty()) {
            APObject elem = stack.peek();
//            case 1 the element is unvisited
            if (elem.depth == -1) {
                elem.depth = depth;
                nodeDepth.put(elem.n.nodeID, depth);
                elem.reachBack = depth;
//              now find the nodeIDs of all the child nodes to this one, that are not the parent.
//              First initialise the children list to empty so we don't add other nodes children
                children = findChildren(elem);
                System.out.println("iterAP 82  " + depth + reachBack + children.size());
                elem.children = children;
            }
//            Case 2 the element has children
            else if (!elem.children.isEmpty()){
                for (int i = 0; i < elem.children.size()-1; i++){
                    int childID = elem.children.get(i);
                    int childDepth = nodeDepth.get(childID);
//                    if child is visited
                    if (childDepth != -1){
                        elem.reachBack = Math.min(childDepth, elem.reachBack);
                    }
                    else{
//                      child depth is n+1;
                        childDepth = nodeDepth.get(elem) + 1;
                        nodeDepth.put(childID, childDepth);

//                      push the child into the stack
                        Node child = graph.nodes.get(childID);
                        APObject childElem = new APObject(child, childDepth, reachBack, elem.n, null);
                        stack.push(childElem);
                    }
                }
//              once  through the children, remove all children from n.children
                elem.children.clear();
            }
//            Case 3 the element has no children and its a visited node
            else{
//            if it is the firstNode it has no parent
                if (elem.n.nodeID != firstNode.nodeID){
                    Node parent = elem.parent;
                    int parRB = nodeDepth.get(parent.nodeID);
//                    TODO How do find the parent APObject?? Its not a Map its an object
//                    How do I get its reachback? Do I have to create a whole Map of reachbacls to NodeIDs?
//                    int parRB = reachback of APIObject that has parent as its node;
                    if (parRB < 0 || elem.reachBack < 0){
                        System.err.println("Unvisited node in final case parRB" + parRB + " node rB" + elem.reachBack );
                    }
                    else{
                        parRB = Math.min(elem.reachBack, parRB);
                        elem.reachBack = depth;
                    }

                }
                stack.pop();
            }
        }
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

    private ArrayList<Integer> findChildren(APObject elem) {
        ArrayList<Integer> children = new ArrayList<>();
        for (Segment s : elem.n.segments) {
//                  test if this node n is the start or end of the segment and set its neighbour to the other end
//                  if (elem.n.nodeID = s.start.nodeID) then
            int childID = s.end.nodeID;
//                  otherwise it must be the other end
            if (elem.n.nodeID == childID) {
                childID = s.start.nodeID;
            }
            // do NOT add the parent to the child list
            if (childID != elem.parent.nodeID) {
                children.add(childID);
            }
        }
        return children;
    }
}
