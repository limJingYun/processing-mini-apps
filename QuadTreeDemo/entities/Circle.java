package QuadTreeDemo.entities;

public class Circle extends Ellipse {

	public Circle(double x, double y, double radius) {
		super(x, y, x + 2 * radius, y + 2 * radius);
	}

}
