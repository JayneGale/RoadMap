import java.util.*;
//import java.util.PriorityQueue;

public class AStarPath {
    protected double g;
    protected double f;

    public ArrayList<AStar> FindPath(Node startNode, Node targetNode, boolean isTime){
        ArrayList<AStar> path = new ArrayList<>();
        ArrayList<Node> visited = new ArrayList<>();
//        ArrayList<Segment> fringed =  new ArrayList<>(); // segment so I can Map it easily
        // define an AStar object containing Node, g weight to this node, H estimate to goalNode
        PriorityQueue<AStar> fringe = new PriorityQueue<>(30, new SegmentComparator());
// TODO Does visited have to be a node now I have path or can it be a nodeID?
        AStar start = new AStar(startNode, null, 0, h_function(startNode, targetNode, isTime));
        fringe.add(start);
//        visited.add(startNode);
        while(fringe.peek() != null) {
            // remove the top element from the priority queue
            AStar current = fringe.poll();
            System.out.println("21 Poll priority queue node " + current.node.nodeID + " number of segments: " +current.node.segments.size());
            if (fringe.contains(current)) {
                System.out.println("23 Fringe still contains node " + current.node.nodeID + " number of segments: " + current.node.segments.size());
            }
            if (visited.contains(current.node)){
                System.out.println("Skip this node, already visited" + current.node.nodeID);
            }
            if(!visited.contains(current.node)){
                visited.add(current.node);
                path.add(current);
                // if the target Node is now top of the queue, we have reached the target // we don't stop the first time we reach the target as the endNode, only when we visit the target for the first time
                if (current.node.nodeID == targetNode.nodeID) {
                    System.out.println("24 Reached the target! " + current.node);
                    break;
                }
                //Populate the fringe with the current node's unvisited neighbours
                for (Segment s : current.node.segments) {
                    Node neighbour = s.end;
                    if(neighbour.nodeID == current.node.nodeID){
                        neighbour = s.start;
                    }
                    if(!visited.contains(neighbour)){
//                      calculate the neighbouring nodes g and f values
//                      g for the segment end node is the cost (distance or time) to current node from the start, plus the weight (distance or time) along the segment
                        g = current.g_Value + g_function(s, isTime); // cumulative cost (time or distance) to get to the neighbouring node
//                      the heuristic, h_function, is the crow flies distance or time. f is g + h ie  estimated total cost from the start to the end - we select minimal f
                        f = g + h_function(neighbour, targetNode, isTime);
//                        System.out.println("46 Segment start " + s.start.nodeID + " end " + neighbour.nodeID + " neighbour nodeID  " + neighbour.nodeID+ " seg g = " + g + " f = " + f);
                        AStar next = new AStar(neighbour, current.node, g, f);
                        fringe.add(next);
                    }
                }
                System.out.println("47 Peek priority queue best neighbouring node is " + fringe.peek().node.nodeID + " g "   + fringe.peek().g_Value + " and f " + fringe.peek().f_Value);
                //once all its segments are added we are finished with current node
                // we've marked it as visited, removed it from the fringe, and populated the fringe with it's unvisited neighbours
            }
//            System.out.println("53 Added unvisited neighbours of " + current.node.nodeID + " fringe size is " + fringe.size());
        }
        // if there are no paths to the targetNode, the fringe will be empty and the targetNode will not be in visited.
        if(visited.isEmpty()) {
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
            System.err.println("There is no path from startNode" + startNode.nodeID + " to targetNode " + targetNode.nodeID);
        }
        System.out.println("last visit in visited " + path.get(numVisits - 1).node.nodeID);
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
                prevNode = thisAstar.prev;

                // if the startNode is the same as the end of the path, or there is only one node return an empty path
                if(thisNode.nodeID == startNode.nodeID && numNodes <= 1) {
                    shortestPath.isEmpty();
                    System.err.println("Last node " + thisAstar.node.nodeID + " is the startNode " + startNode.nodeID);
                    return shortestPath;
                }
                // this method will only work if the last element in visited is the targetNode
                if(thisNode.nodeID != targetNode.nodeID) {
                    System.err.println("Last node " + thisAstar.node.nodeID + " is not the target " + targetNode.nodeID);
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
                    for (AStar a : visited) {
                        if (a.node.nodeID == prevNode.nodeID) {
                            thisAstar = a;
                            break;
                        }
                    }
                    n++;
                    System.out.println("Next node is " + thisAstar.node.nodeID + " and its prev " + thisAstar.prev.nodeID);
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
            g = g/speed; //distance divided by speed
        }
        return g;
    }
    public double h_function(Node node, Node targetNode, boolean isTime){
//      the heuristic is the straight line distance between the point and the target
        double h = node.location.distance(targetNode.location);
        if(isTime) h = h/110;
        return h;
        // distance between two Nodes
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
