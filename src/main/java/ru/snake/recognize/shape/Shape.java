package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.List;

public class Shape {

	private final ShapeType shapeType;

	private List<Point> polygonPoints;

	private Point ellipseCenter;

	private int ellipseWidth;

	private int ellipseHeight;

	private double ellipseAngle;

	private Shape(
		ShapeType shapeType,
		List<Point> polygonPoints,
		Point ellipseCenter,
		int ellipseWidth,
		int ellipseHeight,
		double ellipseAngle
	) {
		this.shapeType = shapeType;
		this.polygonPoints = polygonPoints;
		this.ellipseCenter = ellipseCenter;
		this.ellipseWidth = ellipseWidth;
		this.ellipseHeight = ellipseHeight;
		this.ellipseAngle = ellipseAngle;
	}

	/**
	 * @return the shapeType
	 */
	public ShapeType getShapeType() {
		return shapeType;
	}

	/**
	 * @return the polygonPoints
	 */
	public List<Point> getPolygonPoints() {
		return polygonPoints;
	}

	/**
	 * @return the ellipseCenter
	 */
	public Point getEllipseCenter() {
		return ellipseCenter;
	}

	/**
	 * @return the ellipseWidth
	 */
	public int getEllipseWidth() {
		return ellipseWidth;
	}

	/**
	 * @return the ellipseHeight
	 */
	public int getEllipseHeight() {
		return ellipseHeight;
	}

	/**
	 * @return the ellipseAngle
	 */
	public double getEllipseAngle() {
		return ellipseAngle;
	}

	public static Shape none() {
		return new Shape(ShapeType.NONE, null, null, 0, 0, 0.0);
	}

	public static Shape polygon(List<Point> polygonPoints) {
		return new Shape(ShapeType.POLYGON, polygonPoints, null, 0, 0, 0.0);
	}

	public static Shape circle(Point ellipseCenter, int ellipseWidth, int ellipseHeight, double ellipseAngle) {
		return new Shape(ShapeType.CIRCLE, null, ellipseCenter, ellipseWidth, ellipseHeight, ellipseAngle);
	}

}
