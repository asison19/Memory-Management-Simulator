package memoryManagementSimulator;

public class Process {
	private int id; // the process identification number
	private int startTime; // the time the process started
	private int endTime; // the amount time alive fir the process to end
	private int[] spaceAmount; // the amount of space that the process needs at one page
	private Page[] pages; // the pages of the process
	private int nextPage = 0; // the index of pages
	private int timeAlive = 0; // how long the process has run
	
	public Process(int id, int startTime, int endTime, int pageAmount, int[] spaceAmount) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.spaceAmount = spaceAmount;
		this.pages = new Page[pageAmount];
		
		//create the pages and assign them the memory they need
		for(int i = 0; i < pages.length; i++) {
			pages[i] = new Page();
			pages[i].id = i + 1;
			pages[i].spaceAmount = spaceAmount[i];
		}
	}
	
	//set the next indexes
	public void setIndexes(int start, int end) {
		pages[nextPage].startIndex = start;
		pages[nextPage].endIndex = end;
		nextPage++;
	}
	
	public void setIndexesOfPageAt(int pageID, int start, int end) {
		pages[pageID].startIndex = start;
		pages[pageID].endIndex = end;
	}
	
	// remove all the page indexes and reset nextPage to zero
	public void removeAllIndexes() {
		for(int i = 0; i < pages.length; i++) {
			pages[nextPage].resetIndexes();
		}
			
		nextPage = 0;
	}
	
	public int getPageAmount() {
		return pages.length;
	}
	
	public Page getPageAt(int i) {
		return pages[i];
	}
	
	public int getSizeOfPageAt(int i) {
		return pages[i].spaceAmount;
	}
	
	public int getId() {
		return id;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getTimeAlive() {
		return timeAlive;
	}
	
	public void incrementTimeAlive() {
		timeAlive++;
	}
	
	public boolean isComplete() {
		return (timeAlive >= endTime);
	}
	
	public Page[] getPages() {
		return pages;
	}
	
	public void resetNextPage() {
		nextPage = 0;
	}
	
}
