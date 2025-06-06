public class GaussElim
{
	private double[] xyz;
	
	public GaussElim(double[][] coeffs, double[] ans)
	{
		this.xyz = GaussPivot(coeffs, ans);
	}
	
	/** This returns the values of the variables that were calculated */
	public double[] getValues()
	{
		return this.xyz;
	}
	
	
	/** This solves a system of equations using Gauss with partial pivoting */
	private static double[] GaussPivot(double[][] x, double[] ans)
	{
		/*
		This solves a system of equations that are formatted in the form:
		
		a*x + b*y + c*z = D
		e*x + f*y + g*z = H 
		i*x + j*y + k*z = L
		
		where coeffs is an array of array's of the coeffs of x, y, and z
		and answers is a column vector of F,G,H 
		*/
		double[] xyz = new double[ans.length];
		int Ldown = x.length;
		int Lacross = x[0].length;
		double max;
		int maxI;
		
		//Loops through the system of equations in order to pivot where necessary, and 
		// will solve the equations once the last equation has been reduced to z = H/j 
		for(int i = 0; i < Lacross - 1; i++)
		{
			max = x[i][i];
			maxI = i;
			
			for(int k = i; k < Ldown; k++)
			{
				if(Math.abs(max) < Math.abs(x[k][i]))
					maxI = k;
			}
			
			//PrintArray(x, ans);

			if(maxI != i)
			{
				double[] temp = x[maxI];
				x[maxI] = x[i];
				x[i] = temp;
				double tempa = ans[maxI];
				ans[maxI] = ans[i];
				ans[i] = tempa;
			}
			
			//PrintArray(x,ans);
			
			//loops through the equations in order to reduce them
			for(int m = i + 1; m < Ldown; m++)
			{
				double factor = x[m][i]/x[i][i];
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
			double sub = ans[b];
			for(int y = Lacross - 1; y > b; y--)
				sub -= x[b][y]*xyz[y];
			
			xyz[b] = sub/x[b][b];
		}
		
		return xyz;
	}
	
	
	
	/** Prints out the arrays for testing purposes */
	private static void PrintArray(double[][] x, double[] ans)
	{
		int Ldown = x.length;
		int Lacross = x[0].length;
		System.out.println();
		for(int j = 0; j < Ldown; j++)
		{
			for(int l = 0; l < Lacross; l++)
			{
				System.out.printf("%.2f ", x[j][l]);
			}
			System.out.printf("%.2f\n", ans[j]);
		}
	}
}