package QuadTreeDemo.util;

import QuadTreeDemo.entities.Entity;
import QuadTreeDemo.entities.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class QuadTree {

	private int maxObjects;
	private int maxLevels;

	private int level;
	private List<Entity> objects;
	private Entity bounds;
	private QuadTree[] nodes;

	/*
	 * Constructor
	 */
	public QuadTree(int pLevel, Entity pBounds) {
		level = pLevel;
		objects = new ArrayList<Entity>();
		bounds = pBounds;
		nodes = new QuadTree[4];

		this.maxLevels = 5;
		this.maxObjects = 10;
	}

	public QuadTree(int pLevel, Entity pBounds, int maxObjects, int maxLevels) {
		level = pLevel;
		objects = new ArrayList<Entity>();
		bounds = pBounds;
		nodes = new QuadTree[4];

		this.maxLevels = maxLevels;
		this.maxObjects = maxObjects;
	}

	/*
	 * Clears the QuadTree
	 */
	public void clear() {
		objects.clear();

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	/*
	 * Splits the node into 4 subnodes
	 */
	private void split() {
		int subWidth = (int) (bounds.getWidth() / 2);
		int subHeight = (int) (bounds.getHeight() / 2);
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();

		nodes[1] = new QuadTree(level + 1, new Rectangle(x + subWidth, y,
				subWidth, subHeight), maxObjects, maxLevels);
		nodes[0] = new QuadTree(level + 1, new Rectangle(x, y, subWidth,
				subHeight), maxObjects, maxLevels);
		nodes[3] = new QuadTree(level + 1, new Rectangle(x, y + subHeight,
				subWidth, subHeight), maxObjects, maxLevels);
		nodes[2] = new QuadTree(level + 1, new Rectangle(x + subWidth, y
				+ subHeight, subWidth, subHeight), maxObjects, maxLevels);
	}

	/*
	 * Determine which node the object belongs to. -1 means object cannot
	 * completely fit within a child node and is part of the parent node
	 */
	private int getIndex(Entity pRect) {
		int index = -1;
		double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect
				.getY() + pRect.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (pRect.getX() < verticalMidpoint
				&& pRect.getX() + pRect.getWidth() < verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}
		// Object can completely fit within the right quadrants
		else if (pRect.getX() > verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		}

		return index;
	}

	private int getIndex(double x, double y) {
		int index = -1;

		if (level < maxLevels && nodes[0] != null) {
			double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
			double horizontalMidpoint = bounds.getY()
					+ (bounds.getHeight() / 2);
			if (x < verticalMidpoint) {
				if (y < horizontalMidpoint) {
					index = 0;
				} else if (y > horizontalMidpoint) {
					index = 3;
				}
			} else if (x > verticalMidpoint) {
				if (y < horizontalMidpoint) {
					index = 1;
				} else if (y > horizontalMidpoint) {
					index = 2;
				}
			}
		}

		return index;
	}

	/*
	 * Insert the object into the QuadTree. If the node exceeds the capacity, it
	 * will split and add all objects to their corresponding nodes.
	 */
	public void insert(Entity pRect) {
		if (nodes[0] != null) {
			int index = getIndex(pRect);

			if (index != -1) {
				nodes[index].insert(pRect);

				return;
			}
		}

		objects.add(pRect);

		if (objects.size() > maxObjects && level < maxLevels) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					nodes[index].insert(objects.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	/*
	 * Return all objects that could collide with the given object
	 */
	public List<Entity> retrieve(List<Entity> returnObjects, Entity pRect) {
		int index = getIndex(pRect);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, pRect);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	public List<Entity> retrieve(Entity pRect) {
		List<Entity> returnObjects = new ArrayList<Entity>();
		int index = getIndex(pRect);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, pRect);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	public List<Entity> retrieve(double x, double y) {
		List<Entity> returnObjects = new ArrayList<Entity>();
		int index = getIndex(x, y);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, x, y);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	public List<Entity> retrieve(List<Entity> returnObjects, double x, double y) {
		int index = getIndex(x, y);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, x, y);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}

	public boolean hasChildren() {
		if (level < maxLevels && nodes[0] != null) {
			return true;
		}
		return false;
	}

	public QuadTree[] getChildren() {
		return nodes;
	}

	public Entity getBounds() {
		return bounds;
	}

}