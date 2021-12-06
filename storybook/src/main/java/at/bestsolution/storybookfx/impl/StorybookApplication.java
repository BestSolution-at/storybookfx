package at.bestsolution.storybookfx.impl;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import at.bestsolution.storybookfx.Story;
import at.bestsolution.storybookfx.Storybook;
import at.bestsolution.storybookfx.StorybookTheme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StorybookApplication extends Application {
	private int refreshCount = 0;
	
	private StackPane storyPane = new StackPane();
	
	{
		storyPane.getStyleClass().add("root");
		storyPane.setStyle("-fx-background-color: white");
		storyPane.setPadding(new Insets(10));
	}

	@Override
	public void start(Stage stage) throws Exception {
		SplitPane pane = new SplitPane();
		pane.getStyleClass().add("storybook-splitpane");
		pane.getItems().addAll(createNavigation(), createContentArea());
		pane.setDividerPositions(0.3);

		StackPane stackPane = new StackPane(pane);
		stackPane.setStyle("-fx-background-color: rgb(246, 249, 252); -fx-padding: 10;");

		Scene scene = new Scene(stackPane, 800, 600);
		scene.getStylesheets().add(Storybook.class.getResource("storybookfx.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		
		refreshStylesheets();
	}
	
	public void refreshStylesheets() {
		storyPane.getStylesheets().clear();
		ServiceLoader.load(StorybookTheme.class).forEach( t -> {
			storyPane.getStylesheets().addAll(t.getStylesheets().stream().map( s -> s + "?count=" + refreshCount).collect(Collectors.toList()));
		});
		
	}

	private Node createNavigation() {
		VBox box = new VBox();
		box.getStyleClass().add("storybook-navigation");
		HBox.setHgrow(box, Priority.ALWAYS);
		
		VBox navigationContent = new VBox();
		VBox.setVgrow(navigationContent, Priority.ALWAYS);
		box.getChildren().add(navigationContent);

		ServiceLoader.load(Story.class).stream().map(Provider::get)
			.sorted(Comparator.comparing(Story::title))
			.forEach( (s) -> handleStory(s, navigationContent));

		return box;
	}
	
	private void handleStory(Story story, VBox box) {
		String[] parts = story.title().split("/");
		
		TitledPane pane = box.getChildren().isEmpty() ? null : (TitledPane)box.getChildren().get(box.getChildren().size()-1);
		if( pane == null || ! pane.getUserData().equals(parts[0]) ) {
			pane = new TitledPane(parts[0], null);
			pane.setCollapsible(false);
			pane.setMaxHeight(Double.MAX_VALUE);
			VBox.setVgrow(pane, Priority.ALWAYS);
			TreeView<Story> view = new TreeView<>();
			view.setCellFactory( ( v ) -> {
				return new TreeCell<Story>() {
					public void updateItem(Story value, boolean empty) {
						super.updateItem(story, empty);
						if( value != null ) {
							setText(story.title().split("/")[1]); 
						}
					}
				};
			});
			view.setRoot(new TreeItem<Story>());
			view.setShowRoot(false);
			view.getSelectionModel().selectedItemProperty().addListener( (ob,ol,ne) -> {
				if( ne != null ) {
					storyPane.getChildren().setAll(ne.getValue().createSampleNode());	
				} else {
					storyPane.getChildren().clear();
				}
				
			});
			pane.setContent(view);
			pane.setUserData(parts[0]);
			box.getChildren().add(pane);
		}
		
		TreeView<Story> content = (TreeView<Story>) pane.getContent();
		content.getRoot().getChildren().add(new TreeItem<Story>(story, null));
	}

	private Node createContentArea() {
		VBox box = new VBox();
		box.getStyleClass().add("storybook-content-area");
		
		Button b = new Button("Refresh");
		b.setOnAction( evt -> {
			refreshStylesheets();
		});
		ToolBar bar = new ToolBar(b);
		box.getChildren().add(bar);
		
		VBox.setVgrow(storyPane, Priority.ALWAYS);
		box.getChildren().add(storyPane);
		
		StackPane stackPane = new StackPane(box);
		stackPane.setPadding(new Insets(10));
		
		return stackPane;
	}
}
