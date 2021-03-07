class Members
{
	private Nodes node1;
	private Nodes node2;
	private Float force;
	private float dx;
	private float dy;
	private float hyp;
	private String name;
	
	public Members(Nodes node1, Nodes node2)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.dx = node1.getX() - node2.getX();
		this.dy = node1.getY() - node2.getY();
		this.force = null;
		this.hyp = (float)Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		this.name = "" + node1.getName() + node2.getName();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Float getForce()
	{
		return this.force;
	}
	
	public float getDX()
	{
		return this.dx;
	}
	
	public float getDY()
	{
		return this.dy;
	}
	
	public float getHYP()
	{
		return this.hyp;
	}
	
	public Nodes[] getNodes()
	{
		Nodes[] ends = {node1, node2};
		return ends;
	}
}