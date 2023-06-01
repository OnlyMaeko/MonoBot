import javax.swing.*;
import java.awt.*;

public class GameGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel boardLabel;

    public GameGUI() {

        frame = new JFrame("Monopoly Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1024);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        ImageIcon boardImageIcon = new ImageIcon("./files/board.jpg");
        Image originalImage = boardImageIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(800, 800, Image.SCALE_SMOOTH);
        boardImageIcon = new ImageIcon(scaledImage);

        boardLabel = new JLabel(boardImageIcon);
        mainPanel.add(boardLabel, BorderLayout.CENTER);

        Game game = new Game();
		game.startGame();
	
		for (int i = 0; i < 50; i++) {
			for (Player player : game.getPlayers()) {
				game.playTurn(player, 0);
				System.out.println(player.getMoneyAmount());
			}
		}

		for (Player player : game.getPlayers()) {
			System.out.println(player.getMoneyAmount());
		}

        frame.add(mainPanel);

    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gameGUI = new GameGUI();
            gameGUI.show();
        });
    }
}