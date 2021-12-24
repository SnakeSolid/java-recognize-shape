package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algorithm {

	private static final double CIRCLE_THRESHOLD = 0.8;

	private static final double CORNER_THRESHOLD = 0.0;

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

	public Shape recognize(List<Point> sourcePoints) {
		// --------------------------------------------------------------------
		// Calculate center of point cloud.
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (Point point : sourcePoints) {
			minX = Math.min(minX, point.x);
			minY = Math.min(minY, point.y);
			maxX = Math.max(maxX, point.x);
			maxY = Math.max(maxY, point.y);
		}

		int centerX = (minX + maxX) / 2;
		int centerY = (minY + maxY) / 2;

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
			if (value > CIRCLE_THRESHOLD) {
				isCircle = false;

				break;
			}
		}

		if (isCircle) {
			int minIndex = 0;
			int maxIndex = 0;
			double minValue = Integer.MAX_VALUE;
			double maxValue = Integer.MIN_VALUE;

			for (int index = 0; index < data.length; index += 1) {
				double value = data[index];

				if (value < minValue) {
					minIndex = index;
					minValue = value;
				}

				if (value > maxValue) {
					maxIndex = index;
					maxValue = value;
				}
			}

			int agnleIndex = 0;
			double agnleValue = Double.MIN_VALUE;

			for (int index = 0; index < filtered.length; index += 1) {
				double value = filtered[index];

				if (value > agnleValue) {
					agnleIndex = index;
					agnleValue = value;
				}
			}

			int ellipseWidth = (int) Math.sqrt(maximalDistance * distancies.get(indexes[maxIndex]).distance);
			int ellipseHeight = (int) Math.sqrt(maximalDistance * distancies.get(indexes[minIndex]).distance);
			double ellipseAngle = 2.0 * Math.PI * agnleIndex / DATA_SIZE + Math.PI / 2.0;

			return Shape.circle(new Point(centerX, centerY), 2 * ellipseWidth, 2 * ellipseHeight, ellipseAngle);
		} else {
			// --------------------------------------------------------------------
			// Find first point peak.
			int startIndex = 0;

			while (startIndex < filtered.length && filtered[startIndex] >= CORNER_THRESHOLD) {
				startIndex += 1;
			}

			while (startIndex < filtered.length && filtered[startIndex] < CORNER_THRESHOLD) {
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

				if (value >= CORNER_THRESHOLD && inPoint) {
					if (maxValue < value) {
						maxValue = value;
						maxIndex = offset;
					}
				} else if (value >= CORNER_THRESHOLD && !inPoint) {
					maxValue = value;
					maxIndex = offset;
					inPoint = true;
				} else if (value < CORNER_THRESHOLD && inPoint) {
					Distance distance = distancies.get(indexes[maxIndex]);
					found.add(distance);
					inPoint = false;
				} else if (value < CORNER_THRESHOLD && !inPoint) {
					// Not in point, do nothing
				}
			}

			if (found.size() > 5) {
				return Shape.none();
			}

			Collections.sort(found, Comparator.comparingInt((Distance d) -> d.index).reversed());

			List<Point> polygonPoints = new ArrayList<>();

			for (Distance foundDistancie : found) {
				Point point = sourcePoints.get(foundDistancie.index);
				polygonPoints.add(point);
			}

			return Shape.polygon(polygonPoints);
		}
	}

}
