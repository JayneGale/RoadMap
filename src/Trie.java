import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This is an implementation of a trie, used for the search box. We cheat a bit
 * and store references to the actual Road objects in each node, blowing the
 * memory efficiency but making this easy to implement.
 * 
 * @author tony
 */
public class Trie {
	TrieNode root = new TrieNode();

	public Trie(Collection<Road> roads) {
		for (Road road : roads) {
			add(road);
		}
	}

	/**
	 * Adds a given road to the Trie.
	 */
	public void add(Road road) {
		// if we don't have any data on the road, leave it out.
		if (road.name.equals("-"))
			return;

		// traverse the trie using the name of the road. at each step, either a
		// node already exists for that letter (if) or we need to make a new one
		// (else).
		TrieNode node = this.root;
		char[] name = road.name.toCharArray();
		for (int i = 0; i < name.length; i++) {
			if (node.children.containsKey(name[i])) {
				node = node.children.get(name[i]);
			} else {
				TrieNode next = new TrieNode();
				node.children.put(name[i], next);
				node = next;
			}
		}

		// finally, add a reference to the road to the terminal node of its
		// name.
		node.data.add(road);
	}

	/**
	 * Returns all Roads whose names start with a given prefix.
	 */
	public Collection<Road> get(String prefix) {
		// first, traverse to the end of the prefix.
		TrieNode node = root;
		for (char c : prefix.toCharArray()) {
			node = node.children.get(c);

			if (node == null)
				return new HashSet<>();
		}

		// then, accumulate all the roads referenced by the Trie at the end of
		// the prefix.
		Collection<Road> names = new HashSet<>();
		traverse(node, names);
		return names;
	}

	/**
	 * Performs a traversal of the Trie rooted at the given TrieNode,
	 * accumulating all the Roads into a provided Collection.
	 */
	private static void traverse(TrieNode root, Collection<Road> elems) {
		elems.addAll(root.data);

		for (Character c : root.children.keySet()) {
			traverse(root.children.get(c), elems);
		}
	}

	/**
	 * Represents a single node in the trie. It contains a collection of the
	 * Roads whose names are exactly the traversal down to this node.
	 */
	private class TrieNode {
		Map<Character, TrieNode> children = new HashMap<>();
		Collection<Road> data = new HashSet<>();
	}
}

// code for COMP261 assignments