package ru.snake.recognize.shape;

public class Kernel {

	public final double[] values;

	public final int center;

	private Kernel(double[] values, int center) {
		this.values = values;
		this.center = center;
	}

	public void apply(double[] data, double[] result) {
		for (int dataIndex = 0; dataIndex < data.length; dataIndex += 1) {
			double sum = 0.0;

			for (int kernelIndex = 0; kernelIndex < values.length; kernelIndex += 1) {
				int offset = dataIndex + kernelIndex - center;

				if (offset < 0) {
					offset += data.length;
				}
				if (offset >= data.length) {
					offset -= data.length;
				}

				sum += values[kernelIndex] * data[offset];
			}

			result[dataIndex] = sum;
		}
	}

	public static Kernel peakDetect(int dataSize) {
		// data length / (2 * max corners + 1)
		int size = dataSize / (2 * 5 + 1);
		// Middle element of array
		int center = (size / 2) | 1;
		double values[] = new double[size];
		double kernelSum = 0.0;

		for (int index = 0; index < size; index += 1) {
			double factor = Math.abs(index - center) / (center + 1.0);
			values[index] = Math.sqrt(Math.pow(1 - factor, 2.0) + 1) - 1;
			kernelSum += values[index];
		}

		for (int index = 0; index < size; index += 1) {
			values[index] += -kernelSum / size;
		}

		return new Kernel(values, center);
	}

}
