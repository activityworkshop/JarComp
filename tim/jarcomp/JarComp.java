package tim.jarcomp;

import java.io.File;

/**
 * Jar comparer tool
 * Copyright activityworkshop.net
 * Please see http://activityworkshop.net/software/ for more information
 */
public class JarComp
{
	/** Version number of tool */
	public static final String VERSION_NUMBER = "1";
	/** Build number */
	public static final String BUILD_NUMBER = "008";

	/**
	 * Main entry point to Jar comparer tool
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		// Parse command line arguments, extract two files if available
		File file1 = null, file2 = null;
		if (args != null && args.length > 0) {
			file1 = new File(args[0]);
			if (args.length > 1) {
				file2 = new File(args[1]);
			}
		}
		// Construct main window and initialise
		CompareWindow window = new CompareWindow();
		// Pass two files to start with, or instruct to prompt
		if (file1 == null) {
			// no files given, so prompt for them
			window.startCompare();
		}
		else {
			// at least one file given, so use it/them
			window.startCompare(file1, file2, false);
		}
	}
}
