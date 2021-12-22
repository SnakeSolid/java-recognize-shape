package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algorithm {

	private static final int DATA_SIZE = 1024;

	private final List<Distance> distancies;

	private final Kernel kernel;

	private final double[] data;

	private final double[] filtered;

	private final int[] indexes;

	public Algorithm() {
		this.distancies = new ArrayList<>();
		this.data = new double[DATA_SIZE];
		this.filtered = new double[DATA_SIZE];
		this.indexes = new int[DATA_SIZE];
		this.kernel = Kernel.peakDetect(DATA_SIZE);
	}

	public Shape recognize(List<Point> sourcePoints, List<Point> polygonPoints) {
		// --------------------------------------------------------------------
		// Calculate center of point cloud.
		int centerX = 0;
		int centerY = 0;

		for (Point point : sourcePoints) {
			centerX += point.x;
			centerY += point.y;
		}

		centerX /= sourcePoints.size();
		centerY /= sourcePoints.size();

		// --------------------------------------------------------------------
		// Calculate distances from center to every point.
		distancies.clear();

		for (int index = 0; index < sourcePoints.size(); index += 1) {
			Point point = sourcePoints.get(index);
			Distance distance = new Distance();

			int deltaX = point.x - centerX;
			int deltaY = point.y - centerY;

			distance.distance = Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
			distance.angle = Math.atan2(deltaX, deltaY);
			distance.index = index;
			distancies.add(distance);
		}

		Collections.sort(distancies, Comparator.comparingDouble((Distance d) -> d.angle));

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
			distance.distance = distance.distance / maximalDistance;
			distance.position = data.length * (distance.angle - minimalAngle) / (maximalAngle - minimalAngle);
		}

		// --------------------------------------------------------------------
		// Build distance chart mapped linearly to angle.
		for (int index = 0; index < data.length; index += 1) {
			Distance dd = new Distance();
			dd.position = index;

			int found = Collections
				.binarySearch(distancies, dd, Comparator.comparingDouble((Distance d) -> d.position));

			if (found >= 0) {
				data[index] = distancies.get(found).distance;
			} else {
				int point = -found - 1;
				Distance left = distancies.get(point - 1);
				Distance right = distancies.get(point);
				double factor = (index - left.position) / (left.position - right.position);

				data[index] = left.distance + factor * (left.distance - right.distance);

				if (factor < 0.5) {
					indexes[index] = point - 1;
				} else {
					indexes[index] = point;
				}
			}
		}

		kernel.apply(data, filtered);

		// --------------------------------------------------------------------
		// Check that shape is circle. In this case peak detector must not any
		// find corners.
		boolean isCircle = true;

		for (double value : filtered) {
			if (value > 1.0) {
				isCircle = false;

				break;
			}
		}

		if (isCircle) {
			return Shape.CIRCLE;
		} else {
			// --------------------------------------------------------------------
			// Find first point peak.
			int startIndex = 0;

			while (startIndex < filtered.length && filtered[startIndex] >= 0.0) {
				startIndex += 1;
			}

			while (startIndex < filtered.length && filtered[startIndex] < 0.0) {
				startIndex += 1;
			}

			// --------------------------------------------------------------------
			// Find best point for every peak.
			List<Distance> found = new ArrayList<>();
			boolean inPoint = true;
			double maxValue = filtered[startIndex];
			int maxIndex = indexes[startIndex];

			for (int index = 0; index < filtered.length; index += 1) {
				int offset = index + startIndex;

				if (offset >= filtered.length) {
					offset -= filtered.length;
				}

				double value = filtered[offset];

				if (value >= 0.0 && inPoint) {
					if (maxValue < value) {
						maxValue = value;
						maxIndex = offset;
					}
				} else if (value >= 0.0 && !inPoint) {
					maxValue = value;
					maxIndex = offset;
					inPoint = true;
				} else if (value < 0.0 && inPoint) {
					Distance distance = distancies.get(indexes[maxIndex]);
					found.add(distance);
					inPoint = false;
				} else if (value < 0.0 && !inPoint) {
					// Not in point, do nothing
				}
			}

			if (found.size() > 5) {
				return Shape.NONE;
			}

			Collections.sort(found, Comparator.comparingInt((Distance d) -> d.index).reversed());

			polygonPoints.clear();

			for (Distance foundDistancie : found) {
				Point point = sourcePoints.get(foundDistancie.index);
				polygonPoints.add(point);
			}

			return Shape.POLYGON;
		}
	}

}
