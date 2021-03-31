import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class AStar {
    public final Node node;
    public final Node prev;
    public final double g_Value;
    public final double f_Value;

    public AStar(Node node, Node prev, double g_Value, double f_Value) {

        this.node = node;
        this.prev = prev;
        this.g_Value = g_Value;
        this.f_Value = f_Value;
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
}
