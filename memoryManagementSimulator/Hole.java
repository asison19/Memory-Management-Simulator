package memoryManagementSimulator;

import java.util.Comparator;

/*
 * Class, Hole
 * Spots in memory that don't have anything in them
 */
public class Hole implements Comparable<Hole>{
	private int startIndex;
	private int endIndex;
	private int totalSize;
	
	public Hole(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		totalSize = endIndex - startIndex + 1;
	}
	
	public int getStartIndex() {
		return startIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public int getTotalSize() {
		return totalSize;
	}
	
	public void setIndexes(int start, int end) {
		startIndex = start;
		endIndex = end;
		totalSize = endIndex - startIndex + 1;
	}
	
	//@param size, processes' size
	public boolean smallerThan(int size) {
		return totalSize < size;
	}
	
	@Override
	public int compareTo(Hole o1) {
		// TODO Auto-generated method stub
		return this.getTotalSize() - o1.getTotalSize();
	}	
	
}
