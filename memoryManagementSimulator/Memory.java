package memoryManagementSimulator;
import java.util.ArrayList;

public abstract class Memory {
	
	/* 
	 * boolean array to keep track of which "bytes" are occupied by a process
	 * false meaning the space is taken
	 * true meaning the space is not taken
	 */
	private boolean[] memory;
	
	/*
	 * contains the processes in memory
	 */
	private ArrayList<Process> lookupTable = new ArrayList<>();
	private int processAmount;
	private int finishedProcessAmount;
	
	public Memory(int memorySize, int processAmount){
		memory = new boolean[memorySize];
	}
	
	// remove data from start to last
	public void removeData(int start, int last) {
		for(int i = start; i < last; i++)
			memory[i] = false;
	}
	
	// add data from start to last
	public void addData(int start, int last) {
		for(int i = start; i < last; i++)
			memory[i] = false;
	}
	
	public void addProcess(Process proc) {
		lookupTable.add(proc);
	}
	
	// TODO pass in as index?
	public boolean removeProcess(Process proc, int time) {
		lookupTable.remove(proc);
		
		// output the process completing
		System.out.println("Time: " + time + "\nProcess " + proc.getId() + 
				" has completed");
		
		// check if we're finished
		if(finishedProcessAmount == processAmount)
			return true;
		else
			return false;
		
	}
	
	public void outputMemoryMap() {
		
	}
	
}
