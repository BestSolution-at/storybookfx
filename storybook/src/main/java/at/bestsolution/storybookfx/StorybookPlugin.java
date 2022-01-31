package at.bestsolution.storybookfx;

import javafx.scene.Scene;

public interface StorybookPlugin {
	public default Scene processScene(Scene scene) {
		return scene;
	};
}
