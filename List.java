/** Linked List implementation of our List abstract data type */
public class List<L> {
  // put all fields from ListAsLinkedList class here
  // sets the instance variables for the LinkedList class
	private Link<L> tail;
	private Link<L> head;
	private int size;
	private Link<L> temp;
  
  // put all methods from ListAsLinkedList class here
  /** This initializes the instance variables */
	public List()
	{
		temp = head = tail = null;
		size = 0;
	}

	/** Creates a List with a singular value in it */
	public List(L item)
	{
		head = tail = new Link<L>(item);
		size = 1;
	}
	
	/** Adds the given value to the end of the list */
	public void append(L value)
	{
		// creates a temp value to add a char to the end of the linked list
		temp = new Link<L>(value);
		
		//Case 0 the head and tail are the same thing
		if (head == null)
			head = tail = temp; 
		
		//Case 1+ creates a temp variable to move the tail of the linked list 
		//	and sets the prev/next to make it a doubly linked list
		else
		{
			tail.setNext(temp);
			tail = temp;			
		}
		
		// increments size because something was added
		size++;
	}
	
	/** Adds the given value to the beginning of the list */
	public void prepend(L value)
	{
		// temp variable to swap the linked list head/tail 
		temp = new Link<L>(value);
		
		//Case 0 everything is the same
		if (head == null)
			head = tail = temp;			
		
		//Case 1+ uses the temp variable to swap the head value to the added value
		//	sets the next and prev values properly so the linked list is doubly linked
		else
		{ 
			Link<L> temp2 = head;
			head = temp;
			
			head.setNext(temp2);
		}
		
		// increments size because something was added
		size++;
	}
	
	/** Deletes the container at the given position (a container holds a value) */
	public void deleteAt(int position)
	{
		// Starting point to search through the linked list
		temp = head;
		
		// if the position is the head, set the head to the next item and set the prev to null and decrement the size
		if (position == 0)
		{
			head = head.getNext();
			size--;
		}
		
		// Deleting will be different for the head, tail, and body
		// If the position is valid and not the head
		else if( position > 0 && position <= size)
		{
			//starts a loop to go through the linked list to get to the position
			for(int i = 1; i < position; i++)
				temp = temp.getNext();
			
			// if the position is the same as the size  set the new tail
			if(position + 1 == size)
			{
				tail = temp;
				tail.setNext(null);
			}
			
			// if the position is in the middle use temp values to delete the desired position
			else
				temp.setNext(temp.getNext().getNext());

			// decrement the size since something is being deleted
			size--;
		}
		
			
	}
	
	/** Returns the number of values currently in our list */
	public int size()
	{
		return this.size;
	}
	
	/** Retrieves the value at the given position (0-based) */
	public L getValueAt(int position)
	{
		// starts at the head of the linked list and goes through the list until it is at the position
		temp = head;
		
		//System.out.println(position);
		//printBackwards();
		
		for(int i = 0; i < position; i++)
			temp = temp.getNext(); 
		
		return temp.getData();
	}

	/** Searches for the FIRST occurence of a given value in our list.
		* If found, it returns the position of that value.
		* If not found, it returns -1 */
	public int positionOf(L value)
	{
		// sets a value if the position is not found
		int found = -1;
		
		// starts at the head and goes through the list to find the first instance of a character
		// it returns the position of that character
		temp = head;		
		for(int i = 0; i < size; i++)
		{
			if(temp.getData() == value)
			{
				found = i;
				break;
			}
			temp = temp.getNext();
		}
		
		return found;
	}
	
	public void printBackwards() 
	{
		// Links used to traverse the linked list from head to tail
		Link<L> cur = head; // called "cur" to stand for the "current" Links I am printing
    
		// as long as cur is not null, keep traversing
		while (cur != null)
		{
			//System.out.print(cur.getData());
			cur = cur.getNext();
		}
		System.out.println();
	}
  
}

/** A linked list Link for our linked list */
class Link<N> {
  // put all fields from Link class here
  // initializes the instance variables for the Link class
	private N data;
	private Link<N> next;
  
  // put all methods from Link class here
  /** This sets the initialized variables to the inputted values */
	public Link(N data)
	{
		this.data = data;
		this.next = null;
	}
	
	/** This returns the data held by an instance */
	public N getData()
	{
		return data;
	}
	
	/** This allows the data to be set from different classes */
	public void setData(N data)
	{
		this.data = data;
	}
	
	/** This returns the next list instance */
	public Link<N> getNext()
	{
		return next;
	}
	
	/** This allows the next list instance to be set in different classes */
	public void setNext(Link<N> next)
	{
		this.next = next;
	}
  
}
