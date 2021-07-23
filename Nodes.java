class Nodes
{
	//setup private variables for the node class
	private char name;
	private List<Nodes> connections;
	private double x;
	private double y;
	private Double nodeForce;
	
	
	/** Constructor for the node class */
	public Nodes(char name, double x, double y)
	{
		//Sets up a node  
		this.name = name;
		this.x = x;
		this.y = y;
		this.nodeForce = null;
		this.connections = new List<Nodes>();
	}
	
	public char getName()
	{
		return this.name;
	}
	
	public List<Nodes> getConnections()
	{
		return this.connections;
	}
	
	public double[] getCoords()
	{
		double[] coords = {this.x, this.y};
		return coords;
	}
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}

	public Double getNodeForce()
	{
		return nodeForce;
	}
	
	public void setX(double xval)
	{
		this.x = xval;
	}
	
	public void setY(double yval)
	{
		this.y = yval;
	}

	public void setNodeForce(double force)
	{
		this.nodeForce = force;
	}
	
	public void addConnect(Nodes node)
	{
		this.connections.append(node);
	}
}