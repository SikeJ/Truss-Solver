class Nodes
{
	//setup private variables for the node class
	private char name;
	private List<Beam> connections;
	private double x;
	private double y;
	private Double nodeForce;
	private boolean support;
	
	/** Constructor for the Node class */
	public Nodes(char name, double x, double y, boolean support)
	{
		//Sets the name, location of the current Node
		this.name = name;
		this.x = x;
		this.y = y;

		//Sets the reaction force at the Node to null and starts a list of connections
		this.nodeForce = null;
		this.connections = new List<Beam>();

		//sets whether the Node will have reaction forces
		this.support = support;
	}
	
	/** Retruns the name of the Node */
	public char getName()
	{
		return this.name;
	}
	
	/** Returns a list of the other Nodes that are connected to the current node */
	public List<Beam> getConnections()
	{ 
		return this.connections;
	}
	
	/** Returns the Coords of the current node */
	public double[] getCoords()
	{
		double[] coords = {this.x, this.y};
		return coords;
	}
	
	/** Returns the X coordinate */
	public double getX()
	{
		return this.x;
	}
	
	/** Returns the Y coordinate */
	public double getY()
	{
		return this.y;
	}

	/** Returns the Force applied at the node if there is any */
	public Double getNodeForce()
	{
		return nodeForce;
	}
	
	/** Sets the Y coordinate, in case the node wants to be moved */
	public void setX(double xval)
	{
		this.x = xval;
	}
	
	/** Sets the Y coordinate, in case the node wants to be moved */
	public void setY(double yval)
	{
		this.y = yval;
	}

	/** Let's you set the force applied at the Node */
	public void setNodeForce(double force)
	{
		this.nodeForce = force;
	}
	
	/** Adds a Node to thee connection list */
	public void addConnect(Beam beam)
	{
		this.connections.append(beam);
	}

	/** Removes a connenction from the list */
	public void removeConnet(Beam beam)
	{
		this.connections.deleteAt(connections.positionOf(beam));
	}

	/** Returns whether the Node is supported */
	public boolean getSupport()
	{
		return this.support;
	}
}