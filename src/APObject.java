import java.util.ArrayList;

public class APObject {
    public final Node n;
    public int depth;
    public int reachBack;
    public Node parent;
    public ArrayList<Integer> children;

    public APObject(Node n, int depth, int reachBack, Node parent, ArrayList<Integer> children) {
        this.n = n;
        this.depth = depth;
        this.reachBack = reachBack;
        this.parent = parent;
        this.children = children;
    }

}
