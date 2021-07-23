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
		
		this.dx = node1.getX() - node2.getX();
		this.dy = node1.getY() - node2.getY();
		this.force = 100.0;
		this.hyp = (double)Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		this.name = "" + node1.getName() + node2.getName();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public double getForce()
	{
		return this.force;
	}
	
	public double getDX()
	{
		return this.dx;
	}
	
	public double getDY()
	{
		return this.dy;
	}
	
	public double getHYP()
	{
		return this.hyp;
	}
	
	public Nodes[] getNodes()
	{
		Nodes[] ends = {node1, node2};
		return ends;
	}
}