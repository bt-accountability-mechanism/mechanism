package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import utils.LogzLogHandler;
import utils.MultiRegex;
import utils.Regex;
import utils.RegexBuilder;
import utils.RegexFileScanner;

public class LeftPanel implements IPanel {

	private JPanel panel;
	
	private JFileChooser fileChooser;
	private JButton fileChooserButton;
	private File selectedFile;
	private JLabel selectedFileText;
		
	private DefaultMutableTreeNode topRegexNode;
		
	private DefaultListModel<String> variablesListModel;
	private static final String VARIABLE_LIST_ITEM_SEP = " -> ";
	
	private static final double DEFAULT_HEADLINE_FONT_SCALE_FACTOR = 1.5;
	
	public LeftPanel() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		initComponents();
	}
	
	
	@Override
	public JPanel getPanel() {
		return panel;
	}
	
	private void initComponents() {
		addHeadline();
		addFileSelectionSection();
		addRegexSection();
		addVariablesSection();
		addButtons();
	}
	
	private void addHeadline() {
		JLabel headline = new JLabel("New file evaluation");
		headline.setFont(new Font(headline.getFont().getName(), Font.BOLD, headlineFontSize(headline.getFont().getSize(), 2)));
		panel.add(headline);
	}
	
	private void addFileSelectionSection() {
		JLabel headline = new JLabel("Choose file to be evaluated");
		headline.setFont(new Font(headline.getFont().getName(), Font.BOLD, headlineFontSize(headline.getFont().getSize())));
		panel.add(headline);
		
		fileChooser = new JFileChooser();
		selectedFile = null;
		selectedFileText = new JLabel("No file selected");
		
		fileChooserButton = new JButton("Open a file...");
		fileChooserButton.addActionListener(new SelectFileButtonActionLister());
		panel.add(fileChooserButton);
		panel.add(selectedFileText);
	}
	
	private void addRegexSection() {
		JLabel headline = new JLabel("Create/modify patterns");
		headline.setFont(new Font(headline.getFont().getName(), Font.BOLD, headlineFontSize(headline.getFont().getSize())));
		panel.add(headline);
		
		topRegexNode = new DefaultMutableTreeNode("Regular expressions");
	    JTree tree = new JTree(topRegexNode);
	    JScrollPane treeView = new JScrollPane(tree);
	    addDemoRegex();
	    tree.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            if (SwingUtilities.isRightMouseButton(e)) {
	            	tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
	            	showMenu("Test123", e.getX(), e.getY());
	            } else {
	            	int selRow = tree.getRowForLocation(e.getX(), e.getY());
		            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		            if(selRow != -1 && e.getClickCount() == 1) {
		                // showMenu("Test123", e.getX(), e.getY());
		            }
	            }
	        }
	        
	        private void showMenu(String label, int x, int y) {
	        	if (tree.getLastSelectedPathComponent() != null) {
	        		TreeNode currentNode = (TreeNode) tree.getLastSelectedPathComponent();
		        	JPopupMenu popup = new JPopupMenu();
		        	if (!currentNode.isLeaf()) {
			        	popup.add(new JMenuItem(new AbstractAction("Add new regex") {
			        	    public void actionPerformed(ActionEvent ae) {
			        	    	addNewNode(currentNode);
			        	    }
			        	}));
		        	}
					popup.add(new JMenuItem(new AbstractAction("Modify") {
		        	    public void actionPerformed(ActionEvent ae) {
		        	    	changeNode(currentNode);
		        	    }
		        	}));
					popup.add(new JMenuItem(new AbstractAction("Delete") {
		        	    public void actionPerformed(ActionEvent ae) {
		        	    	removeNode(currentNode);
		        	    }
		        	}));
					popup.show(tree, x, y);
	        	}
	        }
	        
	        private void removeNode(TreeNode currentNode) {
	        	if (!currentNode.isLeaf() && currentNode.getChildCount() > 1 || currentNode.isLeaf()) {
	        		DefaultMutableTreeNode currentMutableNode = ((DefaultMutableTreeNode) (tree.getLastSelectedPathComponent()));
	        		currentMutableNode.removeFromParent();
	        		
	        		DefaultTreeModel model = (DefaultTreeModel) (tree.getModel()); 
		        	model.reload();
	        	} else {
	        		JOptionPane.showMessageDialog(panel.getParent(),
	        			    "Every pattern needs at least one sub pattern");
	        	}
	        }
	        
	        private void changeNode(TreeNode currentNode) {
	        	modifyNode(currentNode, false);
	        }
	        
	        private void addNewNode(TreeNode currentNode) {
	        	modifyNode(currentNode, true);
	        }
	        
	        private void modifyNode(TreeNode currentNode, Boolean isAddition) {
	        	Boolean isRoot = currentNode.getParent() == null;
	        	String defaultID = null;
	        	String defaultRegex = null;
        		String defaultPrefix = null;
        		String defaultSuffix = null;
        		
	        	DefaultMutableTreeNode currentMutableNode = ((DefaultMutableTreeNode) (tree.getLastSelectedPathComponent()));
        		if (!isAddition && currentMutableNode.getUserObject() instanceof Regex) {
        			Regex reg = ((Regex) currentMutableNode.getUserObject());
        			defaultID = reg.id;
        			defaultRegex = reg.data;
        			defaultPrefix = reg.prefix;
        			defaultSuffix = reg.suffix;
        		} else {
        			defaultID = currentMutableNode.getUserObject().toString();
        		}
        		
	        	String id = (String)JOptionPane.showInputDialog(
	                    panel.getParent(),
	                    "Please enter an ID (unique!) for your pattern",
	                    "Define ID",
	                    JOptionPane.PLAIN_MESSAGE, null, null, defaultID);
	        	
	        	if (!isRoot && isAddition || !isAddition && currentNode.isLeaf()) {
	        		String regex = (String)JOptionPane.showInputDialog(
		                    panel.getParent(),
		                    "Please enter a regular expression",
		                    "Regular expression",
		                    JOptionPane.PLAIN_MESSAGE, null, null, defaultRegex);
	        		String prefix = (String)JOptionPane.showInputDialog(
		                    panel.getParent(),
		                    "If you like, you can enter a regular expression suffix like ^",
		                    "Regular expression prefix",
		                    JOptionPane.PLAIN_MESSAGE, null, null, defaultPrefix);
	        		String suffix = (String)JOptionPane.showInputDialog(
		                    panel.getParent(),
		                    "If you like, you can enter a regular expression suffix like * or +",
		                    "Regular expression suffix",
		                    JOptionPane.PLAIN_MESSAGE, null, null, defaultSuffix);
	        		
	        		if (!isAddition) {
	        			currentMutableNode.setUserObject(new Regex(id, regex, prefix, suffix));
	        		} else {
	        			currentMutableNode.add(new DefaultMutableTreeNode(new Regex(id, regex, prefix, suffix)));
	        		}
	        	} else {
	        		
	        		if (!isAddition) {
	        			currentMutableNode.setUserObject(id);
	        		} else {
	        			final DefaultMutableTreeNode node = new DefaultMutableTreeNode(id);
		        		node.add(new DefaultMutableTreeNode(new Regex("All_" + id, ".")));
	        			currentMutableNode.add(node);
	        		}
	        	}
	        	
	        	DefaultTreeModel model = (DefaultTreeModel) (tree.getModel()); 
	        	model.reload();
	        }
	    });
	    
	    panel.add(treeView);
	}
	
	private void addDemoRegex() {
		DefaultMutableTreeNode patternA = new DefaultMutableTreeNode("PatternA");
		patternA.add(new DefaultMutableTreeNode(new Regex("SubRegexA", completeLogLine("Robot goes left")))) ;
		patternA.add(new DefaultMutableTreeNode(new Regex("SubRegexB", completeLogLine("Robot goes right"), null, "+")));
		patternA.add(new DefaultMutableTreeNode(new Regex("SubRegexC", completeLogLine("Robot goes back"))));
		
		topRegexNode.add(patternA);
	}
	
	private static String completeLogLine(final String message) {
		return "%datetime% - %name% - %types% - (?:" + message + ")%newline%";
	}
	
	private void addVariablesSection() {
		JLabel headline = new JLabel("Create/modify Variables");
		headline.setFont(new Font(headline.getFont().getName(), Font.BOLD, headlineFontSize(headline.getFont().getSize())));
		panel.add(headline);
		
		variablesListModel = new DefaultListModel<String>();
		addDemoVariables();
		JList<String> list = new JList<String>(variablesListModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		// list.setVisibleRowCount(5);
		panel.add(list);
		
		JTextField variableTextField = new JTextField(1);
		variableTextField.setColumns(1);
		variableTextField.addActionListener(new AbstractAction()
		{
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public void actionPerformed(ActionEvent e)
		    {
				JTextField source = (JTextField) e.getSource();
				variablesListModel.addElement(source.getText());
				source.setText("");
		    }
		});
		variableTextField.setMaximumSize(new Dimension(400, 20));
		panel.add(variableTextField);
	}
	
	private void addDemoVariables() {
		for (Entry<String, String> item : getDemoVariables().entrySet()) {
			variablesListModel.addElement(item.getKey() + VARIABLE_LIST_ITEM_SEP + item.getValue());
		}
	}
	
	private void addButtons() {
		JButton createButton = new JButton("Start new evaluation");
		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedFile == null) {
					JOptionPane.showMessageDialog(panel.getParent(),
	        			    "Could not execute due to missing file");
					return;
				}
				
				RegexFileScanner scanner = new RegexFileScanner(selectedFile.getAbsolutePath());
				scanner.addOutputHandler(new LogzLogHandler());
				final List<String> regexIDs = new ArrayList<String>();
				
				Enumeration<DefaultMutableTreeNode> nodes = topRegexNode.children();
				Enumeration<DefaultMutableTreeNode> leafs;
				List<MultiRegex> multiRegexList = new ArrayList<MultiRegex>();
				List<Regex> regexList = new ArrayList<Regex>();
				while (nodes.hasMoreElements()) {
					DefaultMutableTreeNode node = nodes.nextElement();
					String multiRegexID = node.getUserObject().toString();
					leafs = node.children();
					regexList.clear();
					while (leafs.hasMoreElements()) {
						Regex currentRegex = (Regex) leafs.nextElement().getUserObject();
						regexList.add(currentRegex);
					}
					multiRegexList.add(new MultiRegex(multiRegexID, regexList.toArray(new Regex[regexList.size()])));
					regexIDs.add(multiRegexID);
				}
				
				final Map<String, String> variables = getVariables();
				final String finalRegex = RegexBuilder.mergeRegexExpressions(multiRegexList.toArray(new MultiRegex[multiRegexList.size()]), variables);
				System.out.println(finalRegex);
				try {
					scanner.scan(finalRegex, regexIDs);
					JOptionPane.showMessageDialog(panel.getParent(),
	        			    "Scan successfull and sent to your evaluation program! ");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		panel.add(createButton);
	}
	
	private Map<String, String> getVariables() {
		Map<String, String> variables = new HashMap<String, String>();
		for (int index = 0; index < variablesListModel.getSize(); index++) {
			String variableData = variablesListModel.getElementAt(index);
			int indexOfSep = variableData.indexOf(VARIABLE_LIST_ITEM_SEP);
			// do avoid split due to possibilities of duplicates of separators
			if (indexOfSep > 0) {
				String key = variableData.substring(0, indexOfSep);
				String value = variableData.substring(indexOfSep + VARIABLE_LIST_ITEM_SEP.length(), variableData.length());
				variables.put(key, value);
			}
		}
		return variables;
	}
	
	private static int headlineFontSize(int originSize) {
		return headlineFontSize(originSize, DEFAULT_HEADLINE_FONT_SCALE_FACTOR);
	}
	
	private static int headlineFontSize(int originSize, double factor) {
		return (int) (originSize * factor);
	}
	
	private class SelectFileButtonActionLister implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
		    //Handle open button action.
		    if (e.getSource() == fileChooserButton) {
		        int returnVal = fileChooser.showOpenDialog(panel.getParent());

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            selectedFile = fileChooser.getSelectedFile();
		            selectedFileText.setText("Selected file: " + selectedFile.getName());
		            
		            //This is where a real application would open the file.
		            // log.append("Opening: " + file.getName() + "." + newline);
		        } else {
		            // log.append("Open command cancelled by user." + newline);
		        }
		   } 
		}
		
	}
	
	private static Map<String, String> getDemoVariables() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("%datetime%", "(\\d{4}+-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01]) (?:00|[0-9]|1[0-9]|2[0-3]):(?:[0-9]|[0-5][0-9]):(?:[0-9]|[0-5][0-9]),(?:[0-9]{3}))");
		map.put("%name%", "(?:irobot)");
		map.put("%types%", "(?:INFO|DEBUG)");
		map.put("%newline%", "(?:[\\n|\\r])");
		
		return map;
	}
	
}
