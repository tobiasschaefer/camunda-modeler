/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.List;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class DefaultMoveBPMNShapeFeature extends DefaultMoveShapeFeature {

	int preShapeX;
	int preShapeY;
	
	public DefaultMoveBPMNShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void preMoveShape(IMoveShapeContext context) {
		super.preMoveShape(context);
		preShapeX = 0;
		preShapeX = 0;
		
		if (context.getShape().getGraphicsAlgorithm() != null){
			preShapeX = context.getShape().getGraphicsAlgorithm().getX();
			preShapeY = context.getShape().getGraphicsAlgorithm().getY();
		}
	}
	
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		PictogramElement shape = context.getPictogramElement();
		BPMNShape bpmnShape = DIUtils.getShape(context.getPictogramElement());

		// move label after the shape has been moved
		moveLabel(shape, bpmnShape);
		
		// perform actual reconnect of edges
		AnchorUtil.reConnect(shape, getDiagram());
		
		// update di
		DIUtils.updateDIShape(shape, bpmnShape);
	}

	private void moveLabel(PictogramElement shape, BPMNShape bpmnShape) {
		
		Object[] node = getAllBusinessObjectsForPictogramElement(shape);
		
		for (Object object : node) {
			List<PictogramElement> picElements = Graphiti.getLinkService().getPictogramElements(getDiagram(), (EObject) object);
			for (PictogramElement element : picElements){
				boolean isLabel = Graphiti.getPeService().getPropertyValue(element, GraphicsUtil.LABEL_PROPERTY) != null;
				if (bpmnShape == null) {
					bpmnShape = DIUtils.getShape(element);
				}
				
				if (element!=shape && isLabel){
					try{
						ContainerShape container = (ContainerShape) element;
						// only align when not selected, the move feature of the label will do the job when selected
							GraphicsUtil.alignWithShape(
									(AbstractText) container.getChildren().get(0).getGraphicsAlgorithm(), 
									container,
									shape.getGraphicsAlgorithm().getWidth(),
									shape.getGraphicsAlgorithm().getHeight(),
									shape.getGraphicsAlgorithm().getX(),
									shape.getGraphicsAlgorithm().getY(),
									preShapeX,
									preShapeY
							);
						DIUtils.updateDILabel(container, bpmnShape);
					}
					catch(Exception e){
						new RuntimeException("Composition of label container is not as expected");
					}
				} else if (isLabel) {
					DIUtils.updateDILabel((ContainerShape) element, bpmnShape);
				}
			}
		}
	}
}