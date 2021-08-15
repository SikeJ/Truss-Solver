/** Stack abstract data type */
public class Stack<TE>
{
	/** List objects to hold our stack items.
    Use List operations to implement the methods below */
	private List<TE> list;

	/** This intializes the stack class with a list in order to keep track of the stack */
	public Stack() 
	{
		// instantiate list here
		list = new List<TE>();
	}
	
	/** This adds a value to the top of the stack */
	public void push(TE value) 
	{
		/*since adding a value to a stack is the same with an empty stack or a filled stack
		*  you don't have to account for anything in this push function because everything
		*  is already handled in the list functions */
		list.append(value);	
	}

	/** This returns the value at the top of the stack and removes it */
	public TE pop() 
	{
		TE value = null;
				
		// if the stack is empty, you can't pop anything from it so you return a error value
		if(!isEmpty())
		{
			// sets the variable that will be returned to the top of the stack
			value = list.getValueAt(list.size()-1);
			
			//removes the value at the top of the stack
			list.deleteAt(list.size()-1);
		}
		//list.printBackwards();
		//System.out.println("V: " +value);
		//returns either an error value (null) or the top of the stack
		return value;
	}
	
	/** This looks at the top value of the stack without removing it */
	public TE peek() 
	{
		//intializes the returned variable to an error
		TE value = null; 
		
		//if the stack isn't empty set the returned variable to the top of the stack
		if(!isEmpty())
			value = list.getValueAt(list.size()-1);
		
		//returns an error or the top value of the stack
		return value;
	}

	/** Checks to see if the stack is empty */
	public boolean isEmpty() 
	{
		return list.size() == 0;
	}
}
