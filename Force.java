class Force
{
    private Nodes node;
    private double force;
    private String units;

    public Force(Nodes node, double force, String units)
    {
        this.node = node;
        node.setNodeForce(force);
        this.force = force;
        this.units = units;
    }

    public Nodes getNode()
    {
        return this.node;
    }

    public double getForce()
    {
        return this.force;
    }

    public String getUnits()
    {
        return this.units;
    }
}