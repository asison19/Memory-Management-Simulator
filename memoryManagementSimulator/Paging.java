package memoryManagementSimulator;

public class Paging extends Memory{

	public Paging(int memorySize, int processAmount) {
		super(memorySize, processAmount);
		
	}

	@Override
	public void addProcess(Process proc) {
		
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
