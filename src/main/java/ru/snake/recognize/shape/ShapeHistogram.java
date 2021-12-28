package ru.snake.recognize.shape;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShapeHistogram {

	private static final int DATA_SIZE = 1024;

	private final double[] values;

	private final int[] indexes;

	public ShapeHistogram() {
		this.values = new double[DATA_SIZE];
		this.indexes = new int[DATA_SIZE];
	}

	public void calculate(List<Distance> distancies) {
		// --------------------------------------------------------------------
		// Normalize distances to make algorithm scale independent. Maximal
		// value distance must be 1.
		double minimalAngle = 0.0;
		double maximalAngle = 0.0;
		double maximalDistance = 0.0;

		for (Distance distance : distancies) {
			minimalAngle = Math.min(distance.angle, minimalAngle);
			maximalAngle = Math.max(distance.angle, maximalAngle);
			maximalDistance = Math.max(distance.distance, maximalDistance);
		}

		for (Distance distance : distancies) {
			distance.normalized = distance.distance / maximalDistance;
			distance.position = values.length * (distance.angle - minimalAngle) / (maximalAngle - minimalAngle);
		}

		// --------------------------------------------------------------------
		// Build distance chart mapped linearly to angle.
		for (int index = 0; index < values.length; index += 1) {
			Distance dd = new Distance();
			dd.position = index;

			int found = Collections
					.binarySearch(distancies, dd, Comparator.comparingDouble((Distance d) -> d.position));

			if (found >= 0) {
				values[index] = distancies.get(found).normalized;
			} else {
				int point = -found - 1;
				Distance left = distancies.get(point - 1);
				Distance right = distancies.get(point);
				double factor = (index - left.position) / (left.position - right.position);

				values[index] = left.normalized + factor * (left.normalized - right.normalized);

				if (factor < 0.5) {
					indexes[index] = point - 1;
				} else {
					indexes[index] = point;
				}
			}
		}
	}

	public double[] getValues() {
		return values;
	}

	public int[] getIndexes() {
		return indexes;
	}

	@Override
	public String toString() {
		return "ShapeHistogram [values=" + Arrays.toString(values) + ", indexes=" + Arrays.toString(indexes) + "]";
	}

}
