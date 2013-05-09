package com.kin3tik.synon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Toolkit;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Preferences extends JDialog {

	private JPanel contentPanel = new JPanel();
	private APIHandler api;
	private ArrayList<String> wordsToIgnore;
	private JTextField textField;
	private JTextArea textWordList;
	
	/**
	 * Create the dialog.
	 */
	public Preferences(APIHandler api) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Preferences.class.getResource("/com/kin3tik/synon/symbol.png")));
		setTitle("Preferences");
		this.api = api;
		this.wordsToIgnore = api.getWordsToIgnore();
		
		setBounds(100, 100, 400, 225);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[117.00,grow][grow][17.00]", "[][][grow]"));
		{
			JLabel lblWord = new JLabel("Word to Add/Remove:");
			contentPanel.add(lblWord, "cell 0 0,alignx left");
		}
		{
			textField = new JTextField();
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
						buttonAction();
					}
				}
			});
			contentPanel.add(textField, "cell 1 0,growx");
			textField.setColumns(10);
		}
		{
			JButton btnAdd = new JButton("Add/Remove");
			btnAdd.addActionListener(new ActionListener() {
				//add a new word to the list and redraw the wordList display
				public void actionPerformed(ActionEvent e) {
					buttonAction();				
				}
			});
			
			contentPanel.add(btnAdd, "cell 2 0,alignx center");
		}
		{
			JLabel lblWordList = new JLabel("Current List of Words to Ignore:");
			contentPanel.add(lblWordList, "cell 0 1 3 1,alignx center");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 0 2 3 1,grow");
			{
				textWordList = new JTextArea();
				textWordList.setEditable(false);
				textWordList.setWrapStyleWord(true);
				textWordList.setLineWrap(true);
				scrollPane.setViewportView(textWordList);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final Preferences ref = this;
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						//pass ignore list back to api handler and close the window
						getAPIHandler().setWordsToIgnore(getWordsToIgnore());
						ref.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		//show the current word list in the text box
		populateList();
	}
	
	/**
	 * Provides functionality for add/remove button
	 * If the word within the text field is not in the ignore list
	 * it adds it, if it is already in the ignore list it removes it.
	 */
	private void buttonAction() {
		String s = textField.getText().trim();
		if(getWordsToIgnore().contains(s)) {
			//remove
			getWordsToIgnore().remove(s);
		} else {
			//add
			getWordsToIgnore().add(s);
		}
		populateList();	
	}
	
	/**
	 * Populates the text field with the contents of the wordsToIgnore list
	 */
	private void populateList() {
		if(wordsToIgnore.isEmpty()){
			textWordList.setText("");
		} else {
			textWordList.setText(wordsToIgnore.toString());
		}
	}

	private ArrayList<String> getWordsToIgnore() {
		return this.wordsToIgnore;
	}
	
	private APIHandler getAPIHandler() {
		return this.api;
	}
}
