package org.eclipse.bpmn2.modeler.core.layout.util;

import org.eclipse.draw2d.geometry.Vector;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class LayoutUtil {
	
	public static double getLayoutTreshold(Shape startShape, Shape endShape) {
		
		Point startShapeCenter = getCenter(startShape);
		Point endShapeCenter = getCenter(endShape);
		
		Vector shapeVector = new Vector(endShapeCenter.getX() - startShapeCenter.getX(), endShapeCenter.getY() - startShapeCenter.getY());
		Vector unitXVector = new Vector (1,0);
		
		double angle = shapeVector.getAngle(unitXVector);
		return angle;
	}
	
	private static Point getCenter(Shape shape) {
		ILocation shapeLocation = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(shape);
		GraphicsAlgorithm shapeGa = shape.getGraphicsAlgorithm();
		Point shapeCenter = Graphiti.getGaService().createPoint(shapeLocation.getX() + shapeGa.getWidth() / 2, shapeLocation.getY() + shapeGa.getHeight() / 2 );
		
		return shapeCenter;
	}
}
