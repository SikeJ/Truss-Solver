// Class to contain all of the information for the truss to be used 
// allows easy access throughout the different classes
class Truss
{

	// Setup variables to be used throughout the class
	private Dictionary<Character, Node> trussNode;
	private Dictionary<String, Beam> trussBeam;
	private Dictionary<Character, Force> trussForce;
	private char nextNodeName;

	/** Constructor for the Truss Class */
	public static void main(String[] args)
	{
		// Initialize Dictionaries to keep the Nodes & Beams tracked
		trussNode = new Hashtable<>();
		trussBeam = new Hashtable<>();
		trussForce = new Hashtable<>();

		// Put the standard support Nodes into the hashtable
		trussNode.put('A', new Nodes('A', 0, 0, true));
		trussNode.put('Z', new Nodes('Z', 0, 25, true));

		// Sets the name for the next Node
		nextNodeName = 'B';
		
	}

	/** Establishes the location of the second supporting Node */
	public void addSupport(double x)
	{
		trussNode.put('Z', new Nodes('Z', x, 0, true));
	}

	/** Used to add a Node to the Truss */
	public void addNode(double x, double y)
	{
		Node newNode = new Nodes(nextNodeName, x, y, false);
		trussNode.put(nextNodeName, newNode);
		nextNodeName++;
	}

	/** Used to add a beam between two different Nodes */
	public void addBeam(Nodes node1, Nodes node2)
	{
		// Get the nodes corresponding to the names provided
		// Make sure that the nodes are there in this block or before??
		Nodes node1 = trussNode.get(name1);
		Nodes node2 = trussNode.get(name2);

		Beam newBeam = new Beam(node1, node2);

		trussBeam.put(newBeam.getName(), newBeam);
	}

	/** Used to add a force to any selected Node */
	public void addForce(double force, double angle, Nodes node)
	{
		Force newForce = new Force(node, force, "in");

		trussForce.put(node.getName(), newForce);
	}

	/** Used to solve the currently setup Truss */
	public void solveTruss()
	{
		// Loop through the forces that are acting on the Nodes to
		// Solve for the X and Y reactions of the supports
		// Node A is assumed to be pinned (have both X&Y reaction)
		// Node Z is assumed to be a slider (only have Y reaction)
		

		// With the support reactions found, the force of every member
		// can be determined

	}
}
