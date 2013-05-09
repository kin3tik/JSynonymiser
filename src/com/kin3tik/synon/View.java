package com.kin3tik.synon;

import java.awt.EventQueue;
import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import java.awt.Toolkit;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
//import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.KeyAdapter;

public class View {

	@SuppressWarnings("unused")
	private final boolean DEBUG = true;
	private String version = "20130504";
	
	private JFrame frmSynonymiser;
	
	private HashMap<String, ArrayList<String>> dict;
	private APIHandler api;
	private FileHandler file;
	
	private JTextArea textArea_output;
	private JTextArea textArea_input;
	private JProgressBar progressBar;
	private JMenuBar menuBar;
	private JMenuItem menuItemAbout;
	private JMenuItem menuItemIgnore;
	private JMenu mnMenu;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View window = new View();
					window.frmSynonymiser.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public View() {
		file = new FileHandler();
		dict = file.loadDict();
		api = new APIHandler(this, dict);
		api.setWordsToIgnore(file.loadIgnoreList());
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frmSynonymiser = new JFrame();
		frmSynonymiser.setIconImage(Toolkit.getDefaultToolkit().getImage(View.class.getResource("/com/kin3tik/synon/symbol.png")));
		frmSynonymiser.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				file.saveDict(dict);
				file.saveIgnoreList(api.getWordsToIgnore());
			}
		});
		frmSynonymiser.setTitle("Synonymiser");
		frmSynonymiser.setBackground(Color.WHITE);
		frmSynonymiser.setBounds(100, 100, 350, 300);
		frmSynonymiser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSynonymiser.getContentPane().setLayout(new MigLayout("", "[180.00,grow][180.00px,grow]", "[110.00px,grow][][]"));
		
		JScrollPane scrollPane_input = new JScrollPane();
		frmSynonymiser.getContentPane().add(scrollPane_input, "cell 0 0,grow");
		
		textArea_input = new JTextArea();
//		textArea_input.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent evt) {
//				if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
//					textArea_output.setText("Loading...");
//					api.setInput(getInputText().trim());
//					
//					Thread worker = new Thread(api);
//				    worker.start();
//				}
//			}
//		});
		textArea_input.setWrapStyleWord(true);
		textArea_input.setLineWrap(true);
		scrollPane_input.setViewportView(textArea_input);
		
		JScrollPane scrollPane_output = new JScrollPane();
		frmSynonymiser.getContentPane().add(scrollPane_output, "cell 1 0,grow");
		
		textArea_output = new JTextArea();
		textArea_output.setWrapStyleWord(true);
		textArea_output.setLineWrap(true);
		textArea_output.setEditable(false);
		scrollPane_output.setViewportView(textArea_output);
		
		JButton btnSynon = new JButton("Synonymise");
		btnSynon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				textArea_output.setText("Loading...");
				api.setInput(getInputText());
				
				Thread worker = new Thread(api);
			    worker.start();
			}
		});
		frmSynonymiser.getContentPane().add(btnSynon, "cell 0 1 2 1,growx");
		
		progressBar = new JProgressBar();
		frmSynonymiser.getContentPane().add(progressBar, "cell 0 2 2 1,growx");
		
		menuBar = new JMenuBar();
		frmSynonymiser.setJMenuBar(menuBar);
		
		mnMenu = new JMenu("Options");
		menuBar.add(mnMenu);
		
		menuItemAbout = new JMenuItem("About");
		mnMenu.add(menuItemAbout);
		
		menuItemIgnore = new JMenuItem("Edit Ignore List");
		mnMenu.add(menuItemIgnore);
		menuItemIgnore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Preferences pref = new Preferences(api);
				pref.setVisible(true);
			}
		});
		menuItemAbout.addActionListener(new ActionListener() {
			//shows a dialog with "about" information
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(menuItemAbout,
						"Version: "+version+"\n"+
					    "Made by James White\n"+
						"jameskvwhite.com\n\n"+
						"Project uses Resty:\n"+
						"beders.github.io/Resty/",
					    "About",
					    JOptionPane.INFORMATION_MESSAGE
				);
			}
		});
	}
	
	public void setOutputText(String s) {
		this.textArea_output.setText(s);
	}
	
	public String getInputText() {
		return this.textArea_input.getText();
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
}