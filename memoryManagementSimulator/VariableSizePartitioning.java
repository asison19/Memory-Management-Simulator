package memoryManagementSimulator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
				if(freeSpace == proc.getSizeOfPageAt(0)) { // because this is VSP, there is only 1 "page"
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
	
	// similar to looking for holes in outputMemoryMap
	// looks for smallest contiguous memory that can fit the process
	private boolean bestFit(Process proc) {
		ArrayList<Hole> holes = new ArrayList<>();
		
		// Search for the holes and add them all into a list
		// TODO optimize. redundant to continue searching if hole is larger than another hole that itself
		// is larger than the space needed.
		for(int i = 0; i < memory.length; i++) {
			int start = i;
			int end = i;
			// if we find a empty space, look to see how much in a row
			while(i < memory.length && !memory[i]) { // TODO it's getting stuck here
				end++;
				i++;
				
				if (end < memory.length && memory[end]) {
					holes.add(new Hole(start, end));
				}
			}
		}
		
		Collections.sort(holes); // sort in ascending order of the totalSize
		
		// prune small holes that can't fit the process 
		//Predicate<Hole> condition = Hole -> Hole.smallerThan(proc.getPageAt(0).spaceAmount);
		//holes.removeIf(condition); 
		
		// search the holes from smallest to largest
		for(Hole hole : holes) {
			if(proc.getPageAt(0).spaceAmount <= hole.getTotalSize()) {
				proc.setIndexes(hole.getStartIndex(), hole.getEndIndex());
				addData(hole.getStartIndex(), hole.getEndIndex());
				return true; // end if we've found  a spot it can fit in
			}
		}
		
		//holes.removeAll(holes); // unnecessary here
		return false; // TODO
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
				if(time >= proc.getStartTime()) {
					
					// attempts to add into memory
					// first fit
					if(fitAlgorithm == 1){
						// if true, we fit process into memory and now will add to lookupTable and remove from wait-list
						if(firstFit(proc)) {
							System.out.println("Process " + proc.getId() + " is starting.");
							lookupTable.add(proc);		
							outputMemoryMap();
						}
						// best fit
					} else if(fitAlgorithm == 2) {
						if(bestFit(proc)) {
							System.out.println("Process " + proc.getId() + " is starting.");
							lookupTable.add(proc);		
							outputMemoryMap();
						}
						// worst fit
					} else if(fitAlgorithm == 3) {
						if(worstFit(proc)) {
							System.out.println("Process " + proc.getId() + " is starting.");
							lookupTable.add(proc);		
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