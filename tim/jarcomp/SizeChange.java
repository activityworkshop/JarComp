package tim.jarcomp;

/**
 * Class to represent a size change for sorting and display
 */
class SizeChange implements Comparable<SizeChange>
{
	/** Size difference, positive means the file has grown larger */
	private long _sizeDiff = 0L;
	/** True if the files are in any way different, even if the size is the same */
	private boolean _changed = false;

	/**
	 * Update the difference object when the details are found
	 */
	public void update(long inDiff, boolean inChanged)
	{
		_sizeDiff = inDiff;
		_changed  = inChanged;
	}

	/**
	 * compare two objects
	 */
	public int compareTo(SizeChange inOther)
	{
		if (inOther._changed != _changed)
		{
			return _changed ? 1 : -1;
		}
		if (_sizeDiff > inOther._sizeDiff)
		{
			return 1;
		}
		return (_sizeDiff == inOther._sizeDiff) ? 0 : -1;
	}

	/**
	 * @return value as a string for display
	 */
	public String toString()
	{
		if (!_changed) {
			return "";
		}
		if (_sizeDiff > 0) {
			return "+" + _sizeDiff;
		}
		return "" + _sizeDiff;
	}
}

