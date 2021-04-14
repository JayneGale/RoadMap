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
		for (Segment s : node.segments) {
			if (this.nodeID == s.start.nodeID) {
				nextNodeIDs.add(s.end.nodeID);
			} else {
				nextNodeIDs.add(s.start.nodeID);
			}
// only add segments that allow cars ie notforcar == 0
			if (s.road.notforcar == 0) {
//				test for one way roads

//			 don't allow any roads that are not for cars in the A* algorithm
			if(s.road.notforcar == 1){
				continue;
			}

//				One_way:
//				----------
//				0 : both directions allowed
//				1 : one way road, direction from beginning to end
//
//					if its NOT a oneway road, add both segmentts; ie it is two edges, one going each way; add s to both incoming and outgoing adj lists
				if (s.road.oneway == 0) {
					outGoing.add(s);
					incoming.add(s);
//					check the order of the nodes; set the segment start as this Node and end as the other one
				}
				if (s.road.oneway == 1) {
					if (this.nodeID == s.start.nodeID) {
						outGoing.add(s);
//				this node is the start of one way segment s; add it only to the outgoing adjacency list
					} else if (this.nodeID == s.end.nodeID) {
						incoming.add(s);
//				this node is the end of one-way road segment s ie add it only to the incoming adjacency list
					}

//					Other restrictions
//					five values:  nodeID-1, roadID-1, nodeID, roadID-2, nodeID-2.
//
//					nodeID, the middle NodeID, specifies the intersection involved. ie this node.nodeID
//							The restriction specifies that it is not permitted to turn from
//							the road segment of roadID-1 going between nodeID-1 and this intersection, nodeID
//							into the road segment of roadID-2 going between this intersection, nodeID and nodeID-2
//					pseudocode
//					if(this.nodeID.hasRestrictions) (new Restrictions list has size (of 5) > 0)
//					move is banned if
//					if(s.road.roadID == roadID-1)
					// case1: s.start = this node, s.end = node1 road = road 1
					// case2: s.start = node1 s.end = this node, road = road 1
//					AND
//					if(s.road.roadID == roadID-1)
					// case1: s.start = this node, s.end = node2 road = road 2
					// case2: s.start = node2 s.end = this node, road = road 2
//					AND ?? s.roadID can't be both roadID 1 and roadID 2
//					run out of time
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