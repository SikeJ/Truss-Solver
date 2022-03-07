class Main
{
	public static void main(String [] args)
	{
		Nodes node1 = new Nodes('A', 0, 0, true);
		Nodes node2 = new Nodes('B', 50, 0, true);
		Nodes node3 = new Nodes('C', 25, 90, false);
		Nodes node4 = new Nodes('D', -75, 25, false);
		Nodes node5 = new Nodes('E', 15, 5, false);
		
		Beam beam = new Beam(node1, node2);
		Beam beam2 = new Beam(node1, node3);
		Beam beam3 = new Beam(node2, node3);
		//node3.setNodeForce(-100.0);
		List<Force> appliedForces = new List<Force>(node3.nodeForce);

		//SetUpGUI gui = new SetUpGUI();

		//Creating a more complicated truss in order to test the program
		
		Beam beam4 = new Beam(node1, node4);
		Beam beam5 = new Beam(node4, node3);
		Beam beam6 = new Beam(node3, node5);
		Beam beam7 = new Beam(node5, node2);
		node4.setNodeForce(200.0);
		node5.setNodeForce(300.0);		
		appliedForces.append(node4.nodeForce);
		appliedForces.append(node5.nodeForce);
		Nodes[] nodes = {node1, node2, node3, node4, node5};
		Beam[] beamed = {beam, beam2, beam3, beam4, beam5, beam6, beam7};
		

		Nodes[] supports = {node1, node2};
/*
		Nodes[] nodes = {node1, node2, node3};
		Beam[] beamed = {beam, beam2, beam3};
*/
		
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
				double calc = -test.getForce() * (place.getX() - tested.getX()); 
				testForce += calc;
				length++;
			}
			supports[i].setNodeForce(testForce/(place.getX() - supports[i].getX()));
		}

		//Starts a stack to perform a depth first search to try and calculate everything
		Stack<Nodes> stack = new Stack<Nodes>();
		boolean[] seenList = new boolean[nodes.length];
		seenList[node1.getName() - 'A'] = true;

		stack.push(node1);
		int whileTrace = 0;
		while(!stack.isEmpty())
		{
			Nodes node = stack.peek();

			int noForce = 0;
			List<Integer> beamPosition = new List<Integer>();
			List<Beam> beams = node.getConnections();
			for(int i = 0; i < beams.size(); i++)
			{
				Beam test = beams.getValueAt(i);

				Nodes nodeT = test.getOtherNode(node);

				for(int f = 0; f < nodes.length; f++)
				{
					if(!seenList[f])
					{
						seenList[f] = true;
						stack.push(nodes[f]);
					}
				}
				
				if(test.getForce() == null)
				{
					noForce++;
					beamPosition.append(i);
				}
			}

			//System.out.pritln("Checking at Node: " + node.getName());
			switch(noForce)
			{
				case 0:
					//System.out.pritln("Popping");
					stack.pop();
					break;

				case 1:
					int beamI = beamPosition.getValueAt(0);
					Beam testB = beams.getValueAt(beamI);
					testB.setView(node);
					double anss = 0;
					for(int pos = 0; pos < beams.size(); pos++)
					{
						if(pos != beamI)
						{
							Beam curBeam = beams.getValueAt(pos);
							////System.out.pritln("1Beam: " + curBeam.getName());
							curBeam.setView(node);
							anss -= curBeam.getForce() * curBeam.getDX() / curBeam.getHYP();
						}
					}
					//System.out.pritln(anss);

					double realans = (anss * testB.getHYP() / testB.getDX());
					//System.out.pritln(realans);
					testB.setForce(realans);
					stack.pop();
					break;

				case 2:
					int pos = 0;
					double[][] variables = new double[2][2];
					double xforce;
					double yforce;
					int[] bpos = new int[2];
					for(; pos < beamPosition.size(); pos++)
					{
						Beam curBeam = beams.getValueAt(beamPosition.getValueAt(pos));
						curBeam.setView(node);
						////System.out.pritln(node.getName());
						//curBeam.printBeam();
						////System.out.pritln("2Beam:" + curBeam.getName());
						xforce = curBeam.getDX() / curBeam.getHYP();
						yforce = curBeam.getDY() / curBeam.getHYP();
						//System.out.printf("X:%.2f Y:%.2f\n", xforce, yforce);
						variables[0][pos] = xforce;
						variables[1][pos] = yforce;
						bpos[pos] = beamPosition.getValueAt(pos);
						//System.out.printf("Fun X: %.2f Y: %.2f\n", variables[0][pos]
						//			, variables[1][pos]);
					}
					double[] answers = {0.0, 0.0};
					for(int j = 0; j < beams.size(); j++)
					{
						//System.out.printf("j:%d bpos:%d or %d\n", j, bpos[0], bpos[1]);
						if(j != bpos[0] && j != bpos[1])
						{
							Beam curBeam = beams.getValueAt(j);
							curBeam.setView(node);
							xforce = curBeam.getDX() / curBeam.getHYP();
							yforce = curBeam.getDY() / curBeam.getHYP();
							////System.out.pritln("Beam:" + curBeam.getName());
							//System.out.printf("Ans X:%.2f Y:%.2f\n", xforce, yforce);
							answers[0] -= xforce * curBeam.getForce();
							answers[1] -= yforce * curBeam.getForce();
						}
					}
					answers[1] -= node.getNodeForce();
					////System.out.pritln(answers[1]);

					GaussElim test = new GaussElim(variables, answers);
					double[] ans = test.getValues();
					for(pos = 0; pos < beamPosition.size(); pos++)
					{
						Beam curBeam = beams.getValueAt(beamPosition.getValueAt(pos));
						curBeam.setForce(ans[pos]);
						//System.out.printf("Name: %s Force: %.2f\n", curBeam.getName(), ans[pos]);
					}
					stack.pop();
					break;

				default:
					//System.out.pritln("There's more then 2 missing forces, can't solve with this method");
			}
			whileTrace++;
		}
		//System.out.pritln(whileTrace);
		
		
		for(Nodes nodeJ:nodes)
			nodeJ.printNode();

		for(Beam beamJ:beamed)
			beamJ.printBeam();			
		
	}
}
