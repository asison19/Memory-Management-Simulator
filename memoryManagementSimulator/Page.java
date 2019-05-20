package memoryManagementSimulator;

/* 
 * Page CLass
 * contains the start of the page kept in memory
 * and the end of the page
 * which may contain internal fragmentation
 */
public class Page{
	public int id;
	public int startIndex = -1; // -1 meaning it has not been set yet
	public int endIndex = -1;
	public int spaceAmount;
	public void resetIndexes() {
		startIndex = -1;
		endIndex = -1;
	}
}
