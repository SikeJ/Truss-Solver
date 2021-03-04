class Testing
{
	public static void main(String[] args)
	{
		//int[][] x = new int[3][3];
		//int[] ans = new int[x.length];
		
		float[][] x = {{0, 21}, {1, 1}};
		float[] ans = {10000, 1000};
		
		float[] maybe = GaussElim(x,ans);
		
		for(int i = 0; i < maybe.length; i++)
			System.out.println(maybe[i]);		
		
	}
	
	/** This solves a system of equations using Gauss with partial pivoting */
	private static float[] GaussElim(float[][] x, float[] ans)
	{
		/*
		This solves a system of equations that are formatted in the form:
		
		a*x + b*y + c*z = F
		d*x + e*y + f*z = G 
		h*x + i*y + j*z = H
		
		where coeffs is an array of array's of the coeffs of x, y, and z
		and answers is a column vector of F,G,H 
		*/
		float[] xyz = new float[ans.length];
		int Ldown = x.length;
		int Lacross = x[0].length;
		float max;
		int maxI;
		
		//Loops through the system of equations in order to pivot where necessary, and 
		// will solve the equations once the last equation has been reduced to z = H/j 
		for(int i = 0; i < Lacross - 1; i++)
		{
			max = x[i][i];
			maxI = i;
			for(int k = i; k < Ldown; k++)
			{
				if(max < Math.abs(x[k][i]))
					maxI = k;
			}
			
			if(maxI != i)
			{
				float[] temp = x[maxI];
				x[maxI] = x[i];
				x[i] = temp;
				float tempa = ans[maxI];
				ans[maxI] = ans[i];
				ans[i] = tempa;
			}
			
			//PrintArray(x,ans);
			
			//loops through the equations in order to reduce them
			for(int m = i + 1; m < Ldown; m++)
			{
				float factor = x[m][i]/x[i][i];
				//System.out.println(factor);
				
				for(int n = 0; n < Lacross; n++)
					x[m][n] -= factor*x[i][n];
					
				ans[m] -= factor*ans[i];
			}
			
			//PrintArray(x,ans);
			
		}
		
		//Goes through the reduced equations and backsolves for the variables xyz
		for(int b = Lacross - 1; b >= 0; b--)
		{
			float sub = ans[b];
			for(int y = Lacross - 1; y > b; y--)
				sub -= x[b][y]*xyz[y];
			
			xyz[b] = sub/x[b][b];
		}
		
		return xyz;
	}
	
	
	
	/** Prints out the arrays for testing purposes */
	private static void PrintArray(float[][] x, float[] ans)
	{
		int Ldown = x.length;
		int Lacross = x[0].length;
		System.out.println();
		for(int j = 0; j < Ldown; j++)
		{
			for(int l = 0; l < Lacross; l++)
			{
				System.out.print(x[j][l] + " ");
			}
			System.out.println(ans[j]);
		}
	}
}













































