package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

/**
 * randomly places stars (1p white) and meteors (2p orange with cyan tail) in the sky
 * @author Morgan Vanderhei
 */
public class Main {
	private static final int MAXX = Toolkit.getDefaultToolkit().getScreenSize().width,
			MAXY = Toolkit.getDefaultToolkit().getScreenSize().height;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		JFrame j = new JFrame("Looking up at the sky...");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		StarCanvas c = new StarCanvas((screenSize.height * screenSize.width)/3000, .01);
		j.add(c);
		j.setSize(MAXX, MAXY);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		j.setUndecorated(true);
		j.setVisible(true);
		j.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				System.exit(0);
			}
		});
		do {
			try {
				TimeUnit.MILLISECONDS.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			c.move();
		} while (true);
	}

	private static class StarCanvas extends Component {
		private static final long serialVersionUID = 4479948873591268638L;
		private ArrayList<Point> stars = new ArrayList<Point>();
		private ArrayList<Point> farStars = new ArrayList<Point>();
		private ArrayList<Meteor> meteors = new ArrayList<Meteor>();
		private int numStars;
		private double meteorChance;

		public StarCanvas(int numStars, double meteorChance) {
			this.numStars = numStars;
			this.meteorChance = meteorChance;
			while (stars.size() < numStars) {
				stars.add(new Point(new Random().nextInt(MAXX), new Random().nextInt(MAXY)));
				farStars.add(new Point(new Random().nextInt(MAXX), new Random().nextInt(MAXY)));
			}
		}

		public void paint(Graphics g2) {
			Graphics2D g = (Graphics2D) g2;
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, MAXX, MAXY);
			for (int i = 0; i < numStars; i++) {
				g.setColor(Color.WHITE);
				g.fillRect(stars.get(i).getX(), stars.get(i).getY(), 1, 1);
				g.setColor(Color.DARK_GRAY);
				g.fillRect(farStars.get(i).getX(), farStars.get(i).getY(), 1, 1);
			}
			for (Meteor m : meteors.toArray(new Meteor[0])) {
				g.setColor(Color.GRAY);
				g.drawLine(m.getX(), m.getY(), m.getX() - m.vx * 2, m.getY() - m.vy * 2);
				g.setColor(Color.ORANGE);
				g.fillRect(m.getX(), m.getY(), 2, 2);
			}
		}

		public void move() {
			for (int i = 0; i < numStars; i++) {
				stars.get(i).addX(1);
				farStars.get(i).addX(1);
			}
			for (Meteor m : meteors) {
				m.addX(m.vx);
				m.addY(m.vy);
			}
			if (Math.random() < meteorChance) {
				meteors.add(new Meteor(3, (int) (Math.random() * MAXY)));
			}
			checkPoints();
			while (stars.size() < numStars) {
				stars.add(new Point(new Random().nextInt(3)-3, new Random().nextInt(MAXY)));
			}
			while (farStars.size() < numStars) {
				farStars.add(new Point(new Random().nextInt(3)-3, new Random().nextInt(MAXY)));
			}
			repaint();
		}

		private void checkPoints() {
			for (int i = 0; i < stars.size(); i++) {
				if (stars.get(i).getX() > MAXX || stars.get(i).getY() > MAXY) {
					stars.remove(i);
					i--;
				}
			}
			for (int i=0;i<farStars.size();i++) {
				if (farStars.get(i).getX() > MAXX || farStars.get(i).getY() > MAXY) {
					farStars.remove(i);
					i--;
				}
			}
			for (int i=0;i<meteors.size();i++) {
				if (meteors.get(i).getX() > (MAXX+meteors.get(i).vx*2) || meteors.get(i).getY() > (MAXY+meteors.get(i).vy*2) || meteors.get(i).getY() < (meteors.get(i).vy*2)) {
					meteors.remove(i);
					i--;
				}
			}
		}
	}

	private static class Point {
		private int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void addX(int x) {
			this.x += x;
		}

		public void addY(int y) {
			this.y += y;
		}

		public String toString() {
			return "[" + x + "," + y + "]";
		}
	}

	private static class Meteor extends Component {
		private static final long serialVersionUID = 1641489264636149100L;
		private Point coords;
		private int vx, vy;

		public Meteor(int x, int y) {
			coords = new Point(x, y);
			vx = ((int) (new Random().nextDouble() * 5 + 3) +3);
			vy = (int) (new Random().nextDouble() * 5 - 2.5);
		}
		public int getX() {return coords.getX();}
		public int getY() {return coords.getY();}
		public void addX(int x) {
			coords.addX(x);
		}

		public void addY(int y) {
			coords.addY(y);
		}
	}
}
