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
	
	/*
	 * Searches from the beginning of memory to the end for any available spots
	 * Memory must be contiguous
	 * False if no spot available
	 */
	private boolean firstFit(Process proc) {
		int freeSpace = 0;
		for(int i = 0; i < memory.length; i++) {
			// while we find a contiguous set of false booleans find out if it's the right fit
			int start = i;
			if(!memory[i]) {
				freeSpace++;
				// if the spot is large enough add it into memory
				if(freeSpace == proc.getSizeOfPageAt(0)) {
					proc.setIndexes(proc.getSizeOfPageAt(0) - i, i);
					addData(proc.getSizeOfPageAt(0) - i, i);
					return true;
				}
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
		
		// continue running until all processes have started and completed
		while(!waitingProcesses.isEmpty() || !lookupTable.isEmpty()) {
			
			/* check waiting processes if they can run
			 * if they can run, remove from wait-list and add to lookupTable if there's space in memory
			 * if no space, then leave in wait-list
			 */
			for(int i = 0; i < waitingProcesses.size(); i++) {
				if (time >= waitingProcesses.get(i).getStartTime()) {
					//attempts to add into memory
					if(fitAlgorithm == 1) {
						//if true, we fitted process into memory and now will add to lookupTable and remove from wait-list
						if(firstFit(waitingProcesses.get(i))) {
							lookupTable.add(waitingProcesses.get(i));
							waitingProcesses.remove(i);
							outputMemoryMap();
						}
					}
				}
			}
			
			// check for complete processes 
			for(int i = 0; i < lookupTable.size(); i++){
				// check if any processes are complete
				if(lookupTable.get(i).isComplete()) {
					System.out.println("Process " + lookupTable.get(i).getId() + " has completed at time " + time + ".");
					outputMemoryMap();
				}
			}
			lookupTable.removeIf(condition); //remove from list if process is complete
			
			// increment time for the process if it is not complete
			for(int i = 0; i < lookupTable.size(); i++) {
				lookupTable.get(i).incrementTimeAlive(); 
			}
			time++;
		}
	}
}
/* Example Output:
Memory Size: 2000
Memory management policy (1- VSP, 2- PAG, 3- SEG): 1
Fit algorithm (1- first-fit, 2- best-fit, 3- worst-fit): 1
File name of the Input: D:\CS4310\Program3\vspIn.dat
Starting Simulation.
Process 1 has completed at time 200.
Process 3 has completed at time 200.
Process 5 has completed at time 200.
Process 9 has completed at time 1200.
Process 7 has completed at time 2000.
Process 2 has completed at time 2001. // TODO not sure why this is happening...
Process 6 has completed at time 2001.
Process 4 has completed at time 2002.
Process 8 has completed at time 2100.
 */