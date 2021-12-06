package at.bestsolution.storybookfx.impl;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;

public class ZoomContainer extends Region {
	private final Node contentNode;
	private final DoubleProperty zoomLevel = new SimpleDoubleProperty(this, "zoomLevel", 1.0);
	
	public ZoomContainer(Node contentNode) {
		this.contentNode = contentNode;
		Scale scale = new Scale(1, 1, 0, 0);
		scale.xProperty().bind(zoomLevel);
		scale.yProperty().bind(zoomLevel);
		contentNode.getTransforms().add(scale);
		zoomLevel.addListener( (ob,ol,ne) -> {
			requestLayout();
		});
		getChildren().add(contentNode);
	}
	
	@Override
	protected void layoutChildren() {
		this.contentNode.resizeRelocate(0, 0, getWidth() / zoomLevel.doubleValue(), getHeight() / zoomLevel.doubleValue());
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return super.computeMinHeight(width) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computeMinWidth(double height) {
		return super.computeMinWidth(height) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computePrefHeight(double width) {
		return super.computePrefHeight(width) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return super.computePrefWidth(height) * zoomLevel.doubleValue();
	}

	public final DoubleProperty zoomLevelProperty() {
		return this.zoomLevel;
	}
	

	public final double getZoomLevel() {
		return this.zoomLevelProperty().get();
	}
	

	public final void setZoomLevel(final double zoomLevel) {
		this.zoomLevelProperty().set(zoomLevel);
	}
	
}
