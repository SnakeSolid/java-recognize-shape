package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class PolygonRecognizer {

	private static final double CORNER_THRESHOLD = 0.1;

	private final Kernel peakKernel;

	private final double[] filtered;

	private final List<Integer> pointIndexes;

	private final List<Point> polygonPoints;

	public PolygonRecognizer() {
		this.peakKernel = Kernel.peakDetect(Algorithm.DATA_SIZE);
		this.filtered = new double[Algorithm.DATA_SIZE];
		this.pointIndexes = new ArrayList<>();
		this.polygonPoints = new ArrayList<>();
	}

	public List<Point> getPolygonPoints() {
		return polygonPoints;
	}

	public void recognize(double[] values, int[] indexes, List<Point> points, List<Distance> distances) {
		peakKernel.apply(values, filtered);

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
				Distance distance = distances.get(indexes[maxIndex]);
				found.add(distance);
				inPoint = false;
			} else if (value < CORNER_THRESHOLD && !inPoint) {
				// Not in point, do nothing
			}
		}

		Collections.sort(found, Comparator.comparingInt((Distance d) -> d.index).reversed());

		pointIndexes.clear();
		polygonPoints.clear();

		for (Distance foundDistancie : found) {
			int index = foundDistancie.index;
			Point point = points.get(index);

			pointIndexes.add(index);
			polygonPoints.add(point);
		}
	}

	public double mse(List<Point> points, int centerX, int centerY) {
		if (polygonPoints.size() < 2) {
			return Double.MAX_VALUE;
		}

		double mse = 0.0;

		for (int indexIndex = 0; indexIndex < pointIndexes.size(); indexIndex += 1) {
			int fromIndex = pointIndexes.get(indexIndex);
			int toIndex = pointIndexes.get((indexIndex + 1) % pointIndexes.size());
			Point pointA = points.get(fromIndex);
			Point pointB = points.get(toIndex);
			Function<Point, Double> callback;

			if (pointA.x == pointB.x) {
				callback = (point) -> (double) Math.abs(pointA.y - point.y);
			} else if (pointA.y == pointB.y) {
				callback = (point) -> (double) Math.abs(pointA.x - point.x);
			} else {
				double a = (double) (pointA.y - pointB.y) / (pointA.x - pointB.x);
				double b = pointA.y - a * pointA.x;
				double a21 = a * a + 1;

				callback = (point) -> Math.sqrt(Math.pow(b + a * point.x - point.y, 2.0) / a21);
			}

			while (fromIndex != toIndex) {
				Point point = points.get(fromIndex);
				mse += callback.apply(point);

				fromIndex = (fromIndex + 1) % points.size();
			}
		}

		mse /= points.size();

		return mse;
	}

}
