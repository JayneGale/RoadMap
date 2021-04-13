import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 255, 30);
	public static final Color TARGET_COLOUR = new Color (255, 100, 0);
	public static final Color SHORTEST_PATH__COLOUR = new Color(107, 253, 60);
	public static final Color ARTICULATION_POINT = new Color(255,0, 34);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale = 3;

	// our data structures.
	private Graph graph;
	private Trie trie;

	private Node startNode = null;
	private Node targetNode = null;
	private boolean isTime = false;

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {
		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
//			graph.setHighligt(closest);
			String roadNames = nodeNames(closest);
				getTextOutputArea().setText(roadNames);
			}
			if (startNode == null) {
				startNode = closest;
				graph.setHighlight(startNode);
			} else {
				targetNode = closest;
				graph.setTargetHighlight(startNode, targetNode);
			}
		}

	@Override
	protected void onSearch() {
		if (trie == null)
			return;

		// get the search query and run it through the trie.
		String query = getSearchBox().getText();
		Collection<Road> selected = trie.get(query);

		// figure out if any of our selected roads exactly matches the search
		// query. if so, as per the specification, we should only highlight
		// exact matches. there may be (and are) many exact matches, however, so
		// we have to do this carefully.
		boolean exactMatch = false;
		for (Road road : selected)
			if (road.name.equals(query))
				exactMatch = true;

		// make a set of all the roads that match exactly, and make this our new
		// selected set.
		if (exactMatch) {
			Collection<Road> exactMatches = new HashSet<>();
			for (Road road : selected)
				if (road.name.equals(query))
					exactMatches.add(road);
			selected = exactMatches;
		}

		// set the highlighted roads.
		graph.setHighlight(selected);

		// now build the string for display. we filter out duplicates by putting
		// it through a set first, and then combine it.
		Collection<String> names = new HashSet<>();
		for (Road road : selected)
			names.add(road.name);
		String str = "";
		for (String name : names)
			str += name + "; ";

		if (str.length() != 0)
			str = str.substring(0, str.length() - 2);
		getTextOutputArea().setText(str);
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}
	//the below as provided by Dr Mei
	@Override
	protected void onAStar() {
		if(startNode == null || targetNode == null){
			getTextOutputArea().setText("Need to specify both start and target");
		}
		else {
			DoAStar(startNode, targetNode, isTime);
		}
		// reset the startNode and targetNode when finished
		startNode = null;
		targetNode = null;
	}

	public AStarPath path = new AStarPath();

	public void DoAStar(Node startNode, Node targetNode, boolean isTime) {
		List<Segment> shortestPath;
		ArrayList<AStar> visitedNodes;
		visitedNodes = path.FindPath(startNode, targetNode, isTime);
		shortestPath = path.TrackPrev(visitedNodes, startNode, targetNode);
		String str;
		if(shortestPath.isEmpty()) {
			str = "No path from ID:" + startNode.nodeID + " loc:" + startNode.location + " to ID:" + targetNode.nodeID + " loc:" + targetNode.location;
		}
		else {
//			since segments were added from target back to start, to give directions we need to reverse the list
			Collections.reverse(shortestPath);
			graph.setShortestPathColour(shortestPath);
			String startRoadNames = nodeNames(startNode);
			String targetRoadNames = nodeNames(targetNode);
			String str1 = "\n Shortest path from " + startRoadNames + " to " + targetRoadNames;
			getTextOutputArea().setText(str1);

			double weight = path.final_g_Value;
			double finalLen = path.FindLength(shortestPath, isTime);
//			double totLen = 0;

			String thisRoadName = "";
			String capName = "";
			double roadLen = 0;
			for (int i = 0; i < shortestPath.size(); i++){
				Segment s = shortestPath.get(i);
//				System.out.println("Segment " + i + " road " + s.road.name + " " + s.length +"km");
				if(s.road.name == null){
					System.out.println("Gotta handle null roadName: " + s.length + "km, roadID "  + s.road.roadID);
				}
// if the new segment does NOT have the same name as the last segment, store the last one's CapName and roadLen
				if (s.road.name != thisRoadName) {
					// there is no previous segment for i = 0, and the final segment has no next segment name
					if (i != 0) {
						String roadLen2 = String.format("%.1f", roadLen);
						getTextOutputArea().append("\n " + capName + " " + roadLen2 + " km");
					}
// Capitalise the new roadName; restart the length
					thisRoadName = s.road.name;
					String roadN = thisRoadName.substring(0, 1).toUpperCase();
					capName = roadN + thisRoadName.substring(1);
//					totLen += roadLen;
					roadLen = s.length;
				}
//				else s.road.name does = thisRoadName, its a continuation of the same road so cumulate the roadLen
				else {
					roadLen += s.length;
				}
//				getTextOutputArea().append("\n " + capName + String.format(" %.2fkm", roadLen));
//				totWeight += s.length;
				if(i >= shortestPath.size()-1){
					String roadLen2 = String.format("%.1f", roadLen);
					getTextOutputArea().append("\n " + capName + " " + roadLen2 + " km");
//					totLen += roadLen;
				}
			}
//			System.out.println("233 Total length " + totLen);
//			This DOES calculate the total distance correctly both via the algorithm and by adding the segments separately
			str = String.format("\n Total Distance %.1f km", finalLen);
			if(isTime){
				str = String.format("\n Total time %.2f min, distance %.1f km", weight*60, finalLen);
			}
			}
		getTextOutputArea().append(str);
		}

	@Override
	protected void onSpeed() {
	//		this is a toggle button between time and distance
		// it'd be nicer to set up 2 buttons, distance and time;
		// or radio buttons or checkboxes: start with distance checked; if user clicks time true, distance is automatically set to false

		isTime = !isTime;
		String str = "Calculating by ";
		if(isTime){
			str = str + "time";
		}
		if(!isTime)
			str = str + "distance";
		getTextOutputArea().setText(str);
	}

	public ArticulationPoints AP = new ArticulationPoints();

	@Override
	protected void onAPs() {
		// set up APObjects with depth -1 for all the Nodes
		Node root = startNode;
		if (startNode == null){
			root = graph.nodes.get(12420); // a random node
			System.out.println("StartNode null; set root node = 12420: " + root.nodeID + root.location + root.toString());
		}
		// Find Articulation Points
		System.out.println("Mapper269 set up AP graph unvisited");
		HashMap<Integer,APObject> APObjects = AP.SetAllUnvisited(graph);
		HashSet<Node> APs = new HashSet<>();
		APs.clear();
		while (root != null){
			APs.addAll(AP.FindAPs(root, APs));
			Node newRoot = AP.checkDisjointSets(graph);
//			System.out.println("Mapper273 APs size" + APs.size() + " newRoot: " + newRoot);
			root = newRoot;
		}
		// highlight all the APs on the graph
		graph.highLightAPs(APs);
		getTextOutputArea().setText("There are " + APs.size() + " Articulation points");
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		trie = new Trie(graph.roads.values());
		origin = new Location(-8, 4); // small
		scale = 75;
//		origin = new Location(-160, 200); // large
//		scale = 3;
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}

	public String nodeNames(Node node) {
		String roadNames = "";
		String thisRoadName = "";
		int size = node.segments.size();
		if (size > 1) {
			roadNames = "Corner of ";
		}
		for (Segment s : node.segments) {
//			System.out.println(s.road.name);
			if (s.road.name != null) {
				if (thisRoadName != s.road.name) {
					thisRoadName = s.road.name;
					String roadN = thisRoadName.substring(0, 1).toUpperCase();
					String capName = roadN + thisRoadName.substring(1);
					roadNames = roadNames + capName + " ";
//					System.out.println("nodeNames Mapper 310 " + capName);
				}
			}
		}
		return roadNames;
	}
}


// code for COMP261 assignments