package ashwin.frame;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Frame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JButton CBtn;
	JButton PBtn;
	JButton SBtn;
	Robot robocop;
	String savePath;
	File defDir = new File(System.getProperty("user.home") + "\\Documents\\Memory");
	String lastSavedPath;
	String lastSavedFile;
	String timeFormatted;

	public Frame() {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.setResizable(false);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setTitle("memory");
		this.setDefaultCloseOperation(3);
		this.setBounds(100, 100, 70, 110);
		(this.contentPane = new JPanel()).setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		final JLabel Label = new JLabel("memory");
		this.contentPane.add(Label);
		Label.setFont(new Font("Courier New", Font.PLAIN, 20));
		Label.setBounds(35, 0, 418, 20);
		this.CBtn = new JButton("memorize");
		this.contentPane.add(this.CBtn);
		this.CBtn.addActionListener(this);
		this.CBtn.setBounds(5, 25, 122, 23);
		this.PBtn = new JButton("+");
		this.contentPane.add(this.PBtn);
		this.PBtn.addActionListener(this);
		this.PBtn.setBounds(97, 50, 30, 23);
		this.SBtn = new JButton("share");
		this.contentPane.add(this.SBtn);
		this.SBtn.addActionListener(this);
		this.SBtn.setBounds(5, 50, 90, 23);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == PBtn) {
			this.debug("PBtn called.");
			JFileChooser pickFile = new JFileChooser();
			savePath = defDir.toString();
			if (defDir.exists() && defDir.isDirectory()) {
				pickFile.setCurrentDirectory(defDir);
				pickFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (pickFile.showSaveDialog(getComponent(0)) == JFileChooser.APPROVE_OPTION) {
					this.setPath(pickFile.getSelectedFile().getPath().toString());
				}
			} else {
				defDir.mkdir();
				this.debug("created directory '" + defDir + "'");
				pickFile.setCurrentDirectory(defDir);
				pickFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (pickFile.showSaveDialog(getComponent(0)) == JFileChooser.APPROVE_OPTION) {
					this.setPath(pickFile.getSelectedFile().getPath().toString());
				}
			}
		}
		if (e.getSource() == CBtn && savePath != null) {
			this.debug("CBtn called.");
			try {
				this.debug("saved to direcory " + savePath);
				Robocop(savePath);
				JOptionPane.showMessageDialog(new JFrame(), "saved memory.", "memory", JOptionPane.INFORMATION_MESSAGE);
			} catch (AWTException | IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(), "IOException occured", "memory", JOptionPane.ERROR_MESSAGE);
			}
		} else if (savePath == null && e.getSource() == CBtn) {
			this.debug("path is null.");
			savePath = defDir.toString();
			this.debug("using fall-back directory " + defDir);
			try {
				Robocop(savePath);
				this.debug("saved to fall-back directory");
				JOptionPane.showMessageDialog(new JFrame(), "saved memory.", "memory", JOptionPane.INFORMATION_MESSAGE);
			} catch (AWTException | IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(), "IOException occured", "memory", JOptionPane.ERROR_MESSAGE);
			}
		}

		if (e.getSource() == SBtn && lastSavedPath != null) {
			this.sendEmail(lastSavedFile, lastSavedPath, "ashwincharathsandran@gmail.com",
					"Memory from " + timeFormatted, "<html><p>Memory</p></html>");
		} else if (e.getSource() == SBtn && lastSavedPath == null) {
			DateFormat DateFSend = new SimpleDateFormat("yyyy,MM,dd [HH:m:ss]");
			Date date = new Date();
			timeFormatted = DateFSend.format(date).toString();
			JFileChooser pickFile = new JFileChooser();
			if (defDir.exists() && defDir.isDirectory()) {
				pickFile.setCurrentDirectory(defDir);
				if (pickFile.showSaveDialog(getComponent(0)) == JFileChooser.APPROVE_OPTION) {
					lastSavedPath = pickFile.getSelectedFile().getPath().toString();
					lastSavedFile = pickFile.getSelectedFile().getName();
					this.sendEmail(lastSavedFile, lastSavedPath, "ashwincharathsandran@gmail.com",
							"Memory from " + timeFormatted, "<html><p>Memory</p></html>");
				}
			} else {
				defDir.mkdir();
				this.debug("created directory '" + defDir + "'");
				pickFile.setCurrentDirectory(defDir);
				if (pickFile.showSaveDialog(getComponent(0)) == JFileChooser.APPROVE_OPTION) {
					lastSavedPath = pickFile.getSelectedFile().getPath().toString();
					lastSavedFile = pickFile.getSelectedFile().getName();
					this.sendEmail(lastSavedFile, lastSavedPath, "ashwincharathsandran@gmail.com",
							"Memory from " + timeFormatted, "<html><p>Memory</p></html>");
				}
			}
		}

	}

	public void Robocop(final String path) throws AWTException, IOException {
		this.debug("robocop has been called for service");
		DateFormat DateFSave = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		DateFormat DateFSend = new SimpleDateFormat("yyyy,MM,dd [HH:m:ss]");
		Date date = new Date();
		String currentT = DateFSave.format(date).toString();
		final Robot robocop = new Robot();
		final Rectangle screenDimensions = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		final BufferedImage screenshot = robocop.createScreenCapture(screenDimensions);
		ImageIO.write(screenshot, "png", new File(path + "/memory-" + currentT + ".png"));
		this.info("written memory to file 'memory-" + currentT + ".png'");
		lastSavedPath = savePath + java.io.File.separator + "memory-" + currentT + ".png";
		lastSavedFile = "memory-" + currentT + ".png";
		timeFormatted = DateFSend.format(date).toString();
		this.info("Last Saved Path" + lastSavedPath);
	}

	public void sendEmail(final String filename, String path, String reciver, String subject, String msg) {
		final String botEmail = "spadidchong@gmail.com";
		final String botPasskey = "3ND_L1NE_SPADIDCH0NG_IF-VOID_AVOID_LIST";
		Properties props = new Properties();
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(botEmail, botPasskey);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("MemoryBot@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciver));
			message.setSubject(subject);
			message.setText(msg);
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(path);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			this.debug("Processing Message");
			Transport.send(message);
			this.info("Sucess " + filename + " has been sent to " + reciver);
			JOptionPane.showMessageDialog(new JFrame(), "shared memory w/ " + reciver, "memory",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (MessagingException e) {
			System.out.println("WARNING:");
			e.printStackTrace();
		}
	}

	public void setPath(final String path) {
		savePath = path;
		this.info("path set to " + savePath);
	}

	public void debug(final String txt) {
		System.out.println("DEBUG: " + txt);
	}

	public void info(final String txt) {
		System.out.println("INFO: " + txt);
	}

}
