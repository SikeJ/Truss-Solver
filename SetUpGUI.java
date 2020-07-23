import javax.swing.*;
import java.awt.Dimension;
import java.awt.BorderLayout;
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
	
	private int addNodeButtonHeightL = 100;
	
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
		
		button.setBounds(WIDTH * 3/4 + 20, addNodeButtonHeightL, 20, 15);
		
		//adds the different panels & tabs onto the frame
		panel1.add(button, BorderLayout.CENTER);
		panel2.add(textfield, BorderLayout.CENTER);
		panel3.add(textarea, BorderLayout.CENTER);
		panel4.add(label, BorderLayout.CENTER);
		panel5.add(button2, BorderLayout.CENTER);
		
		//creates the tabs
		tabs.addTab("Nodes", null, panel1, TABNODE);
		tabs.addTab("Connections", null, panel2, TABCONNECTIONS);
		tabs.addTab("Forces", null, panel3, TABFORCE);
		tabs.addTab("Factor of Safety", null, panel4, TABSAFETY);
		
		tabs.setPreferredSize(new Dimension(WIDTH * 1/4, HEIGHT));
		panel5.setPreferredSize(new Dimension(WIDTH * 3/4, HEIGHT));
		tabs.setLocation(WIDTH/2, 0);
		add(tabs, BorderLayout.EAST);
		add(panel5, BorderLayout.WEST);
		setVisible(true);
	}
	
	/** This sets up the tab in the GUI in order to be able to add more nodes or edit the current nodes */
	private void NodeTab()
	{
		JLabel label2 = new JLabel("Node:\t Coord:");
		
		label2.setBounds(WIDTH * 3/4 + 15, 100, 80, 50);
		
		addNodeButtonHeightL += 30;
		
		//button.setBounds(WIDTH * 3/4 + 20, addNodeButtonHeightL, 20, 15);
		
		panel1.add(label2);
		//panel1.add(button);
		
		pack();
	}
	
	/** This sets up the tab to make the connections between each tab */
	private void ConnectionsTab()
	{
		
	}
	
	/** This sets up the tab to show the forces in each member, and to add a force onto the truss */
	private void ForcesTab()
	{
		
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
