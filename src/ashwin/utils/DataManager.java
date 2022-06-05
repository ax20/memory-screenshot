package ashwin.utils;

import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ashwin.frame.LoginFrame;

public class DataManager {
	
	static String XORNonnativeData;
	static LoginFrame lgf;

	public static void saveData(final String url) {
		try {
			URL localURL = new URL(url);
			Scanner StreamData = new Scanner(localURL.openStream());
			XORNonnativeData = StreamData.nextLine();
			StreamData.close();
//			DataOutputStream DOS = new DataOutputStream(new FileOutputStream(dataFile));
//			DOS.writeUTF(XORNonnativeData);
//			DOS.flush();
//			DOS.close();
			
			DataManager.info("recieved data " + XORNonnativeData);
			
		} catch (MalformedURLException e) {
			DataManager.error("Invalid URL");
			JOptionPane.showMessageDialog(new JFrame(), "login form disabled.\n[url faliure]", "login form", JOptionPane.ERROR_MESSAGE);
			lgf.dispatchEvent(new WindowEvent(lgf, WindowEvent.WINDOW_CLOSING));
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			DataManager.error("IOException");
			JOptionPane.showMessageDialog(new JFrame(), "login form disabled.\n[IOException]", "login form", JOptionPane.ERROR_MESSAGE);
			lgf.dispatchEvent(new WindowEvent(lgf, WindowEvent.WINDOW_CLOSING));
		}
	}
	

	public static void checkClient(final File dataDirectory, File localDataPath) {
		if (dataDirectory.exists() && dataDirectory.isDirectory()) {
			DataManager.info("storage status: [1]");
			DataManager.info("filtering files");
			if (localDataPath.exists()) {
				localDataPath.delete();
				try {
					localDataPath.createNewFile();
				} catch (IOException e) {
					System.out.println(e.getStackTrace());
					DataManager.error("file filteration error");
					DataManager.error("storage status: [-1]");
				}
				DataManager.info("file filteration complete");
			}
		}
		else if (!dataDirectory.exists() && !dataDirectory.isDirectory()) {
			DataManager.info("storage status: [0]");
			DataManager.info("creating required directories");
			dataDirectory.mkdir();
			try {
				localDataPath.createNewFile();
			} catch (IOException e) {
				DataManager.error("file filteration error");
				DataManager.error("storage status: [-1]");
			}
			DataManager.info("storage status: [1]");
		}
	}

	public static void info(final String info) {
		System.out.println("[DATA] I: " + info);
	}

	public static  void error(final String info) {
		System.out.println("[DATA] [!]: " + info);
	}

	public static void debug(final String debug) {
		System.out.println("[DATA] D: " + debug);
	}
	
	public static String getNonnativeData() {
		return XORNonnativeData;
	}

}
