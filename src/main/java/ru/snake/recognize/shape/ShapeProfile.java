package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapeProfile {

	private static final int DATA_SIZE = 1024;

	private final List<Distance> distancies;

	private final double[] values;

	private final int[] indexes;

	public ShapeProfile() {
		this.distancies = new ArrayList<>();
		this.values = new double[DATA_SIZE];
		this.indexes = new int[DATA_SIZE];
	}

	public void calculate(List<Point> points, int centerX, int centerY) {
		calculateDistancies(points, centerX, centerY);
	}

	/**
	 * Calculate distances from given center to every point.
	 *
	 * @param points
	 *            shape points
	 * @param centerX
	 *            center x
	 * @param centerY
	 *            center y
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
	}

	/**
	 * @return the dataSize
	 */
	public static int getDataSize() {
		return DATA_SIZE;
	}

	/**
	 * @return the distancies
	 */
	public List<Distance> getDistancies() {
		return distancies;
	}

	/**
	 * @return the values
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * @return the indexes
	 */
	public int[] getIndexes() {
		return indexes;
	}

	@Override
	public String toString() {
		return "ShapeProfile [distancies=" + distancies + ", values=" + Arrays.toString(values) + ", indexes="
				+ Arrays.toString(indexes) + "]";
	}

}
