class Main
{
	public static void main(String [] args)
	{
		SetUpGUI gui = new SetUpGUI();
		
		Nodes node1 = new Nodes(0);
		Nodes node2 = new Nodes(1);
		
		Members member = new Members(node1, node2);

		
		float[][] x = {{0, 21}, {1, 1}};
		float[] ans = {239, 73};
		
		System.out.println(member.getName());
		System.out.println(member.getDX());
		System.out.println(member.getDY());
		System.out.println(member.getHYP());
		System.out.println(member.getForce());
		System.out.println(member.getNodes()[1].getName());
		
		GaussElim maybe = new GaussElim(x,ans);
		
		float[] test = maybe.getValues();
		
		for(int i = 0; i < test.length; i++)
			System.out.println(test[i]);	
		
	}
}
