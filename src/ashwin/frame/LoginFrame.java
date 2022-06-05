package ashwin.frame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ashwin.utils.DataManager;
import ashwin.utils.XOR;

public class LoginFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton submitButton;
	private String databaseURL = "https://a.pomf.cat/uedsyz.xor";
	int maxOffenses = 3;
	int offenses;
	String XORNonnativeData;
	String XORLocalData;
	String loginCheck;
	File dataStorageDir = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\XOR-Login");
	File localDataPath = new File(dataStorageDir + "\\locale.xor");

	public LoginFrame() {
		this.setResizable(false);
		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setTitle("login form");
		this.setDefaultCloseOperation(3);
		this.setBounds(100, 100, 300, 250);
		(this.contentPane = new JPanel()).setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		final JLabel Label = new JLabel("enter credentials");
		this.contentPane.add(Label);
		Label.setFont(new Font("Courier New", Font.PLAIN, 20));
		Label.setBounds(50, 10, 418, 20);
		final JLabel LabelUsername = new JLabel("username");
		this.contentPane.add(LabelUsername);
		LabelUsername.setFont(new Font("Courier New", Font.PLAIN, 14));
		LabelUsername.setBounds(40, 40, 418, 20);
		this.usernameField = new JTextField();
		this.contentPane.add(this.usernameField);
		this.usernameField.setBounds(40, 60, 200, 20);
		this.usernameField.setColumns(10);
		final JLabel LabelPassword = new JLabel("password");
		this.contentPane.add(LabelPassword);
		LabelPassword.setFont(new Font("Courier New", Font.PLAIN, 14));
		LabelPassword.setBounds(40, 90, 418, 20);
		this.passwordField = new JPasswordField();
		this.contentPane.add(this.passwordField);
		this.passwordField.setBounds(40, 110, 200, 20);
		this.passwordField.setColumns(10);
		this.passwordField.setEchoChar('—');
		this.submitButton = new JButton("login");
		this.contentPane.add(this.submitButton);
		this.submitButton.addActionListener(this);
		this.submitButton.setBounds(100, 150, 89, 23);
		DataManager.checkClient(dataStorageDir, localDataPath);
		DataManager.saveData(databaseURL); // https://a.pomf.cat/emjulp.xor
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.submitButton && !String.valueOf(this.passwordField.getPassword()).isEmpty() && !this.usernameField.getText().isEmpty()) {
			
				String passTxt = String.valueOf(this.passwordField.getPassword());
				String userTxt = this.usernameField.getText().toString();
				String toFormat = userTxt + "*" + passTxt;

				XOR.writeFile(passTxt, toFormat, localDataPath);

				this.info("reading nonnative data");
				String NonDatamanager = DataManager.getNonnativeData();
				XOR.appendXORData(passTxt, NonDatamanager);
				XORNonnativeData = XOR.getDecryptedAppendData();
				
				this.info("reading local data");
				XOR.readFile(passTxt, localDataPath);
				XORLocalData = XOR.getDecryptedFileData();

				this.info("comparing data.");
				if (XORNonnativeData.contains(XORLocalData)) {
					
					this.info("login valid.");
					JOptionPane.showMessageDialog(new JFrame(), "valid authentication.\nwelcome " + userTxt, "login form",JOptionPane.INFORMATION_MESSAGE);
					new Frame();
					this.dispose();
				} else {
					offenses++;
					if (offenses < maxOffenses) {
						
						this.err("login invalid");
						JOptionPane.showMessageDialog(new JFrame(), "invalid authentication.", "login form", JOptionPane.ERROR_MESSAGE);
						this.usernameField.setText("");
						this.passwordField.setText("");
					}
					else if (offenses == maxOffenses || offenses > maxOffenses) {
						
						JOptionPane.showMessageDialog(new JFrame(), "too many invalid authentications", "login form", JOptionPane.ERROR_MESSAGE);
						offenses = 0;
						this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
					}
				}

		} else if (event.getSource() == this.submitButton && String.valueOf(this.passwordField.getPassword()).isEmpty() || this.usernameField.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(new JFrame(), "invalid fields", "login form", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void info(final String info) {
		System.out.println("[LOGIN] I: " + info);
	}

	public void err(final String info) {
		System.out.println("[LOGIN] [!]: " + info);
	}

	public void debug(final String debug) {
		System.out.println("[LOGIN] D: " + debug);
	}

}
