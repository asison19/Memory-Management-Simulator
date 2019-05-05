package memoryManagementSimulator;

/* 
 * Page CLass
 * contains the start of the page kept in memory
 * and the end of the page
 * TODO: endIndex will be based off the size of the pages in memory
 * which may contain internal fragmentation
 */
public class Page{
	public int startIndex;
	public int endIndex;
	public int spaceAmount;
}
