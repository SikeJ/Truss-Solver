class Main
{
	public static void main(String [] args)
	{
		Nodes node1 = new Nodes('A', 0, 0, true);
		Nodes node2 = new Nodes('B', 10, 0, true);
		Nodes node3 = new Nodes('C', 5, 5, false);
		Nodes node4 = new Nodes('D', -5, 5, false);
		Nodes node5 = new Nodes('E', 15, 5, false);
		
		Beam beam = new Beam(node1, node2);
		Beam beam2 = new Beam(node1, node3);
		Beam beam3 = new Beam(node2, node3);
		node3.setNodeForce(100.0);
		List<Force> appliedForces = new List<Force>(node3.nodeForce);

		//Creating a more complicated truss in order to test the program
		/*
		Beam beam4 = new Beam(node1, node4);
		Beam beam5 = new Beam(node4, node3);
		Beam beam6 = new Beam(node3, node5);
		Beam beam7 = new Beam(node5, node2);
		node4.setNodeForce(200.0);
		node5.setNodeForce(300.0);		
		appliedForces.append(node4.nodeForce);
		appliedForces.append(node5.nodeForce);
		Nodes[] nodes = {node1, node2, node3, node4, node5};
		*/

		Nodes[] supports = {node1, node2};

		Nodes[] nodes = {node1, node2, node3};

		//Finds support reactions. For 3+ supports have to solve integrals
		for(int i = 0; i < supports.length; i++)
		{
			Nodes place = supports[(i+1)%2];
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
			supports[i].setNodeForce(testForce/(place.getX() - supports[i].getX()));
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
