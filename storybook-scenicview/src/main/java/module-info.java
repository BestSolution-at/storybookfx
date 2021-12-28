import at.bestsolution.storybookfx.StorybookPlugin;
import at.bestsolution.storybookfx.plugin.scenicview.ScenicViewPlugin;

module at.bestsolution.storybookfx.plugin.scenicview {
	requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.web;
    requires transitive javafx.swing;
    
    requires org.scenicview.scenicview;
    
    requires at.bestsolution.storybookfx;
    
    provides StorybookPlugin with ScenicViewPlugin;
}