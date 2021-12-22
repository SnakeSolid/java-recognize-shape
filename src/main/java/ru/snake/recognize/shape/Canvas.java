package ru.snake.recognize.shape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
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

		switch (model.getShape()) {
		case NONE:
			break;

		case POLYGON:
			Polygon poligon = new Polygon();

			for (Point point : model.getPoints()) {
				poligon.addPoint(point.x, point.y);
			}

			g2.fillPolygon(poligon);
			break;

		case CIRCLE:
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;

			for (int i = 0; i < values.size() - 1; i += 1) {
				minX = Math.min(minX, values.get(i).x);
				minY = Math.min(minY, values.get(i).y);
				maxX = Math.max(maxX, values.get(i).x);
				maxY = Math.max(maxY, values.get(i).y);
			}

			g2.fillOval(minX, minY, maxX - minX, maxY - minY);
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
