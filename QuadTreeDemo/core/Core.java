package QuadTreeDemo.core;

import QuadTreeDemo.entities.Entity;
import QuadTreeDemo.entities.Rectangle;
import QuadTreeDemo.util.QuadTree;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Core extends PApplet {

	private static Core instance;
	private static final long serialVersionUID = 1L;
	private int width = 800, height = 800;
	private int fps = 120, actualfps;
	private long fpsDrawingDelay = 1001;
	private long t;
	private QuadTree quadTree;
	private List<Entity> objects;
	private List<Entity> tempObjects;
	private int objectsNum = 500;
	private Random rng;
	private boolean shouldColor;
	private boolean drawingUserRect;
	private double userX, userY, userX2, userY2;

	public void setup() {
		instance = this;
		t = millis();
		frame.setTitle("QuadTreeDemo");
		rng = new Random();
		quadTree = new QuadTree(0, new Rectangle(0, 0, width, height), 5, 10);
		objects = new ArrayList<Entity>();
		tempObjects = new ArrayList<Entity>();
		for (int i = 0; i < objectsNum; i++) {
			int w = rng.nextInt(width / 32);
			int h = rng.nextInt(height / 32);
			int x = rng.nextInt(width - w);
			int y = rng.nextInt(height - h);
			Rectangle r = new Rectangle(x, y, w, h);
			r.setColor(100);
			objects.add(r);
			quadTree.insert(objects.get(i));
		}

		noStroke();

	}

	public void input() {
		if (mousePressed && mouseButton == 37) {
			shouldColor = true;
		} else {
			shouldColor = false;
		}
		if (mousePressed && mouseButton == 39 && !drawingUserRect) {
			userX = mouseX;
			userY = mouseY;
			drawingUserRect = true;
		}
		if (!mousePressed && drawingUserRect) {
			userX2 = mouseX;
			userY2 = mouseY;
			drawingUserRect = false;

			objectsNum++;

			objects.add(new Rectangle(Math.min(userX, userX2), Math.min(userY,
					userY2), Math.max(userX, userX2) - Math.min(userX, userX2),
					Math.max(userY, userY2) - Math.min(userY, userY2)));
		}
		if(mousePressed&& mouseButton == 37&&drawingUserRect){
			drawingUserRect=false;
		}
	}

	public void update(long dt) {
		boolean update = false;
		if (dt > 1000 / fps) {
			update = true;
			t = millis();
		}
		if (update) {
		}

		for (int i = 0; i < objectsNum; i++) {
			objects.get(i).setColor(100);
		}
		frame.setTitle(actualfps + " QuadTreeDemo");
		tempObjects.clear();

		quadTree.clear();
		for (Entity e : objects) {
			quadTree.insert(e);
		}

		if (shouldColor) {
			List<Entity> entities = quadTree.retrieve(tempObjects, mouseX,
					mouseY);
			for (Entity e : entities) {
				e.setColor(250);
			}
			frame.setTitle(actualfps
					+ " QuadTreeDemo: Need to check collision with only "
					+ entities.size() + "/" + (objectsNum) + " objects.");
		} else {
			tempObjects.clear();
			for (Entity e : objects) {
				for (Entity e2 : quadTree.retrieve(tempObjects, e)) {
					if (e != e2 && e.collidesWith(e2)) {
						e.setColor(250);
						e2.setColor(250);
					}
				}
			}
		}

	}

	public void draw() {
		// update time stuff
		long now = millis();
		long dt = millis() - t;
		t = now;
		fpsDrawingDelay+=dt;
		if (fpsDrawingDelay > 500) {
			actualfps = (int) (1000 / dt);
			fpsDrawingDelay=0;
		}

		// handle input
		input();

		// update logic
		update(dt);

		// draw
		background(50);

		fill(70,60,60, 125);
		Entity e;
		for (int i = 0; i < objectsNum; i++) {
			stroke(objects.get(i).getColor());
			e = objects.get(i);
			rect((float) e.getX(), (float) e.getY(), (float) e.getWidth(),
					(float) e.getHeight());

		}

		drawQuadTree(quadTree);
		if (drawingUserRect) {
			noStroke();
			fill(160, 100, 150, 50);
			rect((float) Math.min(userX, mouseX),
					(float) Math.min(userY, mouseY),
					(float) (Math.max(userX, mouseX) - Math.min(userX, mouseX)),
					(float) (Math.max(userY, mouseY) - Math.min(userY, mouseY)));
		}
		noStroke();

	}

	private void drawQuadTree(QuadTree qt) {
		stroke(100, 150, 100);
		noFill();
		rect((float) qt.getBounds().getX(), (float) qt.getBounds().getY(),
				(float) qt.getBounds().getWidth(), (float) qt.getBounds()
						.getHeight());
		if (qt.hasChildren()) {
			for (QuadTree qtc : qt.getChildren()) {
				drawQuadTree(qtc);
			}
		}

	}

	public void mousePressed() {
		println(mouseButton);
	}

	public void mouseReleased() {
	}

	public void keyPressed() {
		println(keyCode);

		// R
		if (keyCode == 82) {
			objects.clear();
			objectsNum = 0;
		}

		// E
		if (keyCode == 69) {
			int scale = rng.nextInt(3) + 3;
			int w = rng.nextInt((int) (width / (Math.pow(2, scale))));
			int h = rng.nextInt((int) (height / (Math.pow(2, scale))));
			int x = rng.nextInt(width - w);
			int y = rng.nextInt(height - h);
			Rectangle r = new Rectangle(x, y, w, h);
			r.setColor(100);
			objects.add(r);
			objectsNum++;
		}
	}

	public void keyReleased() {
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "QuadTreeDemo.core.Core" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}

	public void settings() {
		size(width, height);
	}

	public static Core getInstance() {
		return instance;
	}
}