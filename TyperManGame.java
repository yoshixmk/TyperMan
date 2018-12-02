import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
public class TyperManGame extends JPanel implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField currentString;
	private JTextArea pointBox;
	private ArrayList<ZhJpWordItem> bank;
	private ArrayList<FallingWord> wordsOnBoard;
	private int points;
	private Timer time;
	private int currentTime;
	private int difficulty;
	/**
	 * current string height
	 */
	private static final int CS_HEIGHT = 30;

	public TyperManGame() throws FileNotFoundException {
		setSize(Constants.FULL_WIDTH, Constants.FULL_HEIGHT);
		setLayout(null);
		bank = ZhJpDictionary.getWords("words.txt");
		setBackground(Color.WHITE);
		currentString = new JTextField("");
		currentString.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendString();
			}

		});
		currentString.setSize(Constants.FULL_WIDTH, CS_HEIGHT);
		currentString.setLocation(0, Constants.FULL_HEIGHT - 70);
		currentString.setBackground(Color.BLUE);
		currentString.setEditable(true);
		currentString.setForeground(Color.white);
		currentString.setFont(currentString.getFont().deriveFont(20f)); 

		pointBox = new JTextArea("");
		pointBox.setEditable(false);
		pointBox.setSize(60,30);
		pointBox.setBackground(Color.BLACK);
		pointBox.setForeground(Color.white);
		pointBox.setLocation(0, 0);
		
		add(pointBox);
		add(currentString);
		setVisible(true);
		time = new Timer(100, this);
		startNewGame();
	}
	
	public void startNewGame() {
		points = 0;
		currentTime = 0;
		wordsOnBoard = new ArrayList<FallingWord>();
		difficulty = 0;
		time.start();
	}
	
	public void sendString() {
		String entry = currentString.getText();
		if(wordIsOnBoard(entry)) {
			currentString.setText("");
			points = points + entry.length() + difficulty;
			pointBox.setText(""+points);
			removeWord(entry);
			updateUI();
		}
	}

	public boolean wordIsOnBoard(String entry) {
		java.util.Iterator<FallingWord> it = wordsOnBoard.iterator();
		while(it.hasNext()) {
			FallingWord current = it.next();
			if(equalsAmbitious(current.getNormalizedSubWord(), entry)) {
				return true;
			}
		}
		return false;
	}

	private void removeWord(String entry) {
		java.util.Iterator<FallingWord> it = wordsOnBoard.iterator();
		boolean found = false;
		while(it.hasNext() && !found) {
			FallingWord current = it.next();
			remove(current.box);
			it.remove();
			found = true;
		}
	}
	
	private boolean equalsAmbitious(String answer, final String entry) {
		// lower and remove spaces
		final String a = answer.toLowerCase().replaceAll("[\\s　]", "");
		final String b = entry.toLowerCase().replaceAll("[\\s　]", "");

		// all match
		if (a.equals(b)) {
			return true;
		}
		// specific end char remove match
		if (a.endsWith(".") || a.endsWith("?") || a.endsWith("!")) {
			return a.substring(0, a.length()-1).equals(b);
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		currentTime++;
		moveAllDown();
		if(collison()) {
			endGame();
		}
		adjustDifficulty();
	}
	
	private void adjustDifficulty() {
		int wordFrequency = 40 - (difficulty*2)/5+1;
		if(wordFrequency < 4) {
			wordFrequency = 4;
		}
		if(currentTime % wordFrequency == 0) {
			difficulty++;
			makeNewWord();
		}
	}

	private void makeNewWord() {
		ZhJpWordItem randomWord = getRandomWord();
		FallingWord newWord = new FallingWord(randomWord.getWord(), randomWord.getMean(), randomWord.getPinyin(), 3);
		wordsOnBoard.add(newWord);
	}

	private void endGame() {
		time.stop();
	}

	public boolean collison() {
		java.util.Iterator<FallingWord> it = wordsOnBoard.iterator();
		while(it.hasNext()) {
			FallingWord current = it.next();
			if(current.atBottom()) {
				return true;
			}
		}
		return false;
	}


	private void moveAllDown() {
		java.util.Iterator<FallingWord> it = wordsOnBoard.iterator();
		while(it.hasNext()) {
			FallingWord current = it.next();
			current.updateBox();
		}
		updateUI();
	}

	private ZhJpWordItem getRandomWord() {
		Random ran = new Random();
		int randomIndex = ran.nextInt(bank.size());
		return bank.get(randomIndex);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		;
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		;
	}

	@Override
	public void keyTyped(KeyEvent key) {
		
		
	}
	
	private class FallingWord {
		
		private String word;
		private String mean;
		private String subWord;
		private JTextArea box;
		private int boxVel;
		private int xLoc;
		private int yLoc;

		private static final int FALL_WORD_HEIGHT = 50;
		
		public FallingWord(String word, String mean, String subWord, int boxVel) {
			Random ran = new Random();
			this.word = word;
			this.mean = mean;
			this.subWord = subWord;
			xLoc = ran.nextInt(Constants.FULL_WIDTH) - calcBoxWidth();
			if (xLoc < 0) {
				xLoc = 0;
			}
			yLoc = 0;
			this.boxVel = boxVel;
			createBox();
		}
		
		public boolean atBottom() {
			// height: end of time, look for miss word
			if (yLoc >= Constants.FULL_HEIGHT - FALL_WORD_HEIGHT * 2 - CS_HEIGHT) {
				return true;
			} else {
				return false;
			}
		}
		
		public String getNormalizedSubWord() {
			return subWord
					.replaceAll("[aāáǎà]", "a")
					.replaceAll("[eēéěè]", "e")
					.replaceAll("[iīíǐì]", "i")
					.replaceAll("[oōóǒò]", "o")
					.replaceAll("[uūúǔùüǖǘǚǜ]", "u");
		}

		public void updateBox() {
			yLoc = yLoc + boxVel;
			box.setLocation(xLoc, yLoc);
			if (yLoc > Constants.HALF_HEIGHT) {
				box.setForeground(Color.white);
				box.setBackground(Color.red);
			} else if (yLoc > Constants.QUARTER_HEIGHT) {
				box.setBackground(Color.yellow);
			}
		}

		public void createBox() {
			box = new JTextArea(String.format("%s\n%s\n%s", word, subWord, mean));
			box.setLocation(xLoc, yLoc);
			box.setSize(calcBoxWidth(), FALL_WORD_HEIGHT);
			box.setBackground(Color.GREEN);
			add(box);
		}

		private int calcBoxWidth() {
			final String maxLengthValue = Arrays.asList(word, subWord, mean).stream()
					.max(Comparator.comparingInt(String::length)).get();
			return 8 * maxLengthValue.length() + 10;
		}
	}

}

