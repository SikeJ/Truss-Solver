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

		node1.unknowns++;
		node2.unknowns++;

		node1.addConnect(this);
		node2.addConnect(this);
		
		this.dx = (node1.getX() - node2.getX());
		this.dy = (node1.getY() - node2.getY());
		this.force = null;
		this.hyp = (double)Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		this.name = "" + node1.getName() + node2.getName();
	}

	/** Returns the name of the Beam */
	public String getName()
	{
		return this.name;
	}

	/** Sets the force that the beam carries */
	public void setForce(double force)
	{
		this.force = force;
		node1.unknowns--;
		node2.unknowns--;
	}
	
	/** Returns the force the beam is carrying */
	public Double getForce()
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

	/** This sets the 'view' of the Beam to accurately tell which way is positive and negative */
	public void setView(Nodes node)
	{
		if(node == this.node1)
		{
			this.dx = node1.getX() - node2.getX();
			this.dy = node1.getY() - node2.getY();
		}
		else
		{
			this.dx = node2.getX() - node1.getX();
			this.dy = node2.getY() - node1.getY();
		}
	}

	/** Returns the other node attached to the beam */
	public Nodes getOtherNode(Nodes node)
	{
		if(node == this.node1)
			return this.node2;

		else
			return this.node1;
	}

	/** Prints all of the beams information out */
	public void printBeam()
	{
		System.out.println("-------------------------------");
		System.out.printf("Beam: %s\n\u0394Location: (%.1f,%.1f)\nForce: %.2f lbf\n"
			, this.name, this.dx, this.dy, this.force);
	}
}