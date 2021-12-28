package ru.snake.recognize.shape;

import java.util.List;

public class EllipseRecognizer {

	private final Kernel flatKernel;

	private double ellipseAngle;

	private int ellipseWidth;

	private int ellipseHeight;

	public EllipseRecognizer() {
		this.flatKernel = Kernel.flatDetect(Algorithm.DATA_SIZE);
	}

	public double getEllipseAngle() {
		return ellipseAngle;
	}

	public void setEllipseAngle(double ellipseAngle) {
		this.ellipseAngle = ellipseAngle;
	}

	public int getEllipseWidth() {
		return ellipseWidth;
	}

	public void setEllipseWidth(int ellipseWidth) {
		this.ellipseWidth = ellipseWidth;
	}

	public int getEllipseHeight() {
		return ellipseHeight;
	}

	public void setEllipseHeight(int ellipseHeight) {
		this.ellipseHeight = ellipseHeight;
	}

	public void recognize(double[] values, int[] indexes, List<Distance> distances) {
		int agnleIndex = 0;
		double agnleValue = Double.MIN_VALUE;
		double[] filtered = new double[values.length];

		flatKernel.apply(values, filtered);

		for (int index = 0; index < filtered.length; index += 1) {
			double value = filtered[index];

			if (value > agnleValue) {
				agnleIndex = index;
				agnleValue = value;
			}
		}

		Distance widthA = distances.get(indexes[agnleIndex]);
		Distance widthB = distances.get(indexes[(agnleIndex + indexes.length / 2) % indexes.length]);
		Distance heightA = distances.get(indexes[(agnleIndex + 1 * indexes.length / 4) % indexes.length]);
		Distance heightB = distances.get(indexes[(agnleIndex + 3 * indexes.length / 4) % indexes.length]);

		ellipseWidth = (int) ((widthA.distance + widthB.distance) / 2.0);
		ellipseHeight = (int) ((heightA.distance + heightB.distance) / 2.0);
		ellipseAngle = 2.0 * Math.PI * agnleIndex / Algorithm.DATA_SIZE + Math.PI / 2.0;
	}

	public double mse(List<Distance> distances) {
		double mse = 0.0;

		for (Distance distance : distances) {
			double cos01 = (Math.cos(distance.angle - ellipseAngle) + 1.0) / 2.0;
			double delta = distance.distance - cos01 * (ellipseWidth - ellipseHeight) + ellipseHeight;

			mse += Math.pow(delta, 2.0);
		}

		mse /= distances.size();

		return mse;
	}

}
