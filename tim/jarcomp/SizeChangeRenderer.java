package tim.jarcomp;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for size changes
 */
public class SizeChangeRenderer extends DefaultTableCellRenderer
{
	/**
	 * Render Long values from size change
	 */
	protected void setValue(Object inValue)
	{
		if (inValue == null || !(inValue instanceof Long)) {
			super.setValue(inValue);
		}
		else {
			long value = ((Long) inValue).longValue();
			if (value == 0) {
				setText("");
			}
			else if (value < 0) {
				setText("" + value);
			}
			else {
				setText("+" + value);
			}
		}
	}
}
