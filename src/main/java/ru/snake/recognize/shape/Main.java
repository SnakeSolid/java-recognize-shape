package ru.snake.recognize.shape;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main {

	public static void main(String[] args) {
		JFrame dialog = new JFrame("Shape Recognize");
		ShapeModel model = new ShapeModel();
		Canvas canvas = new Canvas(model);
		FrameMouseListener mouseListener = new FrameMouseListener(model, canvas);
		dialog.add(canvas);
		dialog.addMouseListener(mouseListener);
		dialog.addMouseMotionListener(mouseListener);
		dialog.setPreferredSize(new Dimension(600, 600));
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
	}

}
