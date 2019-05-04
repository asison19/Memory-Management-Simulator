package memoryManagementSimulator;

public class Process {
	private int id; // the process identification number
	private int startTime; // the time the process started
	private int endTime; // the time the process ended
	//private int spaceAmount; // the amount of space that the process needs in total
	private int pageAmount; // the number of pages that this process is split into
	private Page[] pages; // the pages of the process
	private int nextPage = 0; // the index of pages
	
	public Process(int id, int startTime, int endTime, int pageAmount) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.pageAmount = pageAmount;
		//this.spaceAmount = spaceAmount;
		this.pages = new Page[pageAmount];
	}
	
	//set the next indexes
	public void setIndexes(int start, int end) {
		pages[nextPage].startIndex = start;
		pages[nextPage].endIndex = end;
		nextPage++;
	}
	
	public int getId() {
		return id;
	}
	
	/*
	public void setStartIndex(int[] startIndexes, int[] endIndexes) {
		
		this.startIndexes = startIndexes;
		this.endIndexes = endIndexes;
		//for(int i = 0; i < pages; i++)
			//this.startIndexes[i] = startIndexes[i];
			
	}
	*/
	
	/*
	 * Inner class Page
	 * contains the start of the page kept in memory
	 * and the end of the page
	 * TODO: endIndex will be based off the size of the pages in memory
	 * which may contain internal fragmentation
	 */
	private class Page{
		public int startIndex;
		public int endIndex;
	}
}
