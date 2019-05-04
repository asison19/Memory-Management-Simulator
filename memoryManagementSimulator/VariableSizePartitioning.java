package memoryManagementSimulator;

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
		startSimulation();
		
	}
	
	
	// TODO pass in as index?
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
	
	//false if no spot available
	private boolean firstFit() {
		
	}
	
	private void bestFit() {
		
	}
	
	private void worstFit() {
		
	}
	
	private void startSimulation() {
		int time = 0;
		
		//continue running until all processes have started and completed
		while(!waitingProcesses.isEmpty() && !lookupTable.isEmpty()) {
			/*
			for(int i = 0; i < waitingProcesses.size(); i++) {
				if(time >= waitingProcesses.get(i).getStartTime()) {
					
				}
			}
			*/
			
			/* check waiting processes if they can run
			 * if they can run, remove from wait-list and add to lookupTable if there's space in memory
			 * if no space, then leave in wait-list
			 */
			for(int i = 0; i < waitingProcesses.size(); i++) {
				if (time >= waitingProcesses.get(i).getStartTime()) {
					//attempts to add into memory
					if(fitAlgorithm == 1) {
						//if true, we fitted process into memory and now will add to lookupTable and remove from wait-list
						if(firstFit()) {
							lookupTable.add(waitingProcesses.get(i));
							waitingProcesses.remove(i);
						}
					}
				}
			}
			
			//check if any processes are complete
			//for(int i = 0; i < lookupTable.size())
			
			// check for complete processes 
			// and increment time for the running processes if they're not complete
			for(int i = 0; i < lookupTable.size(); i++){
				//check if any processes are complete
				if(lookupTable.get(i).isComplete()) {
					System.out.println("Process " + lookupTable.get(i).getId() + " has completed.");
					outputMemoryMap();
					lookupTable.remove(i);
				}
				
				//increment time for the process if it is not complete
				lookupTable.get(i).incrementTimeAlive();
			}
			time++;
		}
	}
}
