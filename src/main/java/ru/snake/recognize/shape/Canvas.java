package ru.snake.recognize.shape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Canvas extends JPanel {

	private final ShapeModel model;

	public Canvas(ShapeModel model) {
		this.model = model;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 600, 600);
		g2.setColor(new Color(51, 102, 204));

		List<Point> values = model.getValues();
		Shape shape = model.getShape();

		switch (shape.getShapeType()) {
		case NONE:
			break;

		case POLYGON:
			Polygon poligon = new Polygon();

			for (Point point : shape.getPolygonPoints()) {
				poligon.addPoint(point.x, point.y);
			}

			g2.fillPolygon(poligon);
			break;

		case CIRCLE:
			AffineTransform transform = g2.getTransform();

			g2.translate(shape.getEllipseCenter().x, shape.getEllipseCenter().y);
			g2.rotate(-shape.getEllipseAngle());
			g2.fillOval(
				-shape.getEllipseWidth() / 2,
				-shape.getEllipseHeight() / 2,
				shape.getEllipseWidth(),
				shape.getEllipseHeight()
			);

			g2.setTransform(transform);
			break;

		default:
			break;
		}

		g2.setColor(Color.BLACK);

		for (int i = 0; i < values.size() - 1; i += 1) {
			Point leftPoint = values.get(i);
			Point rightPoint = values.get(i + 1);

			g2.drawLine(leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y);
		}
	}

}
