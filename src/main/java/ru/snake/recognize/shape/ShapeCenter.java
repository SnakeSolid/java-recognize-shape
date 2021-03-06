package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.List;

public class ShapeCenter {

	private int centerX;

	private int centerY;

	public ShapeCenter() {
		this.centerX = 0;
		this.centerY = 0;
	}

	public void calculateBox(List<Point> points) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (Point point : points) {
			minX = Math.min(minX, point.x);
			minY = Math.min(minY, point.y);
			maxX = Math.max(maxX, point.x);
			maxY = Math.max(maxY, point.y);
		}

		this.centerX = (minX + maxX) / 2;
		this.centerY = (minY + maxY) / 2;
	}

	public void calculateLinear(List<Point> points) {
		if (points.isEmpty()) {
			this.centerX = 0;
			this.centerY = 0;

			return;
		} else if (points.size() == 1) {
			Point point = points.get(0);

			this.centerX = point.x;
			this.centerY = point.y;

			return;
		}

		Point firstPoint = points.get(0);
		Point lastPoint = points.get(points.size() - 1);
		double totalLength = Math
			.sqrt(Math.pow(firstPoint.x - lastPoint.x, 2.0) + Math.pow(firstPoint.y - lastPoint.y, 2.0));
		double sumX = (firstPoint.x + lastPoint.x) / 2.0;
		double sumY = (firstPoint.y + lastPoint.y) / 2.0;

		for (int index = 0; index < points.size() - 1; index += 1) {
			Point leftPoint = points.get(index);
			Point rightPoint = points.get(index + 1);
			double length = Math
				.sqrt(Math.pow(leftPoint.x - rightPoint.x, 2.0) + Math.pow(leftPoint.y - rightPoint.y, 2.0));
			totalLength += length;
			sumX += length * (leftPoint.x + rightPoint.x) / 2.0;
			sumY += length * (leftPoint.y + rightPoint.y) / 2.0;
		}

		this.centerX = (int) Math.round(sumX / totalLength);
		this.centerY = (int) Math.round(sumY / totalLength);
	}

	/**
	 * @return the centerX
	 */
	public int getCenterX() {
		return centerX;
	}

	/**
	 * @return the centerY
	 */
	public int getCenterY() {
		return centerY;
	}

	@Override
	public String toString() {
		return "ShapeCenter [centerX=" + centerX + ", centerY=" + centerY + "]";
	}

}
