package memoryManagementSimulator;

/* 
 * Page CLass
 * contains the start of the page kept in memory
 * and the end of the page
 * which may contain internal fragmentation
 */
public class Segment{
	public int id;
	public int startIndex = -1; // -1 meaning it has not been set yet
	public int endIndex = -1;
	public int spaceAmount;
	public int pageId = -1; // the page ID which this is associated to.
	
	public void resetIndexes() {
		startIndex = -1;
		endIndex = -1;
	}
	
	public void setIndexes(int start, int end) {
		startIndex = start;
		endIndex = end;
	}
}
