import java.util.*;

public class ArticulationPoints {
    public Node neighbour = null;
    public double depth = -1;
    public Node root;
    public int numSubTrees = 0;
    public Map<Integer, APObject> allNodes = new HashMap<>();

    public Map<Integer, APObject> setupAllNodesForAP(Graph graph, Node root){
        System.out.println("setup 11 root node " + root.nodeID);
        for(Node node : graph.nodes.values()){
            APObject fringeNode = new APObject(node, -1, root);
            System.out.println("setup 13 " + node.nodeID + " fringeNode " + fringeNode.n.nodeID + fringeNode.depth + fringeNode.root.nodeID);
            allNodes.put(node.nodeID, fringeNode);
        }
    return allNodes;
    }

    public ArrayList<APObject> FindAPs(Graph graph, Node root)
    {
//      Initialise all the depths to -1 somehow
//      An APObject contains the node, depth and parent - useful to have the root for later to compare trees??
//      Initialise the APs = {}
        ArrayList<APObject> APs = null;
        Stack<APObject> stack = new Stack<>();
        //  create the first APObject
        APObject rootNode = new APObject(root, 0, null);
        // push the root Node into the stack as the first element
        stack.push(rootNode);

        //  find all the neighbours of the root node
        List<Integer> neighbours = new ArrayList<>();
        for(Segment s : root.segments) {
            int neighID = s.start.nodeID;
            if (root.nodeID == neighID) {
                neighID = s.end.nodeID;
            }
            neighbours.add(neighID);
        }
        if (neighbours.size() == 0){
//             go to the next item in the stack if there is one
//        TODO if there are no neighbours?? stop
        }
        // iterate through all the neighbours

        for (Integer n : neighbours){
        // if depth = -1 node is unvisited
            if (allNodes.get(n).depth == -1){
                iterAP(neighbour, 1, root);
            }
        }

        int reachBack;
//        for (Node n : graph.nodes.values()){
//        for(APObject a : stack){
//            a.n =
//        }
//
//        }
        return APs;
            // pseudocode
    }

    private void iterAP(Node node, int depth, Node parent) {

    }
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
    //    there are two? 3? cases:
    //    Case 1
    //    if(peekNode != visited) (depth = -1){
    //    peekNode depth and peekNode rB = fringeElem depth (rB = reachBack)
    //    add all peekNode's nieghbours except the parent/root to its children set
    //    }
    //    Case 2?
    //    PeekNode has children{
    //    remove a child from its children set
    //    if child is visited {
    //      peekNode rB = min(child depth, peekNode rB)}
    //      else{
    //          push <child, fringeElem depth + 1, peekNode> } to fringe
    //      }
    //    Case 3
    //     peekNode has no children
    //    else{
        //    if peekNode is not n
    //    {
    //    parent = fringeElem parent
    //    parent rB = min(peekNode rB, parent rB)
//    if (peekNode rB >= parent depth{ parent is an AP, add to AP list})
    //    }
    //    }
//    pop from fringe
//    end of while loop and end of iterstate method.
//    Now increase the number of root node subtrees by 1
//    If root node has more than 1 subtree, its an AP
}
