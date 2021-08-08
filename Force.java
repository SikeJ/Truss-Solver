class Force
{
    private Nodes node;
    private Double force;
    private String units;

    public Force(Nodes node, Double force, String units)
    {
        this.node = node;
        this.force = force;
        this.units = units;
        //this.node.setNodeForce(force);
    }

    /** Returns what node the current force is acting upon (null if the force is a beam) */
    public Nodes getNode()
    {
        return this.node;
    }

    /** Returns the force so that ti can be used in other methods */
    public Double getForce()
    {
        return this.force;
    }

    /** Returns the units so that everything can be calculated correctly */
    public String getUnits()
    {
        return this.units;
    }

    /** Sets the units of the Force if they want to be changed, also will recalculate the Force
     *      for the new units */
    public void setUnits(String units)
    {
        this.units = units;
    }

    /** Allows for the Force to be set by outside methods */
    public void setForce(Double force)
    {
        this.force = force;
    }
}