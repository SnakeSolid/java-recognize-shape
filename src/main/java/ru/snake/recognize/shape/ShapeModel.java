package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ShapeModel {

	private final List<Point> values;

	private Shape shape;

	public ShapeModel() {
		this.values = new ArrayList<>();
		this.shape = Shape.none();
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

}
