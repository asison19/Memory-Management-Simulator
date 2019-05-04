package memoryManagementSimulator;
import java.util.ArrayList;

public abstract class Memory {
	
	/* 
	 * boolean array to keep track of which "bytes" are occupied by a process
	 * true meaning the space is taken
	 * false meaning the space is not taken
	 */
	private boolean[] memory;
	
	/*
	 * contains the processes in memory that are running
	 */
	protected ArrayList<Process> lookupTable = new ArrayList<>();
	
	/*
	 * contains the processes that are waiting to be given space to run
	 */
	protected ArrayList<Process> waitingProcesses = new ArrayList<>();
	
	protected int processAmount;
	protected int finishedProcessAmount;
	//private int time = 0; // "clock" of the time the process takes in memory
	
	public Memory(int memorySize, int processAmount){
		memory = new boolean[memorySize];
	}
	
	// remove data from start to last
	public void removeData(int start, int last) {
		for(int i = start; i < last; i++)
			memory[i] = false;
	}
	
	// add data from start to last
	public void addData(int start, int last) {
		for(int i = start; i < last; i++)
			memory[i] = true;
	}
	
	
	public void outputMemoryMap() {
		
	}
	
	public int getProcessAmount() {
		return processAmount;
	}
	
	public abstract void addProcess(Process proc);
	public abstract boolean removeProcess(Process proc, int time);
	
}
