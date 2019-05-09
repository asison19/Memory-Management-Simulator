package memoryManagementSimulator;

public class Process {
	private int id; // the process identification number
	private int startTime; // the time the process started
	private int endTime; // the amount time alive fir the process to end
	private int[] spaceAmount; // the amount of space that the process needs at one page
	//private int pageAmount; // the number of pages that this process is split into // TODO remove? cause .length works
	private Page[] pages; // the pages of the process
	private int nextPage = 0; // the index of pages
	//private boolean isCompleted = false; // true once process has been completed
	private int timeAlive = 0; // how long the process has run
	
	public Process(int id, int startTime, int endTime, int pageAmount, int[] spaceAmount) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		//this.pageAmount = pageAmount;
		this.spaceAmount = spaceAmount;
		this.pages = new Page[pageAmount];
		
		//create the pages and assign them the memory they need
		for(int i = 0; i < pages.length; i++) {
			pages[i] = new Page();
			pages[i].spaceAmount = spaceAmount[i];
		}
	}
	
	//set the next indexes
	public void setIndexes(int start, int end) {
		pages[nextPage].startIndex = start;
		pages[nextPage].endIndex = end;
		nextPage++;
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
