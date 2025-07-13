/*
 * Created By: Jackson Sikes
 * Description: A program that solves for the force of a defined statically
 * 	determinate truss. It can suggest a particular style of member to 
 * 	be used to actually make the truss, based on the expected failure
 * 	modes (tension, compression, tearout, shear, etc.).
 */

import java.io.*;
import java.util.*;

// Setting up the main class for the project
public class Main
{
	// Iniitalizing any variables to use throughout the entire program
	static String optionText;
	static Scanner choice = new Scanner(System.in);

	// Setups the Dictionary to keep track of the Nodes of the Truss
	static Dictionary<Integer, Nodes> trussNodes = new Hashtable<Integer, Nodes>();
	static int nodeName = 0;
	
	/** Start point of the entire program */
	public static void main(String[] args)
	{
		// Clears the screen to prep for the program
		clearScreen();

		// Runs the program through the terminal
		terminalTruss();

		// Runs the program through the GUI
		// guiTruss();
	}

	/** Function to create and solve a Truss through the terminal */
	public static void terminalTruss()
	{
		// List out the initial test string for the 
		clearScreen();
		optionText = """
			Welcome to the Truss Solver!
			Please select one of the options below:
			[0] Create a brand new Truss
			[1] Load a previously saved Truss
			""";
		System.out.print(optionText);

		// Get the users choice of they want the program to do
		String optionChosen = choice.nextLine();

		// Filter out the option that the user chose to run
		switch(optionChosen)
		{
			case "0":
				System.out.println("Great choice! Time to get buildin");
				buildTruss();
				break;

			case "1":
				System.out.println("Oooohhhh special, let's pick what file!");
				loadTruss();
				break;

			case "2":
				System.out.println("Boooooooo. I don't like you either");
				break;

			default:
				// Setups an error message if the user puts in a wrong option
				String errorMessage = """
					******************************
					\'""" + optionChosen + """
					\' is not a valid selection...
					Please press enter and re-select what you would like to do
					******************************
					""";
				System.out.print(errorMessage);
				choice.nextLine();
				terminalTruss();
		}

	}

	/** Builds a new Truss to solve in the program */
	public static void buildTruss()
	{
		// Initialize the options to build the truss
		optionText = """
			To build a truss please select the below options:
			[0] Add a new Node
			[1] Add a new Beam
			[2] Add a new Force
			[3] Finish building the truss
			""";
		
		// Clear the screen and print the options for the user
		clearScreen();
		printTruss();
		System.out.print(optionText);

		// Get the users choice of they want the program to do
		String optionChosen = choice.nextLine();

		// Filter out the option that the user chose to run
		switch(optionChosen)
		{
			case "0":
				newNode();
				System.out.println("Why isn't this working?????");
				break;
			
			case "1":
				newBeam();
				break;

			case "2":
				newForce();
				break;

			case "3":
				solveTruss();
				break;

			default:
				// Setups an error message if the user puts in a wrong option
				String errorMessage = """
					******************************
					\'""" + optionChosen + """
					\' is not a valid selection...
					Please press enter and re-select what you would like to do
					******************************
					""";
				System.out.print(errorMessage);
				choice.nextLine();
				buildTruss();
		}
		System.out.println("uh???");

	}

	/** Adds a Node to the Truss being built; User determines location */
	public static void newNode()
	{
		// Clear the output of the program and write out the truss the program already has
		clearScreen();
		printTruss();

		// Make the user input the two 'support' Nodes of the Truss
		while(trussNodes.size() < 2)
		{
			optionText = """
				It's time to decide the coordinates of the two supporting Nodes
				Please enter the X & Y coordinates for Node: """ + nodeName + """
				 below
				X: """;
			System.out.print(optionText);
			Double xcord = choice.nextDouble();

			optionText = "Y:";
			System.out.print(optionText);
			Double ycord = choice.nextDouble();

			trussNodes.put(nodeName, new Nodes(nodeName, xcord, ycord, true));
			nodeName++;
		}
		if(trussNodes.size() < 2)
			System.out.println("There is not a truss");

		else
			System.out.println("There is in fact a truss");

		// Output message to get 
		printTruss();

		// Re-loop through the options so they can chose to add more or stop
		//newNode();
	}

	/** Adds a Beam between two Nodes specified by the user */
	public static void newBeam()
	{
		System.out.println("Will also need to figure out a List/Dict to go through these");
	}

	/** Adds a Force to the user specified Node of the Truss */
	public static void newForce()
	{
		System.out.println("Would be easy if the Nodes were kept in a dict... Tht could make other problems tho");
	}

	/** Solves the truss for all of the internal forces */
	public static void solveTruss()
	{
		System.out.println("I'll add this eventually :)");
	}

	/** Loads a truss that was saved to a file */
	public static void loadTruss()
	{
		System.out.println("How'd I get here?");	
	}

	/** Prints out the truss to the terminal screen as best as possible */
	public static void printTruss()
	{
		System.out.println("There's nothing here for now... :)");
	}

	/** Clears either the terminal or eclipse console, depending which is in use */
	public static void clearScreen()
	{
		if(System.console() != null)
		{
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
		else
			for(int i = 0; i < 50; i++)
				System.out.println();
	}
}
