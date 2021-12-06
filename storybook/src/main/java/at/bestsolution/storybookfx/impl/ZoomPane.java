package at.bestsolution.storybookfx.impl;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

public class ZoomPane extends StackPane {
	private final DoubleProperty zoomLevel = new SimpleDoubleProperty(this, "zoomLevel", 1.0);
	
	public ZoomPane() {
		Scale scale = new Scale(1, 1, 0, 0);
		scale.xProperty().bind(zoomLevel);
		scale.yProperty().bind(zoomLevel);
		getTransforms().add(scale);
		zoomLevel.addListener((obs,ol,ne) -> {
			requestLayout();
		});
	}
	
	@Override
	protected double computeMinWidth(double height) {
		return super.computeMinWidth(height) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return super.computeMinHeight(width) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computePrefHeight(double width) {
		return super.computePrefHeight(width) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return super.computePrefWidth(height) * zoomLevel.doubleValue();
	}
	
	@Override
	protected double computeMaxWidth(double height) {
		return super.computeMaxWidth(height);
	}
	
	@Override
	protected double computeMaxHeight(double width) {
		return super.computeMaxHeight(width);
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
