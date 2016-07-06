package tim.jarcomp;

/**
 * Class to represent a single entry in the jar file
 * for displaying in the comparison table
 */
public class EntryDetails
{
	/** Name of entry, including full path */
	private String _name = null;
	/** Flag to show if it's present or not (might be zero length) */
	private boolean[] _present = new boolean[2];
	/** Sizes of this file, in bytes, in archives */
	private long[] _sizes = new long[2];
	/** Md5 sums in both archives */
	private String[] _md5Sums = new String[2];
	/** SizeChange */
	private SizeChange _sizeChange = new SizeChange();

	/** Constants for entry status */
	public enum EntryStatus
	{
		/** File not in first but in second    */ ADDED,
		/** File found in first, not in second */ REMOVED,
		/** File size different in two files   */ CHANGED_SIZE,
		/** File size same (md5 not checked)   */ SAME_SIZE,
		/** File checksum different            */ CHANGED_SUM,
		/** Files really equal                 */ EQUAL
	};
	// TODO: Each of these status flags needs an icon

	/**
	 * @return name of entry
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param inName name to set
	 */
	public void setName(String inName) {
		_name = inName;
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @return size of this file in corresponding archive
	 */
	public long getSize(int inIndex)
	{
		if (inIndex < 0 || inIndex > 1) {return 0L;}
		return _sizes[inIndex];
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @param inSize size of file in bytes
	 */
	public void setSize(int inIndex, long inSize)
	{
		if (inIndex==0 || inIndex==1)
		{
			_sizes[inIndex] = inSize;
			_present[inIndex] = true;
			_sizeChange.update(_sizes[1] - _sizes[0], isChanged());
		}
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @return md5 sum of this file in corresponding archive
	 */
	public String getMd5Sum(int inIndex)
	{
		if (inIndex < 0 || inIndex > 1) {return null;}
		return _md5Sums[inIndex];
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @param inMd5Sum md5 checksum of this file
	 */
	public void setMd5Sum(int inIndex, String inMd5Sum)
	{
		if (inIndex==0 || inIndex==1)
		{
			_md5Sums[inIndex] = inMd5Sum;
			_sizeChange.update(_sizes[1] - _sizes[0], isChanged());
		}
	}

	/**
	 * @return true if md5 sums have been generated for this entry
	 */
	public boolean getMd5Checked()
	{
		return (_md5Sums[0] != null && _md5Sums[1] != null);
	}

	/**
	 * @return status of entry
	 */
	public EntryStatus getStatus()
	{
		if (!_present[0] && _present[1]) {return EntryStatus.ADDED;}
		if (_present[0] && !_present[1]) {return EntryStatus.REMOVED;}
		if (_sizes[0] != _sizes[1]) {return EntryStatus.CHANGED_SIZE;}
		if (!getMd5Checked()) {return EntryStatus.SAME_SIZE;}
		// md5 sums have been checked
		if (!_md5Sums[0].equals(_md5Sums[1])) {return EntryStatus.CHANGED_SUM;}
		return EntryStatus.EQUAL;
	}

	/**
	 * @return difference in file sizes (bytes)
	 */
//	public long getSizeDifference()
//	{
//		return _sizes[1] - _sizes[0];
//	}


	/**
	 * @return size change object
	 */
	public SizeChange getSizeChange()
	{
		return _sizeChange;
	}

	/**
	 * @return true if the row represents a change
	 */
	public boolean isChanged()
	{
		EntryStatus status = getStatus();
		return status != EntryStatus.SAME_SIZE && status != EntryStatus.EQUAL;
	}
}

