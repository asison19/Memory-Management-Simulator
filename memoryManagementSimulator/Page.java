package memoryManagementSimulator;

/* 
 * Page CLass
 * contains the start of the page kept in memory
 * and the end of the page
 * which may contain internal fragmentation
 */
public class Page{
	public int id;
	public int startIndex;
	public int endIndex;
	public int spaceAmount;
}
