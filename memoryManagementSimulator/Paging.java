package memoryManagementSimulator;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Paging extends Memory{
	
	private int pageSize;
	private ArrayList<Process> incompleteProcesses; // processes whose page sizes are too large to fit 
	private Page[] pages;
	
	public Paging(int memorySize, int processAmount, int pageSize) {
		super(memorySize, processAmount, -1); // for paging, we don't have a fitAlgorithm
		this.pageSize = pageSize;
		createPages();
	}
	
	private void createPages() {
		int pageId = 1;
		for(int i = 0; i < memory.length; i += pageSize) {
			pages[pageId - 1] = new Page(pageId, pageSize, i, i + pageSize);
			pageId++;
		}
	}
	
	private boolean fitAlgorithm(Process proc) {
		// keep track of which pages will be used to fit the process
		int[] pageIds = new int[proc.getSegmentAmount()];
		
		// for all the segments, see if they can all fit
		for(Segment segment: proc.getSegments()) {
			// if one segment can't fit, the entire process won't be added, so return false, and reset the holes
			// continue otherwise
			if (!(fitHelper(segment, pageIds)))
				return false;
		}
		
		// set the indexes for the respective segments of the processes
		// add all the segments and their data
		for(int i = 0 ; i < proc.getSegmentAmount(); i++) {
			proc.setIndexesOfSegmentAt(i, pages[pageIds[i]].startIndex, pages[pageIds[i]].endIndex);
			proc.getSegmentAt(i).pageId = pageIds[i];
			pages[pageIds[i]].occupy(proc.getId(), proc.getSegmentAt(i).id);
			addData(pages[pageIds[i]].startIndex, pages[pageIds[i]].endIndex);
		}	
		
		// if we get here, all segments have been added and so the process has been added
		return true;
	}
	
	private boolean fitHelper(Segment segment, int[] pageIds) {
		for(Page page: pages) {
			if(!page.occupied) {
				pageIds[segment.id - 1] = page.id;
				return true;
			}
		}
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
					// free the pages
					// for each segments in the process find the page associated to it
					for(Segment segment : lookupTable.get(i).getSegments()) {
						pages[segment.pageId].unOccupy();
					}
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
					// TODO add process into memory pages and check if it fits
					if(fitAlgorithm(proc))
						startProcess(proc, time);
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
		
		//TODO check for any processes who couldn't run because page size too small
	}

	/* TODO make abstract from parent class?
	 * Start the process at time, time by adding it to the running processes list.
	 * process will be removed later together with any other starting processes 
	 */
	private void startProcess(Process proc, int time) {
		System.out.println("Starting Process " + proc.getId() + " at time "+ time +".");
		lookupTable.add(proc);		
		outputMemoryMap();
	}
	
	@Override
	protected void outputProcesses() {
		// TODO make specific for paging
		// output the processes that are currently running
		for(Process proc: lookupTable) {
			for(int i = 0;i < proc.getSegmentAmount(); i++) {
				System.out.println("\t" + proc.getSegmentAt(i).startIndex + "-"
			+ proc.getSegmentAt(i).endIndex + ": Process " + proc.getId() +".");
			}
		}
	}
	
	@Override
	protected void addProcess(Process proc) {
		// check if all the segments can fit in their own pages in memory
		for(Segment segment: proc.getSegments()) {
			if(segment.spaceAmount > pageSize) {
				// if the segment is too big, add it to the incomplete list
				incompleteProcesses.add(proc);
				return;
			}	
		}
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
	
	private class Page {
		int id;
		int size;
		int startIndex;
		int endIndex;
		
		int internalFragmentation = -1;// -1 meaning it is not set
		int processId = -1; 
		int segmentId = -1;
		int segmentSpaceAmount = -1;
		boolean occupied; // if true, this page already has a process' segment attached to it.
		
		public Page(int id, int size, int startIndex, int endIndex) {
			this.id = id;
			this.size = size;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			occupied = false;
		}
		
		public void occupy(int processId, int segmentId) {
			occupied = true;
			this.processId = processId;
			this.segmentId = segmentId;
			
			internalFragmentation = size - segmentSpaceAmount;
		}
		
		public void unOccupy() {
			occupied = false;
			this.processId = -1;
			this.segmentId = -1;
			internalFragmentation = -1;
		}
	}
	
}
