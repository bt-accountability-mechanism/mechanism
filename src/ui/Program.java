package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class Program implements IProgram {

	private JFrame frame;
	private LeftPanel leftPanel;
	private JPanel rightPanel;
		
	public Program() {
		frame = new JFrame("Logging evaluation mechanism");
		setFrameSize();
		setPanels();
	}
	
	@Override
	public void run() {
		frame.setVisible(true);
	}
	
	private void setFrameSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int) (screenSize.getWidth() * 0.8), (int) (screenSize.getWidth() * 0.8));
	}
	
	private void setPanels() {
		leftPanel = new LeftPanel();
		/*rightPanel = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftPanel.getPanel());
		splitPane.setRightComponent(rightPanel);
		frame.add(splitPane);*/
		frame.add(leftPanel.getPanel());
	}
	
}
