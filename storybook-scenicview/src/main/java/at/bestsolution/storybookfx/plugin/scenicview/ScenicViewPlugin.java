package at.bestsolution.storybookfx.plugin.scenicview;

import org.scenicview.ScenicView;

import at.bestsolution.storybookfx.StorybookPlugin;
import javafx.scene.Scene;

public class ScenicViewPlugin implements StorybookPlugin {
	@Override
	public Scene processScene(Scene scene) {
		ScenicView.show(scene);
		return scene;
	}
}
