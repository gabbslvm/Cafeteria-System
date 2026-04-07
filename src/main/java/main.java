
import userinterface.MainFrame;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame login = new MainFrame();
            login.setVisible(true);
        });
    }
}