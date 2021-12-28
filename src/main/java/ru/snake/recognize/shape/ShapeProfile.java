package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShapeProfile {

	private final List<Distance> distancies;

	public ShapeProfile() {
		this.distancies = new ArrayList<>();
	}

	public void calculate(List<Point> points, int centerX, int centerY) {
		calculateDistancies(points, centerX, centerY);
	}

	/**
	 * Calculate distances from given center to every point.
	 *
	 * @param points  shape points
	 * @param centerX center x
	 * @param centerY center y
	 */
	private void calculateDistancies(List<Point> points, int centerX, int centerY) {
		distancies.clear();

		for (int index = 0; index < points.size(); index += 1) {
			Point point = points.get(index);
			Distance distance = new Distance();

			int deltaX = point.x - centerX;
			int deltaY = point.y - centerY;

			distance.distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
			distance.angle = Math.atan2(deltaX, deltaY);
			distance.index = index;
			distancies.add(distance);
		}

		Collections.sort(distancies, Comparator.comparingDouble((Distance d) -> d.angle));
	}

	/**
	 * @return the distancies
	 */
	public List<Distance> getDistancies() {
		return distancies;
	}

	@Override
	public String toString() {
		return "ShapeProfile [distancies=" + distancies + "]";
	}

}
