import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

public class TyperManPanel extends JFrame {

	private static final long serialVersionUID = 1L;
	private TyperManGame game;
	
	public TyperManPanel() throws FileNotFoundException {
		setSize(Constants.FULL_WIDTH, Constants.FULL_HIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		setLocation(gd.getDisplayMode().getWidth()/4,gd.getDisplayMode().getHeight()/4);
		game = new TyperManGame();
		setContentPane(game);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		new TyperManPanel();
	}
}
