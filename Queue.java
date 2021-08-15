/** Queue abstract data type */
public class Queue<TS>
{
	/** List objects to hold our queue items.
    Use List operations to implement the methods below */
	private List<TS> list;
	private Stack<TS> stackR;
  
	/** This intializes the Queue class with a list and a stack */
	public Queue() 
	{
		// instantiate list here and the stack that reverses the queue
		list = new List<>();
		stackR = new Stack<>();

	}
  
	/** This adds a value to the end of a queue */
	public void enqueue(TS value) 
	{
		list.prepend(value);
	}
  
	/** This takes the front value of the queue and removes it */
	public TS dequeue() 
	{
		//checks if queue is empty to return either an error value, or the top of the queue
		if(!isEmpty())
		{
			TS value;
			
			value = list.getValueAt(list.size()-1);
			list.deleteAt(list.size()-1);
			
			return value;
		}
		else
		{
			return null;
		}
	}

	/** This looks at the front value of the queue without removing it */
	public TS front()
	{
		// declares and intializes the returned variable to an error value if the queue is empty
		TS value = null;
		
		// if the queue isn't empty return the value at the front of the queue
		if(!isEmpty())
			value = list.getValueAt(list.size()-1);
		
		return value;
	}

	/** This checks to see if the queue is empty */
	public boolean isEmpty() 
	{
		return list.size() == 0;
	}
	
	/** This reverses the 'map' of the game */
	public void reverse()
	{
		//go through queue until it is empty, emptying the queue into a stackR
		while(!isEmpty())
			stackR.push(dequeue());
		
		//go through the stack until it is empty, emptying the stack into the queue
		while(!stackR.isEmpty())
			enqueue(stackR.pop());
		
	}
}
