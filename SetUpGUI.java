import javax.swing.*;
import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;

class SetUpGUI extends JFrame implements ActionListener
{
	//private variables
	final private int WIDTH = 1200;
	final private int HEIGHT = 600;
	final public String TABNODE = "Create and move nodes around in this tab.";
	final public String TABFORCE = "Calculate the forces in the members joining the nodes here.";
	final public String TABCONNECTIONS = "Connect the correct nodes to one another here.";
	final public String TABSAFETY = "Look at the factor of safety of every member.";
	
	private int numclicks;
	
	private int addedNodeH = 100;
	
	//creates new objects of the java swing class to make the buttons and whatnot
	//creates the panels for each tab used in the GUI
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JPanel panel3 = new JPanel();
	JPanel panel4 = new JPanel();
	JPanel panel5 = new JPanel();
	
	//creates the buttons/textfields
	JButton button = new JButton("Add Node");
	JButton button2 = new JButton("Testing");
	JTextField textfield = new JTextField("Hello, how are you today?");
	JTextArea textarea = new JTextArea("How\tare\tyou\t?");
	
	//sets up the constraint object for the panels
	GridBagConstraints constraints = new GridBagConstraints();
	
	private char [] letters = {'a','b','c','d','e','f','g'};
	
	
	//creates the labels
	JLabel label = new JLabel("This is my label");
	
	//creates the tabs
	JTabbedPane tabs = new JTabbedPane();	
	
	//constructor
	public SetUpGUI()
	{
		//sets the frame of the GUI up
		super("Truss Solver");
		
		numclicks = 0;
		
		//sets the defaults of the GUI
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//adds an action listener to the add node button
		button.addActionListener(this);
		button.setActionCommand("AddNode");
		button2.addActionListener(this);
		button2.setActionCommand("Test");
		
		//sets the layout of the different panel & tabs
		panel1.setLayout(new GridBagLayout());
		panel2.setLayout(new GridBagLayout());
		panel3.setLayout(new GridBagLayout());
		panel4.setLayout(new GridBagLayout());
		
		
		
		//changes and sets the grid constraints for each button/textfield/textarea
		//for the AddNode button
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 4;
		constraints.anchor = GridBagConstraints.NORTH;
		
		panel1.add(button, constraints);
		
		//for the second panel
		
		panel2.add(textfield, constraints);
		
		//for the third
		
		panel3.add(textarea, constraints);
		
		//the fourth
		
		panel4.add(label, constraints);
		
		//the fifth
		
		panel5.add(button2, constraints);
		
		//creates the tabs and adds the panels onto them
		tabs.addTab("Nodes", null, panel1, TABNODE);
		tabs.addTab("Connections", null, panel2, TABCONNECTIONS);
		tabs.addTab("Forces", null, panel3, TABFORCE);
		tabs.addTab("Factor of Safety", null, panel4, TABSAFETY);
		
		tabs.setPreferredSize(new Dimension(WIDTH * 1/4, HEIGHT));
		panel5.setPreferredSize(new Dimension(WIDTH * 3/4, HEIGHT));
		tabs.setLocation(WIDTH *3/4, 0);
		add(tabs, BorderLayout.EAST);
		add(panel5, BorderLayout.WEST);
		setVisible(true);
	}
	
	/** This sets up the tab in the GUI in order to be able to add more nodes or edit the current nodes */
	private void NodeTab()
	{
		Nodes newnode = new Nodes(numclicks);
		
		JLabel label2 = new JLabel("Node " + newnode.getName());
		JLabel label3 = new JLabel("\tCoord:");
		JTextField text2 = new JTextField("Enter here:");
		
		constraints.gridy += 1;
		panel1.add(label2, constraints);
		//constraints.gridx += 1;
		//panel1.add(label3, constraints);
		
		//panel1.add(text2, constraints.RELATIVE);
		
		
		numclicks++;
		pack();
	}
	
	/** This sets up the tab to make the connections between each tab */
	private void ConnectionsTab()
	{
		
	}
	
	/** This sets up the tab to show the forces in each member, and to add a force onto the truss */
	private void ForcesTab()
	{
		System.out.println("How does this work?" + numclicks);
	}
	
	/** This sets up the tab to look at the factor of safety in each member */
	private void SafetyTab()
	{
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand() == "AddNode")
			NodeTab();
		
		else if(e.getActionCommand() == "Test")
			ForcesTab();
		
		else
			System.out.print("stupid");
	}
}
