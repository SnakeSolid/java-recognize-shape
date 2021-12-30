package ru.snake.recognize.shape;

import java.awt.Point;
import java.util.List;

public class Algorithm {

	public static final int DATA_SIZE = 1024;

	private final ShapeCenter shapeCenter;

	private final ShapeProfile shapeProfile;

	private final ShapeHistogram shapeHistogram;

	private final EllipseRecognizer ellipseRecognizer;

	private final PolygonRecognizer polygonRecognizer;

	private final PolylineRecognizer polylineRecognizer;

	private final Kernel peakKernel;

	private final double[] filtered;

	public Algorithm() {
		this.shapeCenter = new ShapeCenter();
		this.shapeProfile = new ShapeProfile();
		this.shapeHistogram = new ShapeHistogram();
		this.ellipseRecognizer = new EllipseRecognizer();
		this.polygonRecognizer = new PolygonRecognizer();
		this.polylineRecognizer = new PolylineRecognizer();
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

		ellipseRecognizer.recognize(values, indexes, distances);
		polygonRecognizer.recognize(values, indexes, sourcePoints, distances);
		polylineRecognizer.recognize(values, indexes, sourcePoints, distances);
		double ellipseMse = ellipseRecognizer.mse(sourcePoints, centerX, centerY);
		double polygonMse = polygonRecognizer.mse(sourcePoints, centerX, centerY);
		double polylineMse = polylineRecognizer.mse(sourcePoints, centerX, centerY);

		System.out.println("--------------");
		System.out.println("Ellipse: " + ellipseMse);
		System.out.println("Polygon: " + polygonMse);
		System.out.println("Polyline: " + polylineMse);

		if (ellipseMse < polygonMse && ellipseMse < polylineMse) {
			Point ellipseCenter = new Point(centerX, centerY);
			int ellipseWidth = ellipseRecognizer.getEllipseWidth();
			int ellipseHeight = ellipseRecognizer.getEllipseHeight();
			double ellipseAngle = ellipseRecognizer.getEllipseAngle();

			return Shape.circle(ellipseCenter, 2 * ellipseWidth, 2 * ellipseHeight, ellipseAngle);
		} else if (polygonMse < polylineMse) {
			return Shape.polygon(polygonRecognizer.getPolygonPoints());
		} else {
			return Shape.polygon(polylineRecognizer.getPolygonPoints());
		}
	}

}
