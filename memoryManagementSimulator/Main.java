package memoryManagementSimulator;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
public class Main {
	
	private static int memorySize;
	private static int memoryManagementPolicy;
	private static int fitAlgorithm;
	private static int pageSize;
	private static String fileName;

	public static void main(String[] args) {
		getUserInput();
		
		try {
			memoryManagement();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void getUserInput() {
		Scanner in = new Scanner(System.in);
		
		System.out.print("Memory Size: ");
		memorySize = in.nextInt();
		
		System.out.print("Memory management policy (1- VSP, 2- PAG, 3- SEG): ");
		memoryManagementPolicy = in.nextInt();
		
		if(memoryManagementPolicy == 2) {
			System.out.print("Page Size: ");
			while(!getPageSize(in))
				System.out.print("Page Size not viable. \nPage Size:");
		} else {
			System.out.print("Fit algorithm (1- first-fit, 2- best-fit, 3- worst-fit): ");
			fitAlgorithm = in.nextInt();
		}
		
		System.out.print("File name of the Input: ");
		fileName = in.next();
		
	}
	
	public static boolean getPageSize(Scanner in) {
		pageSize = in.nextInt();
		if(memorySize % pageSize == 0)
			return true;
		else 
			return false;
	}
	
	public static void memoryManagement() throws FileNotFoundException {
		Memory memory; 
		int processAmount;
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		
		//first line of the file should always be the number of processes
		processAmount = in.nextInt();
		
		switch(memoryManagementPolicy) {
		case 1:
			memory = new VariableSizePartitioning(memorySize, processAmount, fitAlgorithm);
			break;
		case 2:
			memory = new Paging(memorySize, processAmount, pageSize);
			break;
		case 3:
			memory = new Segmentation(memorySize, processAmount, fitAlgorithm);
			break;
		default:
			System.out.println("Proper memory management policy not found. Defaulting to VSP.");
			memory = new VariableSizePartitioning(memorySize, processAmount, fitAlgorithm);
			break;
		}
		
		/*
		 * Next numbers should be a set
		 * First is the ID
		 * Second is the arrival time, and completion time
		 * Last is the page amount and the memory amount of each page
		 * This continues on for however many processes there are
		 */
		for(int i = 0; i < processAmount; i++) {
			int id = in.nextInt();
			int arrivalTime = in.nextInt();
			int endTime = in.nextInt();
			int pageAmount = in.nextInt(); // this should be 1 or greater than 1
			int[] spaceAmount = new int[pageAmount];
			for(int j = 0; j < pageAmount; j++) {
				spaceAmount[j] = in.nextInt();
			}
			
			memory.addProcess(new Process(id, arrivalTime, endTime, pageAmount, spaceAmount));
		}
	}
	
	
}
