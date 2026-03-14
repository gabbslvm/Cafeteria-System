
import userinterface.LoginFrame;
import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
        // Run the UI on the Event Dispatch Thread (good Swing practice)
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}