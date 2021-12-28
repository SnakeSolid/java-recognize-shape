package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algorithm {

	public static final int DATA_SIZE = 1024;

	private static final double CIRCLE_THRESHOLD = 0.4;

	private static final double CORNER_THRESHOLD = 0.1;

	private final ShapeCenter shapeCenter;

	private final ShapeProfile shapeProfile;

	private final ShapeHistogram shapeHistogram;

	private final Kernel peakKernel;

	private final double[] filtered;

	public Algorithm() {
		this.shapeCenter = new ShapeCenter();
		this.shapeProfile = new ShapeProfile();
		this.shapeHistogram = new ShapeHistogram();
		this.peakKernel = Kernel.peakDetect(DATA_SIZE);
		this.filtered = new double[DATA_SIZE];
	}

	public Shape recognize(List<Point> sourcePoints) {
		shapeCenter.calculateBox(sourcePoints);

		int centerX = shapeCenter.getCenterX();
		int centerY = shapeCenter.getCenterY();

		shapeProfile.calculate(sourcePoints, centerX, centerY);

		List<Distance> distances = shapeProfile.getDistancies();
		shapeHistogram.calculate(distances);

		double[] values = shapeHistogram.getValues();
		int[] indexes = shapeHistogram.getIndexes();

		peakKernel.apply(values, filtered);

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
			EllipseRecognizer ellipseRecognizer = new EllipseRecognizer();
			ellipseRecognizer.recognize(values, indexes, distances);
			double mse = ellipseRecognizer.mse(distances);
			int ellipseWidth = ellipseRecognizer.getEllipseWidth();
			int ellipseHeight = ellipseRecognizer.getEllipseHeight();
			double ellipseAngle = ellipseRecognizer.getEllipseAngle();
			Point ellipseCenter = new Point(centerX, centerY);

			System.out.println(mse);

			return Shape.circle(ellipseCenter, 2 * ellipseWidth, 2 * ellipseHeight, ellipseAngle);
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
					Distance distance = distances.get(indexes[maxIndex]);
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
