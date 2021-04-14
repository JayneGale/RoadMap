import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Node represents an intersection in the road graph. It stores its ID and its
 * location, as well as all the segments that it connects to. It knows how to
 * draw itself, and has an informative toString method.
 * 
 * @author tony
 */
public class Node {

	public final int nodeID;
	public final Location location;
	public final Collection<Segment> segments;
	// add a pair of incoming and outgoing adjacency lists
	public final Collection<Segment> outGoing;
	public final Collection<Segment> incoming;
	public final ArrayList<Integer> nextNodeIDs;

	public Node(int nodeID, double lat, double lon) {
		this.nodeID = nodeID;
		this.location = Location.newFromLatLon(lat, lon);
		this.segments = new HashSet<Segment>();
		this.outGoing = new HashSet<Segment>();
		this.incoming = new HashSet<Segment>();
		this.nextNodeIDs = new ArrayList<Integer>();

	}

	public void addSegment(Segment seg) { segments.add(seg); }

	public void addAdjacencyLists(Node node) {
		for (Segment s : node.segments){
//			 create the node neighbours for the AP algorithm
//		     to get the right number of nodes, I found I had to ignore the 'notforcar' restricution

			if(this.nodeID == s.start.nodeID){
				nextNodeIDs.add(s.end.nodeID);
			}
			else {
				nextNodeIDs.add(s.start.nodeID);
			}

//			 don't allow any roads that are not for cars in the A* algorithm
			if(s.road.notforcar == 1){
				continue;
			}

//				One_way:
//				----------
//				0 : both directions allowed
//				1 : one way road, direction from beginning to end
//
//					this is not a oneway road, it is two edges one going each way; add it to both incoming and outgoing adj lists
			if (s.road.oneway == 0){
				outGoing.add(s);
				incoming.add(s);
//					BUT still have to check the order pf the nodes; set the segment start as this Node and end as the other one
//			for this node, could make a new edge that is the reverse of the old one but don't need to
			}
			if (s.road.oneway == 1){
				if(this.nodeID == s.start.nodeID){
					outGoing.add(s);
//				this node is the start of one way segment s; add it only to the outgoing adjacency list
				}
				else if(this.nodeID == s.end.nodeID){
					incoming.add(s);
//				this node is the end of one-way road segment s ie add it only to the incoming adjacency list
				}
			}
		}
	}

	public void draw(Graphics g, Dimension area, Location origin, double scale) {
		Point p = location.asPoint(origin, scale);

		// for efficiency, don't render nodes that are off-screen.
		if (p.x < 0 || p.x > area.width || p.y < 0 || p.y > area.height)
			return;

		int size = (int) (Mapper.NODE_GRADIENT * Math.log(scale) + Mapper.NODE_INTERCEPT);
		g.fillRect(p.x - size / 2, p.y - size / 2, size, size);
	}

	public String toString() {
		Set<String> edges = new HashSet<String>();
		for (Segment s : segments) {
			if (!edges.contains(s.road.name))
				edges.add(s.road.name);
		}

		String str = "ID: " + nodeID + "  loc: " + location + "\nroads: ";
		for (String e : edges) {
			str += e + ", ";
		}
		return str.substring(0, str.length() - 2);
	}
}

// code for COMP261 assignments