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
	
	/*
	 * Contains the holes in memory
	 */
	protected ArrayList<Hole> holes = new ArrayList<>();
	
	protected int processAmount;
	protected int finishedProcessAmount;
	
	
	public Memory(int memorySize, int processAmount){
		memory = new boolean[memorySize];
		this.processAmount = processAmount;
	}
	
	// remove all data of the process from memory
	protected void removeProcess(int index) {
		// for however many pages there are in the process
		for(int i = 0 ; i < lookupTable.get(index).getPageAmount(); i++) {
			// for however long that page is, remove the data
			removeData(lookupTable.get(index).getPageAt(i).startIndex, 
					lookupTable.get(index).getPageAt(i).endIndex);
			
		}
		// proc.resetNextPage(); // Not used in VSP,
	}
	protected void removeProcess(Process proc) {
		// for however many pages there are in the process
		for(int i = 0 ; i < proc.getPageAmount(); i++) {
			// for however long that page is, remove the data
			// TODO can cause unrealistic cases for indexes not yet set if a process wants to go to 
			// index 0 with total space of 1
			removeData(proc.getPageAt(i).startIndex, proc.getPageAt(i).endIndex);
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
	
	
	// TODO redo? Make it so that it goes by order of the memory bits and not process number.
	public void outputMemoryMap() {
		System.out.println("Memory Map:");
		
		// output the processes that are currently running
		outputProcesses();
		
		// Output the amount of free space, the holes. Does not include internal fragmentation
		for(int i = 0; i < memory.length; i++) {
			int start = i;
			
			// if we find a empty space, look to see how much is in a row
			while(i < memory.length && !memory[i]) {
				i++;
				if(i == (memory.length - 1) || (i < memory.length && memory[i])) {
					holes.add(new Hole(start, i));
					System.out.println("\t" + start + "-" + i + ": Hole.");
				}
			}
		}
		holes.removeAll(holes);
		System.out.println();
	}
	
	public int getProcessAmount() {
		return processAmount;
	}
	
	public void removeHole(Hole hole) {
		
	}
	
	// add the process into the waiting list and start the simulation once all processes are added
	public abstract void addProcess(Process proc);
	// output the processes that are currently running
	public abstract void outputProcesses(); 
	
}
