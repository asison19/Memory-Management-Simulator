package memoryManagementSimulator;

public class Process {
	private int id; // the process identification number
	private int startTime; // the time the process started
	private int endTime; // the amount time alive fir the process to end
	private int[] spaceAmount; // the amount of space that the process needs at one page
	private Segment[] segments; // the pages of the process
	private int nextPage = 0; // the index of pages
	private int timeAlive = 0; // how long the process has run
	
	public Process(int id, int startTime, int endTime, int pageAmount, int[] spaceAmount) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.spaceAmount = spaceAmount;
		this.segments = new Segment[pageAmount];
		
		//create the pages and assign them the memory they need
		for(int i = 0; i < segments.length; i++) {
			segments[i] = new Segment();
			segments[i].id = i + 1;
			segments[i].spaceAmount = spaceAmount[i];
		}
	}
	
	//set the next indexes
	public void setIndexes(int start, int end) {
		segments[nextPage].startIndex = start;
		segments[nextPage].endIndex = end;
		nextPage++;
	}
	
	public void setIndexesOfSegmentAt(int pageID, int start, int end) {
		segments[pageID].startIndex = start;
		segments[pageID].endIndex = end;
	}
	
	// TODO remove if unused
	// remove all the page indexes and reset nextPage to zero
	public void removeAllIndexes() {
		for(int i = 0; i < segments.length; i++)
			segments[nextPage].resetIndexes();
			
		nextPage = 0;
	}
	
	public int getSegmentAmount() {
		return segments.length;
	}
	
	public Segment getSegmentAt(int i) {
		return segments[i];
	}
	
	public int getSizeOfSegmentAt(int i) {
		return segments[i].spaceAmount;
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
	
	public Segment[] getSegments() {
		return segments;
	}
	
	public void resetNextPage() {
		nextPage = 0;
	}
	
}
