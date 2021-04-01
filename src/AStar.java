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
}
