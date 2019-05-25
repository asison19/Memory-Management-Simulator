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
		IndexSet[] freeHoles = new IndexSet[proc.getSegmentAmount()];
		
		// for all the segments, see if they can all fit
		for(Segment segment: proc.getSegments()) {
			// if one segment can't fit, the entire process won't be added, so return false, and reset the holes
			// continue otherwise
			if (!(fitHelper(proc, segment, freeHoles)))
				return false;
		}
		
		// set the indexes for the respective segments of the processes
		// add all the segments and their data
		for(int i = 0 ; i < proc.getSegmentAmount(); i++) {
			proc.setIndexesOfSegmentAt(i, freeHoles[i].startIndex, freeHoles[i].endIndex);
			addData(freeHoles[i].startIndex, freeHoles[i].endIndex);
		}	
		// if we get here, all segments have been added and so the process has been added
		return true;
	}
	private boolean fitHelper(Process proc, Segment segment, IndexSet[] freeHoles) {
		for(Hole hole: holes) {
			// if the hole is bigger than the space needed, then set it to be added
			if(hole.getTotalSize() >= segment.spaceAmount) { 
				//segment.setIndexes(hole.getStartIndex(), hole.getStartIndex() + segment.spaceAmount);
				freeHoles[segment.id - 1] = new IndexSet(hole.getStartIndex(), 
						hole.getStartIndex() + segment.spaceAmount - 1);
				// if the hole is the same size, remove the hole
				if(hole.getTotalSize() == segment.spaceAmount)
					holes.remove(hole);
				else // else set the hole's new indexes
					hole.setIndexes(freeHoles[segment.id - 1].endIndex + 1, hole.getEndIndex());
				checkBestOrWorstFit();
				return true;
			}
		}
		// if we get here, none of the holes are large enough for the segment size
		return false;
	}
	
	// similar to looking for holes in outputMemoryMap
	// looks for contiguous memory that can fit the process
	// @param isBest, if true - best fit, if false - worst fit
	private boolean fitBW(Process proc)  {	
		checkBestOrWorstFit();
		
		// after sorting the holes, we can use the first fit algorithm 
		return firstFit(proc);
	}
	
	private void checkBestOrWorstFit() {
		if(fitAlgorithm == 2)
			Collections.sort(holes); // sort in ascending order of the totalSize
		else if(fitAlgorithm == 3)
			Collections.reverse(holes); // sort in descending order of the totalSize
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
			findHoles(); // find the holes in memory
			for(Process proc : waitingProcesses) {

				// check if process start time has already passed, if not, check next process
				if(time >= proc.getStartTime()) {
					
					// attempts to add into memory	
					switch(fitAlgorithm) {
					case 1: // first fit
						if(firstFit(proc))
							startProcess(proc, time);
						break;
					case 2: // best fit
						if(fitBW(proc))
							startProcess(proc, time);
						break;
					case 3: // worst fit
						if(fitBW(proc))
							startProcess(proc, time);
						break;
					}
					findHoles();
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
	
	
	/*
	 * Start the process at time, time by adding it to the running processes list.
	 * process will be removed later together with any other starting processes 
	 */
	private void startProcess(Process proc, int time) {
		System.out.println("Starting Process " + proc.getId() + " at time "+ time +".");
		lookupTable.add(proc);		
		outputMemoryMap();
	}

	@Override
	public void outputProcesses() {
		for(Process proc: lookupTable) {
			for(int i = 0;i < proc.getSegmentAmount(); i++) {
				System.out.println("\t" + proc.getSegmentAt(i).startIndex + "-"
			+ proc.getSegmentAt(i).endIndex + ": Process " + proc.getId() + ", Segment "+ proc.getSegmentAt(i).id + ".");
			}
		}
		
	}
	
	/*
	 * Inner class IndexSet
	 * Used to help in first fit algorithm,
	 * by  keeping track of what index for each segment to go to before
	 * finding out if all segments can or can't fit in memory boolean array.
	 */
	private class IndexSet {
		public IndexSet(int startIndex, int endIndex) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		int startIndex;
		int endIndex;
	}
}
