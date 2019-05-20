/*
 * Segmentation Class
 * Similar to Variable Size Partitioning, but
 * Processes can have non-contiguous memory of predefined sets
 */

package memoryManagementSimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

public class Segmentation extends Memory{ 
	
	private int fitAlgorithm;
	public Segmentation(int memorySize, int processAmount, int fitAlgorithm) {
		super(memorySize, processAmount);
		this.fitAlgorithm = fitAlgorithm;
	}

	// add the process into the waiting processes list
	// this method is called from main
	@Override
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
	 * Searches from the beginning of memory to the end for any available spots
	 * Memory must be contiguous
	 * False if no spot available
	 */
	private boolean firstFit(Process proc) {
		IndexSet[] freeHoles = new IndexSet[proc.getPageAmount()];
		
		for(Page  page: proc.getPages()) {
			if(!firstFitHelper(proc, page, freeHoles)) {
				// if we get here, there is no viable location for the next page of the process
				// so we remove what pages we have already added
				//removeProcess(proc);
				// proc.removeAllIndexes();
				
				return false;
			}
		}
		
		// set the indexes for the respective pages of the processes
		// add all the pages and their data
		for(int i = 0 ; i < proc.getPageAmount(); i++) {
			proc.setIndexesOfPageAt(i, freeHoles[i].startIndex, freeHoles[i].endIndex);
			addData(freeHoles[i].startIndex, freeHoles[i].endIndex);
		}
			
		// if we get here, all pages have been added and so the process has been added
		return true;
	}
	private boolean firstFitHelper(Process proc, Page page, IndexSet[] freeHoles) {
		int freeSpace = 0;
		
		
		// while we find a contiguous set of false booleans find out if it's the right fit
		for(int i = 0; i < memory.length; i++) {
			// if we find one free space, continue looking to see if it's contiguous
			int start = i;
			while(i < memory.length && !memory[i]) {
				freeSpace++;
				// if the space is enough, then put it in and return true
				if(freeSpace == page.spaceAmount) { 
					//proc.setIndexes(start, i);
					//addData(start, i);
					freeHoles[page.id - 1] = new IndexSet(start, i );
					return true;
				}
				i++;
			}
			freeSpace = 0;
		}
		return false;
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
		//Predicate<Hole> condition = Hole -> Hole.smallerThan(proc.getPageAt(0).spaceAmount);
		//holes.removeIf(condition); 
		
		// search the holes from smallest to largest
		for(Hole hole : holes) {
			if(proc.getPageAt(0).spaceAmount <= hole.getTotalSize()) { 
				proc.setIndexes(hole.getStartIndex(), hole.getStartIndex() + proc.getPageAt(0).spaceAmount - 1);
				addData(hole.getStartIndex(), hole.getStartIndex() + proc.getPageAt(0).spaceAmount - 1);
				return true; // end if we've found  a spot it can fit in
			}
		}
		
		//holes.removeAll(holes); // unnecessary here
		return false; 
	}
	
	private void startSimulation() {
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
					// first fit
					if(fitAlgorithm == 1){
						// if true, we fit process into memory and now will add to lookupTable and remove from wait-list
						if(firstFit(proc)) {
							startProcess(proc, time);
						}
						
						// best fit
					} else if(fitAlgorithm == 2) {
						if(fitBW(proc, true)) {
							startProcess(proc, time);
						}
						
						// worst fit
					} else if(fitAlgorithm == 3) {
						if(fitBW(proc, false)) {
							startProcess(proc, time);
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
	
	private void startProcess(Process proc, int time) {
		System.out.println("Starting Process " + proc.getId() + " at time "+ time +".");
		lookupTable.add(proc);		
		outputMemoryMap();
	}

	@Override
	public void outputProcesses() {
		for(Process proc: lookupTable) {
			for(int i = 0;i < proc.getPageAmount(); i++) {
				System.out.println("\t" + proc.getPageAt(i).startIndex + "-"
			+ proc.getPageAt(i).endIndex + ": Process " + proc.getId() + ", Segment "+ proc.getPageAt(i).id + ".");
			}
		}
		
	}

}
/*
 * TODO save code by extending VariableSizePartitioning and only change what's necessary ?
 * 		Necessary as in the fit algorithms
 */
