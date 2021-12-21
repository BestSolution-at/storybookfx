package at.bestsolution.storybookfx.impl;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import at.bestsolution.storybookfx.Story;
import at.bestsolution.storybookfx.StorySample;
import at.bestsolution.storybookfx.Storybook;
import at.bestsolution.storybookfx.StorybookTheme;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.util.StringConverter;

public class StorybookApplication extends Application {
	private int refreshCount = 0;
	
	private VBox storyPane = new VBox();
	
	{
		storyPane.setStyle("-fx-background-color: white");
		storyPane.setPadding(new Insets(10));
	}
	
	private DoubleProperty zoomLevel = new SimpleDoubleProperty(this, "zoomLevel", 1.0);

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
		
		zoomLevel.addListener( (obs, ol, ne) -> {
			storyPane.lookupAll("ZoomContainer").forEach( n -> {
				ZoomContainer c = (ZoomContainer) n;
				c.setZoomLevel(zoomLevel.get());
			});
		});
	}
	
	public void refreshStylesheets() {
		storyPane.lookupAll(".story-root").forEach( n -> {
			((Parent)n).getStylesheets().clear();
			refreshCount += 1;
			
			ServiceLoader.load(StorybookTheme.class).forEach( t -> {
				storyPane.getStylesheets().addAll(t.getStylesheets());
			});
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
			TreeView<Object> view = new TreeView<>();
			view.setCellFactory( ( v ) -> {
				return new TreeCell<Object>() {
					public void updateItem(Object value, boolean empty) {
						super.updateItem(story, empty);
						if( value != null ) {
							if( value instanceof Story ) {
								Story s = (Story) value;
								setText(s.title().split("/")[1]);	
							} else if( value instanceof StorySample ) {
								StorySample s = (StorySample) value;
								setText(s.title());
							}
						} else {
							setText("");
						}
					}
				};
			});
			view.setRoot(new TreeItem<Object>());
			view.setShowRoot(false);
			view.getSelectionModel().selectedItemProperty().addListener( (ob,ol,ne) -> {
				if( ne != null ) {
					if( ne.getValue() instanceof Story ) {
						updatePreview((Story) ne.getValue());
					}
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
	
	private void updatePreview(Story story) {
		VBox box = new VBox(20);
		box.getChildren().addAll(story.samples().stream().map(this::createSampleView).collect(Collectors.toList()));
		storyPane.getChildren().setAll(box);
	}
	
	private VBox createSampleView(StorySample storySample) {
		VBox box = new VBox(10);
		Label label = new Label(storySample.title());
		label.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");
		box.getChildren().add(label);
		
		StackPane storySampleRoot = new StackPane(storySample.createSampleNode());
		storySampleRoot.setStyle("-fx-background-color: white");
		storySampleRoot.getStyleClass().addAll("root","story-root");
		
		ServiceLoader.load(StorybookTheme.class).forEach( t -> {
			storySampleRoot.getStylesheets().addAll(t.getStylesheets());
		});
		
		ZoomContainer pane = new ZoomContainer(storySampleRoot);
		pane.setZoomLevel(zoomLevel.get());
		pane.getStyleClass().add("storybook-sample-canvas");
		box.getChildren().add(pane);
		
		return box;
	}

	private Node createContentArea() {
		VBox box = new VBox();
		box.getStyleClass().add("storybook-content-area");
		
		Button refresh = new Button("Refresh");
		refresh.setOnAction( evt -> {
			refreshStylesheets();
		});
		
		
		ChoiceBox<Double> zoomLevel = new ChoiceBox<>(FXCollections.observableArrayList(1.0, 1.5, 2.0, 2.5)); 
		zoomLevel.setConverter( new StringConverter<Double>() {
			
			@Override
			public String toString(Double v) {
				return v * 100 + "%";
			}
			
			@Override
			public Double fromString(String v) {
				return null;
			}
		});
		zoomLevel.setValue(1.0);
		this.zoomLevel.bind(zoomLevel.valueProperty()); 
		
		ToolBar bar = new ToolBar(refresh, zoomLevel);
		box.getChildren().add(bar);
		
		
		ScrollPane scrollPane = new ScrollPane(storyPane);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		box.getChildren().add(scrollPane);
		
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		
		StackPane stackPane = new StackPane(box);
		stackPane.setPadding(new Insets(10));
		
		return stackPane;
	}
}
