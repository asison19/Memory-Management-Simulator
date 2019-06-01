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
	protected int fitAlgorithm;
	
	
	public Memory(int memorySize, int processAmount, int fitAlgorithm){
		memory = new boolean[memorySize];
		this.processAmount = processAmount;
		this.fitAlgorithm = fitAlgorithm;
		
		findHoles();
	}
	
	// remove all data of the process from memory
	protected void removeProcess(int index) {
		// for however many pages there are in the process
		for(int i = 0 ; i < lookupTable.get(index).getSegmentAmount(); i++) {
			// for however long that page is, remove the data
			removeData(lookupTable.get(index).getSegmentAt(i).startIndex, 
					lookupTable.get(index).getSegmentAt(i).endIndex);
			
		}
		// proc.resetNextPage(); // Not used in VSP,
	}
	
	// TODO remove this if unused anywhere
	protected void removeProcess(Process proc) {
		// for however many pages there are in the process
		for(int i = 0 ; i < proc.getSegmentAmount(); i++) {
			// for however long that page is, remove the data
			// TODO can cause unrealistic cases for indexes not yet set if a process wants to go to 
			// index 0 with total space of 1
			removeData(proc.getSegmentAt(i).startIndex, proc.getSegmentAt(i).endIndex);
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
	protected void outputMemoryMap() {
		System.out.println("Memory Map:");
		
		// output the processes that are currently running
		outputProcesses();
		
		findHoles();
		// output the holes
		for(Hole hole: holes)
			System.out.println("\t" + hole.getStartIndex() 
			+ "-" + hole.getEndIndex() + ": Hole.");
		
		
		// holes.removeAll(holes);
		System.out.println();
	}
	
	protected int getProcessAmount() {
		return processAmount;
	}
	
	protected void findHoles() {
		holes.removeAll(holes);
		
		// Output the amount of free space, the holes. Does not include internal fragmentation
		for(int i = 0; i < memory.length; i++) {
			int start = i;
			// if we find a empty space, look to see how much is in a row
			while(i < memory.length && !memory[i]) {
				i++;
				if(i == (memory.length - 1)) { // if we are at the end of the list, add the hole
					holes.add(new Hole(start, i));
				} else if(i < memory.length && memory[i]) { // or if we find a true boolean
					holes.add(new Hole(start, i - 1));
					//System.out.println("\t" + start + "-" + i + ": Hole.");
				}
			}
		}
	}
	
	// add the process into the waiting list and start the simulation once all processes are added
	protected void addProcess(Process proc) {
		waitingProcesses.add(proc);
		
		/*
		 * if all the processes have been added to the waiting list
		 * it's time to attempt to add them to memory at their start time
		 * if they can't get in, they need to wait still
		 */
		
		// once we add the last process, start the simulation
		if(processAmount == proc.getId()) {
			System.out.println("Starting Simulation.");
			startSimulation();
		}
	}
	// output the processes that are currently running
	protected abstract void outputProcesses(); 
	protected abstract void startSimulation();
	
}
