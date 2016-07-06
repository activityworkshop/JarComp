package tim.jarcomp;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import tim.jarcomp.EntryDetails.EntryStatus;

/**
 * Class to hold the table model for the comparison table
 */
public class EntryTableModel extends AbstractTableModel
{
	/** list of entries */
	private ArrayList<EntryDetails> _entries = null;

	/**
	 * Clear list to start a new comparison
	 */
	public void reset() {
		_entries = new ArrayList<EntryDetails>();
	}

	/**
	 * Reset the table with the given list
	 * @param inList list of EntryDetails objects
	 */
	public void setEntryList(ArrayList<EntryDetails> inList)
	{
		_entries = inList;
		fireTableDataChanged();
	}

	/**
	 * @return number of columns in table
	 */
	public int getColumnCount() {
		return 3;
		// TODO: Columns for size1, size2, status (as icon), size difference
	}


	/**
	 * @return class of column, needed for sorting the Longs properly
	 */
	public Class<?> getColumnClass(int inColNum)
	{
		return getValueAt(0, inColNum).getClass();
	}

	/**
	 * @return column name
	 */
	public String getColumnName(int inColNum)
	{
		if (inColNum == 0) {return "Filename";}
		else if (inColNum == 1) {return "Status";}
		return "Size Change";
	}

	/**
	 * @return number of rows in the table
	 */
	public int getRowCount()
	{
		if (_entries == null) {return 0;}
		return _entries.size();
	}

	/**
	 * @return object at specified row and column
	 */
	public Object getValueAt(int inRowNum, int inColNum)
	{
		if (inRowNum >= 0 && inRowNum < getRowCount())
		{
			EntryDetails entry = _entries.get(inRowNum);
			if (inColNum == 0) return entry.getName();
			else if (inColNum == 1) return getText(entry.getStatus());
			return entry.getSizeChange();
		}
		return null;
	}

	/**
	 * Convert an entry status into text
	 * @param inStatus entry status
	 * @return displayable text
	 */
	private static String getText(EntryStatus inStatus)
	{
		switch (inStatus) {
			case ADDED: return "Added";
			case CHANGED_SIZE: return "Changed size";
			case CHANGED_SUM: return "Changed sum";
			case EQUAL: return "=";
			case REMOVED: return "Removed";
			case SAME_SIZE: return "Same size";
		}
		return inStatus.toString();
	}

	/**
	 * @return true if specified row represents a difference between the two files
	 */
	public boolean areDifferent(int inRowNum)
	{
		if (inRowNum >= 0 && inRowNum < getRowCount())
		{
			return _entries.get(inRowNum).isChanged();
		}
		return false;
	}
}

