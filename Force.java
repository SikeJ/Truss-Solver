class Force
{
    private Nodes node;
    private Double force;
    private String units;
    private double xforce;
    private double yforce;

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

    /** Allows for the x components of the force to be set */
    public void setXforce(double force)
    {
        this.xforce = force;
        this.force = Math.sqrt(Math.pow(this.xforce, 2) + Math.pow(this.yforce, 2));
    }

    /** Allows for the y component of the force to be set */
    public void setYforce(double force)
    {
        this.yforce = force;
        this.force = Math.sqrt(Math.pow(this.xforce, 2) + Math.pow(this.yforce, 2));
    }

    /** Returns the x component of the force */
    public double getXforce()
    {
        return this.xforce;
    }

    /** Returns the y component of the force */
    public double getYforce()
    {
        return this.yforce;
    }
}