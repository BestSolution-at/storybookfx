package at.bestsolution.storybookfx;

import javafx.scene.Node;

public interface Story {
	public String title();
//	public Class<? extends Node> component();
	public Node createSampleNode();
}
