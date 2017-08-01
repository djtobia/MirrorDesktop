import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;

public class Interface {

	private JFrame frame;
	private JButton start, end;
	private Timer timer;
	private Webcam webcam;
	private Dimension myDim;
	private Changer myChanger;
	private SystemTray systemTray;
	private TrayIcon trayIcon;
	private BufferedImage trayImage;
	private PopupMenu popUp;
	private boolean isStarted;
	public Interface() {

		isStarted = false;
		myChanger = new Changer();
		frame = new JFrame();
		frame.setSize(500, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("Mirror Desktop");
		frame.addWindowListener(new MyWindowListener());

		end = new JButton("Stop");
		end.setSize(100, 30);
		end.setLocation(300, 75);
		frame.add(end);
		end.addActionListener(new EndMirrorListener());
		
		start = new JButton("Start");
		start.setSize(100, 30);
		frame.add(start);
		start.setLocation(75, 75);
		start.addActionListener(new StartMirrorListener());

		

		setUpWebcam();
		
		systemTray = SystemTray.getSystemTray();
		try {
			trayImage = ImageIO.read(new File("camera-icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		popUp = new PopupMenu();
		trayIcon = new TrayIcon(trayImage, "Mirror Desktop", popUp);
		setUpMenu();

	}

	public void start(){
		frame.setVisible(true);
	}

	private void setUpMenu() {
		MenuItem item1 = new MenuItem("Open");
		item1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				systemTray.remove(trayIcon);
				int state = frame.getExtendedState();
				state = state & ~JFrame.ICONIFIED;
				frame.setExtendedState(state);
				frame.setVisible(true);
				
			}
		});
		popUp.add(item1);
	}

	private void setUpWebcam() {
		webcam = Webcam.getDefault();

		if (webcam == null) {
			System.out.println("No webcam detected");
			System.exit(-1);
		}
		myDim = new Dimension(640, 480);
		webcam.setViewSize(myDim);

	}

	private class StartMirrorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!isStarted){
			System.out.println("Starting mirror");
			
			webcam.open();
			timer = new Timer();
			
			TimerTask task1 = new TimerTask() {
				@Override
				public void run() {

					
					BufferedImage image = webcam.getImage();
					// save image to PNG file
					try {
						ImageIO.write(image, "PNG", new File("image.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String wallpaper_file = System.getProperty("user.dir")+"/image.png";
					myChanger.Change(wallpaper_file);
				}
			};

			timer.schedule(task1, 1000, 150);
			isStarted = true;
		}
		}
	}



	private class EndMirrorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			timer.cancel();
			webcam.close();
			isStarted =false;
		}

	}

	private class MyWindowListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			int state = frame.getExtendedState();
			state = state & ~JFrame.ICONIFIED;
			frame.setExtendedState(state);
			frame.setVisible(true);
			systemTray.remove(trayIcon);
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			frame.setVisible(false);
			frame.setExtendedState(JFrame.ICONIFIED);
			try {
				systemTray.add(trayIcon);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
