package ru.snake.recognize.shape;

public class Distance {

	public double distance = 0.0;

	public double angle = 0.0;

	public double position = 0.0;

	public int index = 0;

	@Override
	public String toString() {
		return ("" + distance + ";" + angle + ";" + position + ";" + index).replace('.', ',');
	}

}
