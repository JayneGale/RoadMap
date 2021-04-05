import java.util.*;
//import java.util.PriorityQueue;

public class AStarPath {
    public double finalWeight = 0;
    protected double g;
    protected double f;

    public ArrayList<AStar> FindPath(Node startNode, Node targetNode, boolean isTime){
        ArrayList<AStar> path = new ArrayList<>();
//        ArrayList<Node> visited = new ArrayList<>();
        List visited = new ArrayList();
//        ArrayList<Segment> fringed =  new ArrayList<>(); // segment so I can Map it easily
        // define an AStar object containing Node, g weight to this node, H estimate to goalNode
        PriorityQueue<AStar> fringe = new PriorityQueue<>(30, new SegmentComparator());
//      create an AStar element for startNode with no prev and no g value
        AStar start = new AStar(startNode, null, 0, h_function(startNode, targetNode, isTime));
//      add it to the fringe so the fringe has it in
        fringe.add(start);
        while(fringe.peek() != null) {
            // remove the top element from the priority queue - first one will be the startNode element
            AStar current = fringe.poll();
//            System.out.println("21 Poll priority queue node " + current.node.nodeID + " number of segments: " +current.node.segments.size());

//            If a node has already been visited, this second path to it is longer, so skip any visited nodes
            if(!visited.contains(current.node.nodeID)){
//              Start the visit by adding this node to visited and to the path
                visited.add(current.node.nodeID);
                path.add(current);
//              Check if the target Node is now visited, we have reached the target // we don't stop the first time we reach the target as the endNode, only when we visit the target for the first time
                if (current.node.nodeID == targetNode.nodeID) {
                    finalWeight = current.g_Value;
                    System.out.println("36 Reached the target! " + current.node.nodeID + " g_value" + current.g_Value);
                    break;
                }
                //Populate the fringe with the current node's unvisited neighbours
                for (Segment s : current.node.segments) {
//                    one  end of the segment should be the current node - find out which is the node and which is its neighbour
                    if(current.node.nodeID != s.start.nodeID && current.node.nodeID != s.end.nodeID){
                        System.err.println(("43 Neither end of segment " + s.start.nodeID + s.end.nodeID + " is the current Node " + current.node.nodeID));
                    }
                    Node neighbour = s.end;
                    if (s.end.nodeID == current.node.nodeID) {
                        neighbour = s.start;
                    }
                    if(!visited.contains(neighbour.nodeID)){
//                      If the neighbour hasn't been visited, calculate its g and f values
//                      g is the cost (d or time) to current node from the start, plus the weight (d or time) along the segment
                        g = current.g_Value + g_function(s, isTime); // cumulative cost (time or distance) to get to the neighbouring node
//                      the heuristic, h_function, is the crow flies distance or time. f is g + h ie  estimated total cost from the start to the end - we select minimal f
                        f = g + h_function(neighbour, targetNode, isTime);
//                      System.out.println("46 Segment start " + s.start.nodeID + " end " + neighbour.nodeID + " neighbour nodeID  " + neighbour.nodeID+ " seg g = " + g + " f = " + f);
                        // create an AStar <node, prev, g, f> of the unvisited neighbour and add it to the Priority Queue fringe
                        AStar next = new AStar(neighbour, current.node, g, f);
                        fringe.add(next);
                    }
                }
                System.out.println("47 Peek PQ best neighbour: " + fringe.peek().node.nodeID + " g "   + fringe.peek().g_Value + " and f " + fringe.peek().f_Value);
                //once all its segments are added we are finished with current node
                // we've marked it as visited, removed it from the fringe, and populated the fringe with it's unvisited neighbours
            }
        }
        // if there are no paths to the targetNode, the fringe will be empty and the targetNode will not be in visited.
        if(fringe.isEmpty() || fringe == null) {
            System.err.println("There is no nodes connected to startNode" + startNode.nodeID + " to targetNode " + targetNode.nodeID);
        }
        int numVisits = visited.size();
        boolean pathExists = false;
        for (AStar p : path) {
            if (p.node.nodeID == targetNode.nodeID) {
                pathExists = true;
            }
        }
        if (!pathExists){
            System.err.println("78 There is no path from startNode" + startNode.nodeID + " to targetNode " + targetNode.nodeID);
        }
//        System.out.println("last visit in visited " + path.get(numVisits - 1).node.nodeID);
        return path;
    }

    public Collection<Segment> TrackPrev(ArrayList<AStar> visited, Node startNode, Node targetNode){
        Collection<Segment> shortestPath = new HashSet<>();
        Node thisNode;
        Node prevNode;
        if (!visited.isEmpty()) {
            int numNodes = visited.size();
                AStar thisAstar = visited.get(numNodes-1);
                thisNode = thisAstar.node;

            // if the startNode is the same as the end of the path, or there is only one node return an empty path
                if(thisNode.nodeID == startNode.nodeID && numNodes <= 1) {
                    shortestPath.isEmpty();
                    System.err.println("Last node " + thisNode.nodeID + " is the startNode " + startNode.nodeID);
                    return shortestPath;
                }
                // this method will only work if the last element in visited is the targetNode
                if(thisNode.nodeID != targetNode.nodeID) {
                    System.err.println("Last node " + thisNode.nodeID + " is not the target " + targetNode.nodeID);
                    shortestPath.isEmpty();
                    return shortestPath;
                }
//                System.out.println("Last ID " + thisNode.nodeID + " prev node ID " + prevNode.nodeID);
                //Now that we have the correct end node and there are at least two nodes in the visited list
                int n =0;
                while (n < visited.size()){
                    thisNode = thisAstar.node;
                    prevNode = thisAstar.prev;
                    for(Segment s : thisNode.segments) {
                        if (s.end == prevNode || s.start == prevNode) {
                            shortestPath.add(s);
                            break;
                        }
                    }
                    if (prevNode.nodeID == startNode.nodeID) {
                        // we have found the end of the path and added it to the shortestPath
                        return shortestPath;
                    }
                    System.out.println("Node " + thisNode.nodeID + " prev " + prevNode.nodeID + " n " + n);
                    for (AStar a : visited) {
                        if (a.node.nodeID == prevNode.nodeID) {
                            thisAstar = a;
                            break;
                        }
                    }
                    n++;
//                  pseudocode. track back from targetNode at visited.get(numNodes-1). Find its previous node.
//                  if that previous node is the startNode, we are done
                }
            }
        return shortestPath;
    }

    public double g_function(Segment seg, boolean isTime){
        double g = seg.length;
        int speed;
        if (isTime) {
//            0 = 5km/h
//            1 = 20km/h  2 = 40km/h  3 = 60km/h  4 = 80km/h  5 = 100km/h ie speed limit * 20
//            6 = 110km/h 7 = no limit - assume also 110 km/h as no limits in NZ are above that currently
            speed = seg.road.speed_limit*20;
            if (seg.road.speed_limit <= 0){
                speed = 5;
            }
            if(seg.road.speed_limit >= 6){
                speed = 110;
            }
            g = g/speed; //distance divided by speed kms/km/hr = hours
        }
        return g;
    }
    public double h_function(Node node, Node targetNode, boolean isTime){
//      the heuristic is the straight line distance btw Node and target (by time, divide by highest speed limit)
        double h = node.location.distance(targetNode.location);
        if(isTime) h = h/110;
        return h;
        // distance between two Nodes
    }

    public double FindLength(Collection<Segment> shortestPath, boolean isTime) {
        double length = 0;
        for (Segment s : shortestPath){
            length += s.length;
        }
        return length;
    }

    class SegmentComparator implements Comparator<AStar>{
        public int compare(AStar a1, AStar a2) {
            if (a1.f_Value < a2.f_Value)
                return -1;
                else if (a1.f_Value >= a2.f_Value) return 1;
                return 0;
        }
    }
}
