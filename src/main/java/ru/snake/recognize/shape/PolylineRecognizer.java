package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PolylineRecognizer {

	private static final double CORNER_THRESHOLD = 0.1;

	private static final double MAX_DISTANCE = 0.2;

	private static final int N_ITERATIONS = 10;

	private final Kernel peakKernel;

	private final double[] filtered;

	private final List<Integer> pointIndexes;

	private final List<Point> polygonPoints;

	public PolylineRecognizer() {
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
		// Start building line from first point.
		pointIndexes.clear();
		polygonPoints.clear();

		pointIndexes.add(0);
		polygonPoints.add(points.get(0));

		int farIndex = 0;
		double farDistance = Double.MIN_VALUE;

		for (int index = 0; index < points.size(); index += 1) {
			Point point = points.get(index);
			double dstance = Math.pow(point.x - polygonPoints.get(0).x, 2.0)
					+ Math.pow(point.y - polygonPoints.get(0).y, 2.0);

			if (farDistance < dstance) {
				farIndex = index;
				farDistance = dstance;
			}
		}

		pointIndexes.add(farIndex);
		polygonPoints.add(points.get(farIndex));

		// Add only 3 new points.
		for (int iteration = 0; iteration < N_ITERATIONS; iteration += 1) {
			double maxDistance = Double.MIN_VALUE;
			int bestIndex = 0;
			int bestPoint = 0;

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
					double distance = callback.apply(point);

					if (maxDistance < distance) {
						maxDistance = distance;
						bestIndex = fromIndex;
						bestPoint = indexIndex;
					}

					fromIndex = (fromIndex + 1) % points.size();
				}
			}

			int fromIndex = pointIndexes.get(bestPoint);
			int toIndex = pointIndexes.get((bestPoint + 1) % pointIndexes.size());
			Point pointA = points.get(fromIndex);
			Point pointB = points.get(toIndex);

			if (maxDistance < MAX_DISTANCE
					* Math.sqrt(Math.pow(pointA.y - pointB.y, 2.0) + Math.pow(pointA.x - pointB.x, 2.0))) {
				break;
			}

			pointIndexes.add(bestPoint + 1, bestIndex);
			polygonPoints.add(bestPoint + 1, points.get(bestIndex));
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

		return mse * 10.0;
	}

	private static final class IndexValue {

		public final int index;

		public final double value;

		public IndexValue(int index, double value) {
			this.index = index;
			this.value = value;
		}

		@Override
		public String toString() {
			return "IndexValue [index=" + index + ", value=" + value + "]";
		}

	}

}
