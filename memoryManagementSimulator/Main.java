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
				if(memoryManagementPolicy == 1)
					memoryManagementVSP();
				else if(memoryManagementPolicy == 2)
					memoryManagementVSP();
				else if(memoryManagementPolicy == 3)
					memoryManagementSegmentation();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void getUserInput() {
		Scanner in = new Scanner(System.in);
		
		System.out.print("Memory Size: ");
		memorySize = in.nextInt();
		
		System.out.print("Memory management policy (1- VSP, 2- PAG, 3- SEG): ");
		memoryManagementPolicy = in.nextInt();
		
		System.out.print("Fit algorithm (1- first-fit, 2- best-fit, 3- worst-fit): ");
		fitAlgorithm = in.nextInt();
		
		System.out.print("File name of the Input: ");
		fileName = in.next();
		
	}
	
	public static void memoryManagementVSP() throws FileNotFoundException {
		VariableSizePartitioning VSP;// = new VariableSizePartitioning(memorySize, );
		int processAmount;
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		
		//first line of the file should always be the number of processes
		processAmount = in.nextInt();
		
		VSP = new VariableSizePartitioning(memorySize, processAmount, fitAlgorithm);
		
		/*
		 * Next numbers should be a set
		 * First is the ID
		 * Second is the arrival time, and completion time
		 * Last is the page amount and the memory amount of each page
		 * 		since this is VSP page amount should be 1
		 * This continues on for however many processes there are
		 */
		for(int i = 0; i < processAmount; i++) {
			int id = in.nextInt();
			int arrivalTime = in.nextInt();
			int endTime = in.nextInt();
			int pageAmount = in.nextInt(); // this should be 1
			int[] spaceAmount = new int[pageAmount];
			spaceAmount[0] = in.nextInt();
			
			VSP.addProcess(new Process(id, arrivalTime, endTime, pageAmount, spaceAmount));
		}
	}
	
	public static void memoryManagementSegmentation() throws FileNotFoundException {
		Segmentation seg; 
		int processAmount;
		File inputFile = new File(fileName);
		Scanner in = new Scanner(inputFile);
		
		//first line of the file should always be the number of processes
		processAmount = in.nextInt();
		
		seg = new Segmentation(memorySize, processAmount, fitAlgorithm);
		
		/*
		 * Next numbers should be a set
		 * First is the ID
		 * Second is the arrival time, and completion time
		 * Last is the page amount and the memory amount of each page
		 * 		since this is VSP page amount should be 1
		 * This continues on for however many processes there are
		 */
		for(int i = 0; i < processAmount; i++) {
			int id = in.nextInt();
			int arrivalTime = in.nextInt();
			int endTime = in.nextInt();
			int pageAmount = in.nextInt(); // this should be greater than 1
			int[] spaceAmount = new int[pageAmount];
			for(int j = 0; j < pageAmount; j++) {
				spaceAmount[j] = in.nextInt();
			}
			
			seg.addProcess(new Process(id, arrivalTime, endTime, pageAmount, spaceAmount));
		}
	}
}
