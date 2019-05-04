package memoryManagementSimulator;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
	
	private static int memorySize;
	private static int memoryManagementPolicy;
	private static int fitAlgorithm;
	private static int pageSize;
	private static String fileName;

	public static void main(String[] args) {
		getUserInput();
		
		if(memoryManagementPolicy == 1)
			memoryManagementVSP();
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
	
	public static void memoryManagementVSP() {
		VariableSizePartitioning VSP;// = new VariableSizePartitioning(memorySize, );
		int processAmount;
		FileReader fr;
		try {
			fr = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
