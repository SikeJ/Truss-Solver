class Nodes
{
	//setup private variables for the node class
	private char name;
	private List<Character> connections;
	private float x;
	private float y;
	
	
	/** Constructor for the node class */
	public Nodes(int clicks)
	{
		//Sets up a node  
		this.name = (char) (65 + clicks);
		this.connections = new List<Character>();
	}
	
	public char getName()
	{
		return this.name;
	}
	
	public List<Character> getConnections()
	{
		return this.connections;
	}
	
	public float[] getCoords()
	{
		float[] coords = {this.x, this.y};
		return coords;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public void setX(float xval)
	{
		this.x = xval;
	}
	
	public void setY(float yval)
	{
		this.y = yval;
	}
	
	public void addConnect(char node)
	{
		this.connections.append(node);
	}
}