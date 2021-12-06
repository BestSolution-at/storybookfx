import at.bestsolution.storybookfx.Story;
import at.bestsolution.storybookfx.StorybookTheme;

module at.bestsolution.storybookfx {
	requires transitive javafx.graphics;
	requires javafx.controls;
	
	exports at.bestsolution.storybookfx;
	
	opens at.bestsolution.storybookfx.impl to javafx.graphics;
	
	uses Story;
	uses StorybookTheme;
}