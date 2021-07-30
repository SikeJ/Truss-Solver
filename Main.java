class Main
{
		public static void main(String [] args)
	{
		Nodes node1 = new Nodes('A', 0, 0);
		Nodes node2 = new Nodes('B', 10, 0);
		Nodes node3 = new Nodes('C', 5, 5);
		
		Beam beam = new Beam(node1, node2);
		Beam beam2 = new Beam(node1, node3);
		Beam beam3 = new Beam(node2, node3);
		Force force = new Force(node3, 100.0, "lbf");

		Beam[] beams = {beam, beam2, beam3};


		/* for(int i = 0; i < beams.length; i++)
		{
			System.out.println(beams[i].getName());
			System.out.println("DX: " + beams[i].getDX());
			System.out.println("DY: " + beams[i].getDY());
			System.out.println("L: " + beams[i].getHYP());
			System.out.println("F: " + beams[i].getForce());
			System.out.println();
		}
		 */
	}
}
