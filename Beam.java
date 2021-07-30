class Beam
{
	private Nodes node1;
	private Nodes node2;
	private Double force;
	private double dx;
	private double dy;
	private double hyp;
	private String name;
	
	public Beam(Nodes node1, Nodes node2)
	{
		this.node1 = node1;
		this.node2 = node2;

		node1.addConnect(node2);
		node2.addConnect(node1);
		
		this.dx = Math.abs(node1.getX() - node2.getX());
		this.dy = Math.abs(node1.getY() - node2.getY());
		this.force = 100.0;
		this.hyp = (double)Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		this.name = "" + node1.getName() + node2.getName();
	}

	/** Returns the name of the Beam */
	public String getName()
	{
		return this.name;
	}
	
	/** Returns the force the beam is carrying */
	public double getForce()
	{
		return this.force;
	}
	
	/** Returns the change in the X coordinate across the end points of the beam */
	public double getDX()
	{
		return this.dx;
	}
	
	/** Returns the change in the Y coordinate across the end points of the beam */
	public double getDY()
	{
		return this.dy;
	}
	
	/** Returns the length of the beam */
	public double getHYP()
	{
		return this.hyp;
	}
	
	/** Returns the nodes that the beam is connected to */
	public Nodes[] getNodes()
	{
		Nodes[] ends = {node1, node2};
		return ends;
	}
}