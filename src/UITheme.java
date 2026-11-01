import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Central app-wide visual theme. Applying this once at startup (before any
 * window is created) re-skins every screen via Nimbus's UIManager defaults,
 * so individual forms don't need per-screen styling code.
 */
public class UITheme {
    public static final Color PRIMARY = new Color(0x13, 0x6F, 0x63);      // deep teal - primary actions
    public static final Color PRIMARY_DARK = new Color(0x0D, 0x4F, 0x47);  // pressed/hover
    public static final Color BG = new Color(0xF6, 0xF8, 0xF8);           // app/panel background
    public static final Color SURFACE = Color.WHITE;                      // fields/cards
    public static final Color TEXT = new Color(0x1E, 0x2A, 0x2A);
    public static final Color MUTED = new Color(0x5B, 0x6B, 0x6B);
    public static final Color DANGER = new Color(0xB3, 0x26, 0x1E);
    public static final Color BORDER = new Color(0xD9, 0xE2, 0xE1);

    public static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 20);
    public static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void apply() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            // Nimbus unavailable - keep the platform default look and feel.
            return;
        }

        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", new Color(0xDD, 0xE6, 0xE5));
        UIManager.put("control", BG);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusSelectionBackground", PRIMARY);
        UIManager.put("nimbusFocus", PRIMARY);
        UIManager.put("nimbusLightBackground", SURFACE);

        UIManager.put("defaultFont", BODY_FONT);
        UIManager.put("Button.font", LABEL_FONT);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("TextField.font", BODY_FONT);
        UIManager.put("PasswordField.font", BODY_FONT);
        UIManager.put("ComboBox.font", BODY_FONT);
        UIManager.put("Table.font", BODY_FONT);
        UIManager.put("TableHeader.font", LABEL_FONT);
        UIManager.put("Table.rowHeight", 26);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TabbedPane.font", LABEL_FONT);
        UIManager.put("Menu.font", BODY_FONT);
        UIManager.put("MenuItem.font", BODY_FONT);
    }
}
