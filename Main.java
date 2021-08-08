class Main
{
	public static void main(String [] args)
	{
		Nodes node1 = new Nodes('A', 0, 0, true);
		Nodes node2 = new Nodes('B', 10, 0, true);
		Nodes node3 = new Nodes('C', -5, 5, false);
		Nodes node4 = new Nodes('D', -5, 5, false);
		
		Beam beam = new Beam(node1, node2);
		Beam beam2 = new Beam(node1, node3);
		Beam beam3 = new Beam(node2, node3);
		Beam beam4 = new Beam(node1, node4);
		Force force = new Force(node3, 100.0, "lbf");
		Force force2 = new Force(node4, 200.0, "lbf");
		List<Force> appliedForces = new List<Force>(force);
		//appliedForces.append(force2);


		Nodes[] supports = {node1, node2};

		Nodes[] nodes = {node1, node2, node3};

		//Finds support reactions. For 3+ supports have to solve integrals
		for(int i = 0; i < supports.length; i++)
		{
			int j;
			if(i == 0)
				j = 1;

			else
				j = 0;

			Nodes place = supports[j];
			int length = 0;
			double testForce = 0;
			while(length < appliedForces.size())
			{
				Force test = appliedForces.getValueAt(length);
				Nodes tested = test.getNode();
				double calc = test.getForce() * (place.getX() - tested.getX()); 
				testForce += calc;
				length++;
			}
			supports[i].setNodeForce(testForce/(supports[j].getX() - supports[i].getX()));
		}

		//Loops through the beams to calculate beam force
		for(Nodes node:nodes)
		{
			int noForce = 0;
			List<Beam> beams = node.getConnections();
			for(int i = 0; i < beams.size(); i++)
			{
				Beam test = beams.getValueAt(i);
				if(test.getForce() == null)
					noForce++;
			}

			switch(noForce)
			{
				case 1:
					node.printNode();
					break;

				case 2:
					node.printNode();
					break;

				case 3:
					node.printNode();
					break;

				default:
					System.out.println("Switch cases are pretty fun.");
			}
			
		}
	}
}
