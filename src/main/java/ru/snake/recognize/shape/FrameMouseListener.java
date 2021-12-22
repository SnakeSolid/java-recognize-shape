package ru.snake.recognize.shape;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class FrameMouseListener implements MouseListener, MouseMotionListener {

	private final ShapeModel model;

	private final Canvas canvas;

	private final Algorithm algorithm;

	public FrameMouseListener(ShapeModel model, Canvas canvas) {
		this.model = model;
		this.canvas = canvas;
		this.algorithm = new Algorithm();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		model.getValues().clear();
		model.getPoints().clear();
		model.setShape(Shape.NONE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (model.getValues().isEmpty()) {
			return;
		}

		Shape shape = algorithm.recognize(model.getValues(), model.getPoints());
		model.setShape(shape);

		canvas.repaint(new Rectangle(0, 0, 600, 600));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		model.getValues().add(new Point(e.getX(), e.getY()));

		canvas.repaint(new Rectangle(0, 0, 600, 600));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
