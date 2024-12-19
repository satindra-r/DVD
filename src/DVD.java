import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class DVD {
	VideoCapture capture;

	boolean face = false;
	JFrame jframe;
	JLabel background;
	BufferedImage maze;
	ImageIcon dvdlogo;
	ImageIcon dvdlogoc;
	Color[] c;
	int colorpos = 0;
	Timer t;
	int tx;
	int ty;
	int ax;
	int ay;
	Point dvdloc;
	Point dvddir;
	int bodgedist = 100;
	Graphics g;
	Robot r;
	//String path = "./resources/";
	String path = "C:/Users/RamS/Desktop/DVD/resources/";
	public void render() {
		g.fillRect(0, 0, tx, ty);
		g.drawImage(dvdlogoc.getImage(), dvdloc.x, dvdloc.y, background);
		background.setIcon(new ImageIcon(maze));
	}

	public void compute() {
		dvdloc.translate(dvddir.x, dvddir.y);
		boolean sidex = false;
		boolean sidey = false;
		boolean corner = false;
		;
		if (dvdloc.x > ax || dvdloc.x < 0) {
			dvddir.x *= -1;
			sidex = true;
		}
		if (dvdloc.y > ay || dvdloc.y < 0) {
			dvddir.y *= -1;
			sidey = true;
		}
		if (sidex || sidey) {
			if (sidex && sidey) {
				if ((ax - dvdloc.x == ay - dvdloc.y) || (dvdloc.x == dvdloc.y) || (dvdloc.x == ay - dvdloc.y)
						|| (ax - dvdloc.x == dvdloc.y)) {
					corner = true;
				}
			}
			BufferedImage dvdcolor = new BufferedImage(dvdlogo.getIconWidth(), dvdlogo.getIconHeight(),
					BufferedImage.TYPE_3BYTE_BGR);
			Graphics gcol = dvdcolor.createGraphics();
			gcol.drawImage(dvdlogo.getImage(), 0, 0, null);
			gcol.dispose();
			int newcolorpos = colorpos;
			while (newcolorpos == colorpos) {
				newcolorpos = (int) (Math.random() * 6);
			}
			for (int i = 0; i < dvdlogo.getIconWidth(); i++) {
				for (int j = 0; j < dvdlogo.getIconHeight(); j++) {
					if (new Color(dvdcolor.getRGB(i, j)).equals(Color.white)) {
						dvdcolor.setRGB(i, j, c[newcolorpos].getRGB());
					}
				}
			}
			colorpos = newcolorpos;
			dvdlogoc = new ImageIcon(dvdcolor);
			if (face) {
				if ((dvddir.x > 0 && dvddir.y > 0 && ax - dvdloc.x == ay - dvdloc.y)
						|| (dvddir.x < 0 && dvddir.y < 0 && dvdloc.x == dvdloc.y)
						|| (dvddir.x > 0 && dvddir.y < 0 && ax - dvdloc.x == dvdloc.y)
						|| (dvddir.x < 0 && dvddir.y > 0 && dvdloc.x == ay - dvdloc.y)) {
					if (Math.random() < 0.5) {
						dvdloc.x += 1;
					} else {
						dvdloc.x -= 1;
					}

					//System.out.println("hehe");
				}
			} else {
				if ((dvddir.x > 0 && dvddir.y > 0 && Math.abs((ax - dvdloc.x) - (ay - dvdloc.y)) < bodgedist)) {
					dvdloc.x = ax - ay + dvdloc.y;
				}

				if ((dvddir.x < 0 && dvddir.y < 0 && Math.abs(dvdloc.x - dvdloc.y) < bodgedist)) {
					dvdloc.x = dvdloc.y;
				}

				if ((dvddir.x > 0 && dvddir.y < 0 && Math.abs((ax - dvdloc.x) - dvdloc.y) < bodgedist)) {
					dvdloc.x = ax - dvdloc.y;
				}

				if ((dvddir.x < 0 && dvddir.y > 0 && Math.abs(dvdloc.x - (ay - dvdloc.y)) < bodgedist)) {
					dvdloc.x = ay - dvdloc.y;
				}

			}
			AudioInputStream audioIn;
			try {
				if (corner) {
					audioIn = AudioSystem.getAudioInputStream(new File(path + "corner.wav"));
				} else {
					audioIn = AudioSystem.getAudioInputStream(new File(path + "edge.wav"));
				}
				Clip clip = AudioSystem.getClip();
				clip.open(audioIn);
				clip.start();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		tx = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		ty = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		dvdloc = new Point(0, 0);
		dvddir = new Point(-1, -1);
		dvdlogo = new ImageIcon(path + "dvd.png");
		ax = tx - dvdlogo.getIconWidth();
		ay = ty - dvdlogo.getIconHeight();
		Color[] cinit = { Color.red, Color.blue, Color.green, Color.cyan, Color.magenta, Color.yellow };
		c = cinit;
		maze = new BufferedImage(tx, ty, BufferedImage.TYPE_3BYTE_BGR);
		g = maze.getGraphics();
		g.setColor(new Color(0, 0, 0));

		jframe = new JFrame("DVD");
		jframe.setUndecorated(true);
		jframe.setBackground(new Color(0, 0, 0));

		background = new JLabel();
		background.setBounds(0, 0, tx, ty);

	    System.setProperty("java.library.path", "C:/path/to/opencv/build/java/x64");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String xmlFile = path + "haarcascade_frontalface_default.xml";
		CascadeClassifier faceCascade = new CascadeClassifier(xmlFile);

		if (faceCascade.empty()) {
			System.out.println("Error: Could not load classifier file.");
		}

		capture = new VideoCapture(0);

		if (!capture.isOpened()) {
			System.out.println("Error: Could not open video capture device.");
			return;
		}

		Mat frame = new Mat();
		t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				capture.read(frame);

				if (frame.empty()) {
					System.out.println("Error: Empty frame captured.");
				} else {
					Mat grayFrame = new Mat();
					Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

					MatOfRect faceDetections = new MatOfRect();
					faceCascade.detectMultiScale(grayFrame, faceDetections, 1.1, 5, 0, new Size(30, 30), new Size());

					 face = faceDetections.toArray().length > 0;
					//face = true;
					// System.out.println(face);
				}
			}
		}, 0, 1000);

		t.schedule(new TimerTask() {
			public void run() {
				compute();
				render();
			}
		}, 10, 10);
		try {
			r=new Robot();
		} catch (AWTException e) {
		}
		t.schedule(new TimerTask() {
			public void run() {
				r.keyPress(KeyEvent.VK_F23);
				r.keyRelease(KeyEvent.VK_F23);
			}
		}, 60000, 60000);

		jframe.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
				t.purge();
				capture.release();
				System.exit(0);
			}
		});

		jframe.add(background);
		jframe.setLayout(null);
		jframe.setResizable(false);
		jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jframe.setAlwaysOnTop(true);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		DVD dvd = new DVD();
		dvd.init();
	}
}
