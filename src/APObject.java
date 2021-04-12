import java.util.ArrayList;

public class APObject {
    public final Node n;
    public int depth;
    public int reachBack;
    public APObject parent;
    public ArrayList<APObject> children;

    public APObject(Node n, int depth, int reachBack, APObject parent, ArrayList<APObject> children) {
        this.n = n;
        this.depth = depth;
        this.reachBack = reachBack;
        this.parent = parent;
        this.children = children;
    }

}
