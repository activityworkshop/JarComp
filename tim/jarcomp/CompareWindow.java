package tim.jarcomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Class to manage the main compare window
 */
public class CompareWindow
{
	/** Main window object */
	private JFrame _mainWindow = null;
	/** Two files to compare */
	private File[] _files = new File[2];
	/** Displays for jar file details */
	private JarDetailsDisplay[] _detailsDisplays = null;
	/** Label for compare status */
	private JLabel _statusLabel = null;
	/** Second label for contents status */
	private JLabel _statusLabel2 = null;
	/** Table model */
	private EntryTableModel _tableModel = null;
	/** File chooser */
	private JFileChooser _fileChooser = null;
	/** Button to check md5 sums */
	private JButton _md5Button = null;
	/** Refresh button to repeat comparison */
	private JButton _refreshButton = null;
	/** Flag to process md5 sums */
	private boolean _checkMd5 = false;


	/**
	 * Constructor
	 */
	public CompareWindow()
	{
		_mainWindow = new JFrame("Jar Comparer");
		_mainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		_mainWindow.getContentPane().add(makeComponents());
		_mainWindow.pack();
		_mainWindow.setVisible(true);
	}

	/**
	 * Make the GUI components for the main dialog
	 * @return JPanel containing GUI components
	 */
	private JPanel makeComponents()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// Top panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		// Button panel
		JPanel buttonPanel = new JPanel();
		JButton compareButton = new JButton("Compare ...");
		compareButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startCompare();
			}
		});
		buttonPanel.add(compareButton);
		_refreshButton = new JButton("Refresh");
		_refreshButton.setEnabled(false);
		_refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startCompare(_files[0], _files[1], false);
			}
		});
		buttonPanel.add(_refreshButton);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPanel.add(buttonPanel);

		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout(new GridLayout(1, 2, 5, 5));
		_detailsDisplays = new JarDetailsDisplay[2];
		_detailsDisplays[0] = new JarDetailsDisplay();
		detailsPanel.add(_detailsDisplays[0], BorderLayout.WEST);
		_detailsDisplays[1] = new JarDetailsDisplay();
		detailsPanel.add(_detailsDisplays[1], BorderLayout.EAST);
		topPanel.add(detailsPanel);
		detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		_statusLabel = new JLabel("");
		_statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		_statusLabel.setBorder(new EmptyBorder(5, 10, 1, 1));
		topPanel.add(_statusLabel);
		_statusLabel2 = new JLabel("");
		_statusLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
		_statusLabel2.setBorder(new EmptyBorder(1, 10, 5, 1));
		topPanel.add(_statusLabel2);
		mainPanel.add(topPanel, BorderLayout.NORTH);

		// main table panel
		_tableModel = new EntryTableModel();
		JTable table = new JTable(_tableModel)
		{
			/** Modify the renderer according to the row status */
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				if (!isRowSelected(row))
				{
					int modelRow = convertRowIndexToModel(row);
					boolean isChange = ((EntryTableModel)getModel()).areDifferent(modelRow);
					c.setBackground(isChange ? java.awt.Color.YELLOW : getBackground());
				}
				return c;
			}
		};
		table.getColumnModel().getColumn(0).setPreferredWidth(300);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);
		// Table sorting by clicking on column headings
		table.setAutoCreateRowSorter(true);
		mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		// button panel at bottom
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		_md5Button = new JButton("Check Md5 sums");
		_md5Button.setEnabled(false);
		_md5Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startCompare(_files[0], _files[1], true);
			}
		});
		bottomPanel.add(_md5Button);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		bottomPanel.add(closeButton);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		return mainPanel;
	}

	/**
	 * Start the comparison process by prompting for two files
	 */
	public void startCompare()
	{
		startCompare(null, null, false);
	}

	/**
	 * Start the comparison using the two specified files
	 * @param inFile1 first file
	 * @param inFile2 second file
	 * @param inMd5 true to check Md5 sums as well
	 */
	public void startCompare(File inFile1, File inFile2, boolean inMd5)
	{
		// Clear table model
		_tableModel.reset();

		File file1 = inFile1;
		File file2 = inFile2;
		if (file1==null || !file1.exists() || !file1.canRead()) {
			file1 = selectFile("Select first file", null);
		}
		// Bail if cancel pressed
		if (file1 == null) {return;}
		// Select second file if necessary
		if (file2 == null || !file2.exists() || !file2.canRead()) {
			file2 = selectFile("Select second file", file1);
		}
		// Bail if cancel pressed
		if (file2 == null) {return;}
		_files[0] = file1;
		_files[1] = file2;

		// Clear displays
		_detailsDisplays[0].clear();
		_detailsDisplays[1].clear();
		_statusLabel.setText("comparing...");

		// Start separate thread to compare files
		_checkMd5 = inMd5;
		new Thread(new Runnable() {
			public void run() {
				doCompare();
			}
		}).start();
	}

	/**
	 * Compare method, to be done in separate thread
	 */
	private void doCompare()
	{
		CompareResults results = Comparer.compare(_files[0], _files[1], _checkMd5);
		_tableModel.setEntryList(results.getEntryList());
		final boolean archivesDifferent = (results.getStatus() == EntryDetails.EntryStatus.CHANGED_SIZE);
		if (archivesDifferent) {
			_statusLabel.setText("Archives have different size (" + results.getSize(0) + ", " + results.getSize(1) + ")");
		}
		else {
			_statusLabel.setText("Archives have the same size (" + results.getSize(0)+ ")");
		}
		_detailsDisplays[0].setContents(_files[0], results, 0);
		_detailsDisplays[1].setContents(_files[1], results, 1);
		// System.out.println(_files[0].getName() + " has " + results.getNumFiles(0) + " files, "
		//	+ _files[1].getName() + " has " + results.getNumFiles(1));
		if (results.getEntriesDifferent()) {
			_statusLabel2.setText((archivesDifferent?"and":"but") + " the files have different contents");
		}
		else {
			if (results.getEntriesMd5Checked()) {
				_statusLabel2.setText((archivesDifferent?"but":"and") + " the files have exactly the same contents");
			}
			else {
				_statusLabel2.setText((archivesDifferent?"but":"and") + " the files appear to have the same contents");
			}
		}
		_md5Button.setEnabled(!results.getEntriesMd5Checked());
		_checkMd5 = false;
		_refreshButton.setEnabled(true);
		// Possibilities:
		//      Jars have same size, same md5 sum, same contents
		//      Jars have same size but different md5 sum, different contents
		//      Jars have different size, different md5 sum, but same contents
		//      Individual files have same size but different md5 sum
		//      Jars have absolutely nothing in common

		// Maybe poll each minute to check if last modified has changed, then prompt to refresh?
	}


	/**
	 * Select a file for the comparison
	 * @param inTitle title of dialog
	 * @param inFirstFile File to compare selected file with (or null)
	 * @return selected File, or null if cancelled
	 */
	private File selectFile(String inTitle, File inFirstFile)
	{
		if (_fileChooser == null)
		{
			_fileChooser = new JFileChooser();
			_fileChooser.setFileFilter(new GenericFileFilter("Jar files and Zip files", new String[] {"jar", "zip"}));
		}
		_fileChooser.setDialogTitle(inTitle);
		File file = null;
		boolean rechoose = true;
		while (rechoose)
		{
			file = null;
			rechoose = false;
			int result = _fileChooser.showOpenDialog(_mainWindow);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				file = _fileChooser.getSelectedFile();
				rechoose = (!file.exists() || !file.canRead());
			}
			// Check it's not the same as the first file, if any
			if (inFirstFile != null && file != null && file.equals(inFirstFile))
			{
				JOptionPane.showMessageDialog(_mainWindow, "The second file is the same as the first file!\n"
					+ "Please select another file to compare with '" + inFirstFile.getName() + "'",
					"Two files equal", JOptionPane.ERROR_MESSAGE);
				rechoose = true;
			}
		}
		return file;
	}
}

