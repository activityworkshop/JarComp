package tim.jarcomp;

import java.util.ArrayList;

/**
 * Class to hold the results of a compare operation
 */
public class CompareResults extends EntryDetails
{
	/** list of entries */
	private ArrayList<EntryDetails> _entries = null;
	/** Number of files in each archive */
	private int[] _numFiles = new int[2];


	/**
	 * @param inList list of EntryDetails objects
	 */
	public void setEntryList(ArrayList<EntryDetails> inList)
	{
		_entries = inList;
	}

	/**
	 * @return entry list
	 */
	public ArrayList<EntryDetails> getEntryList()
	{
		return _entries;
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @return number of files in specified archive
	 */
	public int getNumFiles(int inIndex)
	{
		if (inIndex < 0 || inIndex > 1) {return 0;}
		return _numFiles[inIndex];
	}

	/**
	 * @param inIndex index, either 0 or 1
	 * @param inNumFiles number of files
	 */
	public void setNumFiles(int inIndex, int inNumFiles)
	{
		if (inIndex==0 || inIndex==1) {
			_numFiles[inIndex] = inNumFiles;
		}
	}


	/**
	 * @return true if the entries are in any way different
	 */
	public boolean getEntriesDifferent()
	{
		// Loop over all entries
		for (int i=0; i<_entries.size(); i++) {
			EntryDetails.EntryStatus status = _entries.get(i).getStatus();
			if (status != EntryDetails.EntryStatus.EQUAL
				&& status != EntryDetails.EntryStatus.SAME_SIZE)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if the md5 sums of all (necessary) entries have been checked
	 */
	public boolean getEntriesMd5Checked()
	{
		// Loop over all entries
		for (int i=0; i<_entries.size(); i++) {
			if (_entries.get(i).getStatus() == EntryDetails.EntryStatus.SAME_SIZE) {
				return false;
			}
		}
		return true;
	}
}
