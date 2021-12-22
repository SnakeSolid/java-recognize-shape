package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ShapeModel {

	private final List<Point> values;

	private final List<Point> points;

	private Shape shape;

	public ShapeModel() {
		this.values = new ArrayList<>();
		this.points = new ArrayList<>();
		this.shape = Shape.NONE;
	}

	/**
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * @param shape
	 *            the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * @return the values
	 */
	public List<Point> getValues() {
		return values;
	}

	/**
	 * @return the points
	 */
	public List<Point> getPoints() {
		return points;
	}

}
