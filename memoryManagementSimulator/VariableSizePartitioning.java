package memoryManagementSimulator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

public class VariableSizePartitioning extends Memory{

	//private int spaceAmount;
	public VariableSizePartitioning(int memorySize, int processAmount, int fitAlgorithm) {
		super(memorySize, processAmount, fitAlgorithm);
	}
	
	// add the process into the waiting processes list
	// this method is called from main
	
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
				if(freeSpace == proc.getSizeOfSegmentAt(0)) { // because this is VSP, there is only 1 segment
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
	// looks for contiguous memory that can fit the process
	// @param isBest, if true - best fit, if false - worst fit
	private boolean fitBW(Process proc, boolean isBest)  {
		ArrayList<Hole> holes = new ArrayList<>();
		
		// Search for the holes and add them all into a list
		// TODO optimize. redundant to continue searching if hole is larger than another hole that itself
		// is larger than the space needed.
		for(int i = 0; i < memory.length; i++) {
			int start = i;
			
			// if we find a empty space, look to see how much in a row
			while(i < memory.length && !memory[i]) { 
				i++;
				if (i == (memory.length - 1) || (i < memory.length && memory[i])) {
					holes.add(new Hole(start, i));
				}
			}
		}
		
		if(isBest)
			Collections.sort(holes); // sort in ascending order of the totalSize
		else
			Collections.reverse(holes); // sort in descending order of the totalSize
		
		// prune small holes that can't fit the process 
		Predicate<Hole> condition = Hole -> Hole.smallerThan(proc.getSegmentAt(0).spaceAmount);
		holes.removeIf(condition); 
		
		// TODO can remove this if pruned
		// search the holes from smallest to largest or largest to smallest for best and worst fit respectively
		for(Hole hole : holes) {
			if(proc.getSegmentAt(0).spaceAmount <= hole.getTotalSize()) { 
				proc.setIndexes(hole.getStartIndex(), hole.getStartIndex() + proc.getSegmentAt(0).spaceAmount - 1);
				addData(hole.getStartIndex(), hole.getStartIndex() + proc.getSegmentAt(0).spaceAmount - 1);
				return true; // end if we've found  a spot it can fit in
			}
		}
		
		//holes.removeAll(holes); // unnecessary here
		return false; 
	}
	@Override 
	protected void startSimulation() {
		int time = 0;
		Predicate<Process> condition = proc -> proc.isComplete(); // condition for when to remove processes from lookupTable
		
		// continue running until all processes have started and completed
		while(!waitingProcesses.isEmpty() || !lookupTable.isEmpty()) {
			
			// check if any processes have arrived
			for(int i = 0; i < waitingProcesses.size(); i++) {
				if(time == waitingProcesses.get(i).getStartTime())
					System.out.println("Process " + waitingProcesses.get(i).getId() + " has arrived at time " + time + ".");
			}
			
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
					switch(fitAlgorithm) {
					case 1: // first fit
						if(firstFit(proc))
							startProcess(proc, time);
						break;
					case 2: // best fit
						if(fitBW(proc, true))
							startProcess(proc, time);
						break;
					case 3: // worst fit
						if(fitBW(proc, false))
							startProcess(proc, time);
						break;
					}
				}
			}
			
			// remove any processes that have been added to the running list, lookupTable
			for(Process proc : lookupTable)
				waitingProcesses.remove(proc);
			
			// increment time for the process if it is not complete
			for(int i = 0; i < lookupTable.size(); i++)
				lookupTable.get(i).incrementTimeAlive(); 
			time++;
		} // end while loop
		System.out.println("Simulation ended.");
	}
	
	private void startProcess(Process proc, int time) {
		System.out.println("Starting Process " + proc.getId() + " at time "+ time +".");
		lookupTable.add(proc);		
		outputMemoryMap();
	}

	@Override
	protected void outputProcesses() {
		// output the processes that are currently running
		for(Process proc: lookupTable) {
			for(int i = 0;i < proc.getSegmentAmount(); i++) {
				System.out.println("\t" + proc.getSegmentAt(i).startIndex + "-"
			+ proc.getSegmentAt(i).endIndex + ": Process " + proc.getId() +".");
			}
		}
	}
}