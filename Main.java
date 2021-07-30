class Main
{
	public static void main(String [] args)
	{
		Nodes node1 = new Nodes('A', 0, 0, true);
		Nodes node2 = new Nodes('B', 10, 0, true);
		Nodes node3 = new Nodes('C', 5, 5, false);
		Nodes node4 = new Nodes('D', -5, 5, false);
		
		Beam beam = new Beam(node1, node2);
		Beam beam2 = new Beam(node1, node3);
		Beam beam3 = new Beam(node2, node3);
		Force force = new Force(node3, 100.0, "lbf");
		Force force2 = new Force(node4, -200.0, "lbf");
		List<Force> appliedForces = new List<Force>(force);
		appliedForces.append(force2);


		Beam[] beams = {beam, beam2, beam3};

		Nodes[] nodes = {node1, node2, node3};

		for(Nodes place:nodes)
		{
			if(place.getSupport())
			{
				int length = 0;
				double testForce = 0;
				while(length < appliedForces.size())
				{
					Force test = appliedForces.getValueAt(length);
					Nodes tested = test.getNode();
					double calc = test.getForce() *(place.getX() - tested.getX()); 
					testForce += calc;
					length++;
				}
				place.setNodeForce(testForce/10);
				System.out.println("Node " + place.getName() + " has a reaction of: " + place.getNodeForce());
			}
		}
	}
}
