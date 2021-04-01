import java.util.*;
import java.util.PriorityQueue;

public class AStarPath {
    public Collection<Segment> FindPath(Node startNode, Node targetNode, boolean isTime){
        Collection<Segment> shortestPath = null;
        HashSet<Node> visited = new HashSet<>();
        // define an AStar object containing Node, g weight to this node, H estimate to goalNode
        PriorityQueue<AStar> fringe = new PriorityQueue<AStar>(100, new SegmentComparator());
        AStar current = new AStar(startNode, null, 0, f_function(startNode, targetNode, isTime));
        //mark startNode as visited and remove from the fringe, and populate the fringe with startNode's nieghbours
        visited.add(startNode);

        //Populate the fringe with the current node's neighbours
        for (Segment s: startNode.segments){
            AStar next = new AStar(s.end, current.node, current.g_Value + g_function(s, isTime), f_function(s.end, targetNode, isTime));
            System.out.println("segments nodeID " + next.node.nodeID + " prev nodeID " + next.prev.nodeID + " g_value " + next.g_Value + " f_value "  + next.f_Value);
            fringe.add(next);
            System.out.println(fringe.size());
        }
//
//		shortestPath.add(path); //have to add the first segment, not the node
        return shortestPath;

    }
    public double g_function(Segment seg, boolean isTime){
        double g = seg.length;
        int speed;
        if (isTime) {
//            0 = 5km/h
//            1 = 20km/h  2 = 40km/h  3 = 60km/h  4 = 80km/h  5 = 100km/h
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
    public double f_function(Node node, Node targetNode, boolean isTime){
        double f = node.location.distance(targetNode.location)/110;
        return f;
        // distance between two Nodes
    }

    class SegmentComparator implements Comparator<AStar>{
        public int compare(AStar a1, AStar a2) {
            if ((a1.f_Value + a1.g_Value) < (a2.f_Value + a2.g_Value))
                return -1;
                else if ((a1.f_Value + a1.g_Value) >= (a2.f_Value + a2.g_Value)) return 1;
                return 0;
        }
    }
}
