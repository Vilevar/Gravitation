package be.vilevar.gravitation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HomeGui {

	private static final int WIDTH = 750, HEIGHT = 300;
	private static HomeGui i;
	
	public static Scene getScene(Gravitation gravitation) {
		return i == null ? (i = new HomeGui(gravitation)).createScene() : i.createScene();
	}
	
	private TableView<Star> stars;
	private Button edit, remove;
	private Scene scene;
	
	@SuppressWarnings("unchecked")
	public HomeGui(Gravitation gravitation) {
		TableColumn<Star, String> names = new TableColumn<>("Name");
		names.setMinWidth(150);
		names.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Star, Double> masses = new TableColumn<>("Mass (kg)");
		masses.setMinWidth(100);
		masses.setCellValueFactory(new PropertyValueFactory<>("mass"));
		TableColumn<Star, Double> radiuss = new TableColumn<>("Radius (m)");
		radiuss.setMinWidth(100);
		radiuss.setCellValueFactory(new PropertyValueFactory<>("radius"));
		TableColumn<Star, Double> x0s = new TableColumn<>("x0 (m)");
		x0s.setMinWidth(100);
		x0s.setCellValueFactory(new PropertyValueFactory<>("x0"));
		TableColumn<Star, Double> y0s = new TableColumn<>("y0 (m)");
		y0s.setMinWidth(100);
		y0s.setCellValueFactory(new PropertyValueFactory<>("y0"));
		TableColumn<Star, Double> vx0s = new TableColumn<>("vx0 (m/s)");
		vx0s.setMinWidth(100);
		vx0s.setCellValueFactory(new PropertyValueFactory<>("vx0"));
		TableColumn<Star, Double> vy0s = new TableColumn<>("vy0 (m/s)");
		vy0s.setMinWidth(100);
		vy0s.setCellValueFactory(new PropertyValueFactory<>("vy0"));
		
		this.stars = new TableView<>();
		this.stars.getColumns().addAll(names, masses, radiuss, x0s, y0s, vx0s, vy0s);
		this.stars.getItems().addAll(this.getDefaultItems());
		
		Button add = new Button("Add");
		add.setOnAction(e -> this.createStarDialog(new Star()).showAndWait().ifPresent(star -> this.stars.getItems().add(star)));
		Button remove = new Button("Remove");
		remove.setDisable(true);
		remove.setOnAction(e -> {
			Star selected = this.stars.getSelectionModel().getSelectedItem();
			if(selected != null) {
				this.stars.getItems().remove(selected);
			}
		});
		Button edit = new Button("Edit");
		edit.setDisable(true);
		edit.setOnAction(e -> {
			TableViewSelectionModel<Star> selection = this.stars.getSelectionModel();
			Star selected = selection.getSelectedItem();
			if(selected != null)
				this.createStarDialog(selected).showAndWait().ifPresent(star -> this.stars.getItems().set(selection.getSelectedIndex(), star));
		});
		Button start = new Button("Start");
		start.setOnAction(e -> {
			Stage window  = gravitation.getWindow();
			SimulationBoard simulation;
			if(SimulationBoard.save == null) {
				 simulation = new SimulationBoard(gravitation, this.stars.getItems());
			} else {
				simulation = SimulationBoard.save;
				simulation.createStars(this.stars.getItems(), true);
			}
			window.setScene(simulation.createScene());
			window.centerOnScreen();
		});
		this.stars.getItems().addListener((ListChangeListener<Star>) (c -> start.setDisable(c.getList().size() <= 1)));
		this.stars.getSelectionModel().selectedIndexProperty().addListener((obs, old, id) -> {
			boolean disable = id == null;
			remove.setDisable(disable);
			edit.setDisable(disable);
		});
		
		this.edit = edit;
		this.remove = remove;
		
		Platform.runLater(() -> start.requestFocus());
		
		HBox sets = new HBox(5, add, edit, remove, start);
		VBox vbox = new VBox(20, this.stars, sets);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		Scene scene = new Scene(vbox, WIDTH, HEIGHT);
		this.scene = scene;
	}
	
	public Scene createScene() {
		SimulationBoard board = SimulationBoard.save;
		if(board != null) {
			this.stars.getItems().clear();
			this.stars.getItems().addAll(board.getStars());
		}
		this.edit.setDisable(true);
		this.remove.setDisable(true);
		return this.scene;
	}
	
	
	
	public Dialog<Star> createStarDialog(final Star star) {
		Dialog<Star> dialog = new Dialog<>();
		dialog.setTitle("Adding a star");
		dialog.setHeaderText("Complete these fields to add a star.");
		dialog.setGraphic(new ImageView(this.getClass().getResource("star_img.png").toString()));
		
		// Buttons
		ButtonType addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
		
		// Create GridPane with inputs
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		TextField nameInput = new TextField();
		nameInput.setPromptText(star.getName());
		TextField massInput = new TextField();
		massInput.setPromptText(String.valueOf(star.getMass()));
		TextField radiusInput = new TextField();
		radiusInput.setPromptText(String.valueOf(star.getRadius()));
		TextField x0Input = new TextField();
		x0Input.setPromptText(String.valueOf(star.getX0()));
		TextField y0Input = new TextField();
		y0Input.setPromptText(String.valueOf(star.getY0()));
		TextField vx0Input = new TextField();
		vx0Input.setPromptText(String.valueOf(star.getVx0()));
		TextField vy0Input = new TextField();
		vy0Input.setPromptText(String.valueOf(star.getVy0()));
		TextField colorInput = new TextField();
		colorInput.setPromptText(star.getColor().toString().toLowerCase());
		
		grid.add(new Label("Name : "), 0, 0);
		grid.add(nameInput, 1, 0);
		grid.add(new Label("Mass (kg) : "), 0, 1);
		grid.add(massInput, 1, 1);
		grid.add(new Label("Radius (m) : "), 0, 2);
		grid.add(radiusInput, 1, 2);
		grid.add(new Label("x0 (m) : "), 0, 3);
		grid.add(x0Input, 1, 3);
		grid.add(new Label("y0 (m)"), 0, 4);
		grid.add(y0Input, 1, 4);
		grid.add(new Label("vx0 (m/s)"), 0, 5);
		grid.add(vx0Input, 1, 5);
		grid.add(new Label("vy0 (m/s)"), 0, 6);
		grid.add(vy0Input, 1, 6);
		grid.add(new Label("Color : "), 0, 7);
		grid.add(colorInput, 1, 7);
		
		// Disabling/Enabling adding button
		Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
		addButton.setDisable(true);
		
		nameInput.textProperty().addListener((observable, oldValue, newValue) -> {
			String name = newValue.trim();
			addButton.setDisable(name.isEmpty());
			star.setName(name);
			this.stars.getItems().forEach(registred -> {
				if(registred.getName().equalsIgnoreCase(name))
					addButton.setDisable(true);
			});
		});
		massInput.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setMass(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		radiusInput.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setRadius(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		x0Input.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setX0(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		y0Input.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setY0(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		vx0Input.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setVx0(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		vy0Input.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				star.setVy0(Double.parseDouble(newValue.trim()));
				addButton.setDisable(false);
			} catch (Exception e) {
				addButton.setDisable(true);
			}
		});
		colorInput.textProperty().addListener((observable, oldValue, newValue) -> {
			String color = newValue.trim();
			try {
				star.setColor(Color.valueOf(color.toUpperCase()));
				addButton.setDisable(false);
			} catch (Exception e) {
				try {
					star.setColor(Color.web(color));
					addButton.setDisable(false);
				} catch (Exception e1) {
					addButton.setDisable(true);
				}
			}
		});
		
		// Set default focus
		Platform.runLater(() -> nameInput.requestFocus());
		
		dialog.getDialogPane().setContent(grid);
		
		// Set converter
		dialog.setResultConverter(dialogButton -> {
			if(dialogButton.equals(addButtonType) && !addButton.isDisable()) {
				return star;
			}
			return null;
		});
		return dialog;
	}
	
	private ObservableList<Star> getDefaultItems() {
		ObservableList<Star> initial = FXCollections.observableArrayList();
		initial.addAll(
				new Star("Sun", 1.9891E30, 7E8, 0, 0, 0, 0, Color.ORANGERED),
				new Star("Earth", 5.9736E24, 6E6, 152_097_701_000D, 0, 0, 29_291, Color.NAVY),
				new Star("Moon", 7.3477E22, 1737400, 152_097_701_000D-400_300_000, 0, 0, 29_291-995, Color.WHITE),
				new Star("Mercure", 3.3011E23, 2439700, -46001E6, 0, 0, 58985.9, Color.DARKGREY),
				new Star("Venus", 4.8685E24, 6051800, 0, 108943E6, -34789.5, 0, Color.RED));
		return initial;
	}
}
