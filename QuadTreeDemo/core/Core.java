package QuadTreeDemo.core;

import QuadTreeDemo.entities.Entity;
import QuadTreeDemo.entities.Rectangle;
import QuadTreeDemo.util.QuadTree;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Core extends PApplet {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 800;
    private static final int FPS = 120;
    private double actualFps;
    private long fpsDrawingDelay = 1001;
    private long t;
    private QuadTree quadTree;
    private List<Entity> objects;
    private List<Entity> tempObjects;
    private int objectsNum = 500;
    private Random rng;
    private boolean shouldColor;
    private boolean drawingUserRect;
    private double userX;
    private double userY;

    @Override
    public void setup() {
        t = millis();
        surface.setTitle("QuadTreeDemo");
        rng = new Random();
        quadTree = new QuadTree(0, new Rectangle(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT), 5, 10);
        objects = new ArrayList<>();
        tempObjects = new ArrayList<>();
        for (int i = 0; i < objectsNum; i++) {
            int w = rng.nextInt(WINDOW_WIDTH / 32);
            int h = rng.nextInt(WINDOW_HEIGHT / 32);
            int x = rng.nextInt(WINDOW_WIDTH - w);
            int y = rng.nextInt(WINDOW_HEIGHT - h);
            Rectangle r = new Rectangle(x, y, w, h);
            r.setColor(100);
            objects.add(r);
            quadTree.insert(objects.get(i));
        }

        noStroke();

    }

    public void input() {
        double userX2;
        double userY2;
        shouldColor = mousePressed && mouseButton == 37;
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
        if (mousePressed && mouseButton == 37 && drawingUserRect) {
            drawingUserRect = false;
        }
    }

    public void update(long dt) {
        if (dt > 1000 / FPS) {
            t = millis();
        }

        for (int i = 0; i < objectsNum; i++) {
            objects.get(i).setColor(100);
        }
        surface.setTitle(actualFps + " QuadTreeDemo");
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
            surface.setTitle(actualFps
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

    @Override
    public void draw() {
        // update time stuff
        long now = millis();
        long dt = millis() - t;
        t = now;
        fpsDrawingDelay += dt;
        if (fpsDrawingDelay > 500) {
            actualFps = (int) (1000 / dt);
            fpsDrawingDelay = 0;
        }

        // handle input
        input();

        // update logic
        update(dt);

        // draw
        background(50);

        fill(70, 60, 60, 125);
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

    @Override
    public void mousePressed() {
        println(mouseButton);
    }

    @Override
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
            int w = rng.nextInt((int) (WINDOW_WIDTH / (Math.pow(2, scale))));
            int h = rng.nextInt((int) (WINDOW_HEIGHT / (Math.pow(2, scale))));
            int x = rng.nextInt(WINDOW_WIDTH - w);
            int y = rng.nextInt(WINDOW_HEIGHT - h);
            Rectangle r = new Rectangle(x, y, w, h);
            r.setColor(100);
            objects.add(r);
            objectsNum++;
        }
    }


    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"QuadTreeDemo.core.Core"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    @Override
    public void settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

}