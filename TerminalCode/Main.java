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
	
	/** Start point of the entire program */
	public static void main(String[] args)
	{
		// Runs the program through the terminal
		terminalTruss();

		// Runs the program through the GUI
		// guiTruss();
	}

	/** Function to create and solve a Truss through the terminal */
	public static void terminalTruss()
	{
		// List out the initial test string for the 
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
				System.out.println("You must've typed something incorrectly");
		}

	}

	/** Builds a new Truss to solve in the program */
	public static void buildTruss()
	{
		optionText = """
			To build a truss please select the below options:
			[0] Add a new Node
			[1] Add a new Beam
			[2] Add a new Force
			""";
		System.out.print(optionText);
	}

	/** Loads a truss that was saved to a file */
	public static void loadTruss()
	{
		System.out.println("How'd I get here?");	
	}
}
