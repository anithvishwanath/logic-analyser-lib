package logicanalyser;

import com.google.common.base.Preconditions;
import java.io.Serializable;

/**
 * Represents a range of locations in a file. 
 * Used for locating markers
 */
public class Interval implements Serializable, Comparable<Interval> {
	private static final long serialVersionUID = 1L;

	public final int start;
	public final int end;

	/**
	 * Creates a new Interval given the two endpoints
	 *
	 * @param start The start index, inclusive
	 * @param end The end index, exclusive
	 */
	public Interval(int start, int end) {
		Preconditions.checkArgument(start < end && start >= 0);

		this.start = start;
		this.end = end;
	}

	/**
	 * Copies another Interval
	 *
	 * @param other The Interval to copy
	 */
	public Interval(Interval other) {
		this.start = other.start;
		this.end = other.end;
	}

	/**
	 * Gets the number of characters contained within the Interval.
	 *
	 * @return The number of characters
	 */
	public int length() {
		return end - start;
	}

	@Override
	public String toString() {
		return String.format("[%d-%d)", start, end);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 43 * hash + this.start;
		hash = 43 * hash + this.end;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Interval) {
			Interval other = (Interval) obj;
			return start == other.start && end == other.end;
		}

		return false;
	}
	
	@Override
	public int compareTo(Interval o) {
		int comp = Integer.compare(start, o.start);
		if (comp == 0) {
			return Integer.compare(end, o.end);
		} else {
			return comp;
		}
	}
}
