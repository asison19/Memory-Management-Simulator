package memoryManagementSimulator;
import java.util.ArrayList;

public abstract class Memory {
	
	/* 
	 * boolean array to keep track of which "bytes" are occupied by a process
	 * true meaning the space is taken
	 * false meaning the space is not taken
	 */
	protected boolean[] memory;
	
	/*
	 * contains the processes in memory that are running
	 */
	protected ArrayList<Process> lookupTable = new ArrayList<>();
	
	/*
	 * contains the processes that are waiting to be given space to run
	 */
	protected ArrayList<Process> waitingProcesses = new ArrayList<>();
	
	protected int processAmount;
	protected int finishedProcessAmount;
	//private int time = 0; // "clock" of the time the process takes in memory
	
	public Memory(int memorySize, int processAmount){
		memory = new boolean[memorySize];
		this.processAmount = processAmount;
	}
	
	// remove all data of the process from memory
	protected void removeProcess(int index) {
		// for however many pages there are in the process
		for(int i = 0 ; i < lookupTable.get(index).getPageAmount(); i++) {
			// for however long that page is
			for(int j = 0; j < lookupTable.get(index).getPageAt(i).spaceAmount; j++) {
				removeData(lookupTable.get(index).getPageAt(i).startIndex, 
						lookupTable.get(index).getPageAt(i).endIndex);
			}
		}
	}
	
	// remove data from start to last
	protected void removeData(int start, int last) {
		for(int i = start; i <= last; i++)
			memory[i] = false;
	}
	
	// add data from start to last
	protected void addData(int start, int last) {
		for(int i = start; i <= last; i++)
			memory[i] = true;
	}
	
	public void outputMemoryMap() {
		System.out.println("Memory Map:");
		for(Process proc: lookupTable) {
			for(int i = 0;i < proc.getPageAmount(); i++) {
				System.out.println("\t" + proc.getPageAt(i).startIndex + "-"
			+ proc.getPageAt(i).endIndex + ": Process " + proc.getId());
			}
		}
	}
	
	public int getProcessAmount() {
		return processAmount;
	}
	
	public abstract void addProcess(Process proc);
	public abstract boolean removeProcess(Process proc, int time);
	
}
