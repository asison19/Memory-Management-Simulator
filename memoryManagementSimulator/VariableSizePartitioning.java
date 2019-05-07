package memoryManagementSimulator;
import java.util.function.Predicate;

public class VariableSizePartitioning extends Memory{

	private int spaceAmount;
	private int fitAlgorithm;
	public VariableSizePartitioning(int memorySize, int processAmount, int fitAlgorithm) {
		super(memorySize, processAmount);
		this.fitAlgorithm = fitAlgorithm;
	}
	
	public void addProcess(Process proc) {
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
	
	/*
	 * TODO delete this?
	public boolean removeProcess(Process proc, int time) {
		lookupTable.remove(proc);
		
		// output the process completing
		System.out.println("Time: " + time + "\nProcess " + proc.getId() + " has completed");
		
		// check if we're finished
		if(finishedProcessAmount == processAmount)
			return true;
		else
			return false;
		
	}
	*/
	
	/*
	 * Searches from the beginning of memory to the end for any available spots
	 * Memory must be contiguous
	 * False if no spot available
	 */
	private boolean firstFit(Process proc) {
		int freeSpace = 0;
		// while we find a contiguous set of false booleans find out if it's the right fit
		for(int i = 0; i < memory.length; i++) {
			// if we find one free space, continue looking to see if it's contiguous
			int start = i;
			while(i < memory.length && !memory[i]) {
				freeSpace++;
				// if the space is enough, then put it in and return true
				if(freeSpace == proc.getSizeOfPageAt(0)) {
					proc.setIndexes(start, i);
					addData(start, i);
					return true;
				}
				i++;
			}
			freeSpace = 0;
		}
		return false; // if we get here, there is no viable location for the process
	}
	
	private boolean bestFit(Process proc) {
		return true; // TODO
	}
	
	private boolean worstFit(Process proc) {
		return true; // TODO
	}
	
	private void startSimulation() {
		int time = 0;
		Predicate<Process> condition = proc -> proc.isComplete(); // condition for when to remove processes from lookupTable
		
		// check if any processes have arrived
		for(int i = 0; i < waitingProcesses.size(); i++) {
			if(time == waitingProcesses.get(i).getStartTime())
				System.out.println("Process " + waitingProcesses.get(i).getId() + " has arrived.");
		}
		
		// continue running until all processes have started and completed
		while(!waitingProcesses.isEmpty() || !lookupTable.isEmpty()) {
			
			// check for complete processes 
			for(int i = 0; i < lookupTable.size(); i++){
				// check if any processes are complete
				if(lookupTable.get(i).isComplete()) {
					removeProcess(i);
					System.out.println("Process " + lookupTable.get(i).getId() + " has completed at time " + time + ".");
				}
			}
			
			// remove from list if process is complete
			if(lookupTable.removeIf(condition))
				outputMemoryMap();
				
			/* check waiting processes if they can run
			 * if they can run, remove from wait-list and add to lookupTable if there's space in memory
			 * if no space, then leave in wait-list
			 */
			for(Process proc : waitingProcesses) {

				// check if process start time has already passed
				if (time >= proc.getStartTime()) {
					
					// attempts to add into memory
					if(fitAlgorithm == 1) {
						
						// if true, we fit process into memory and now will add to lookupTable and remove from wait-list
						if(firstFit(proc)) {
							System.out.println("Process " + proc.getId() + " is starting.");
							lookupTable.add(proc);
							//waitingProcesses.remove(proc);
							
							outputMemoryMap();
						}
					}
				}
			}
			
			// remove any processes that have been added to the running list, lookupTable
			for(Process proc : lookupTable)
				waitingProcesses.remove(proc);
			
			// increment time for the process if it is not complete
			for(int i = 0; i < lookupTable.size(); i++) {
				lookupTable.get(i).incrementTimeAlive(); 
			}
			time++;
		} // end while loop
		System.out.println("Simulation ended.");
	}
}
/*
 * TODO: Process 8 never gets in
 */