package be.vilevar.gravitation;

import java.math.BigDecimal;

import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SimulationBoard {

	public static final BigDecimal DIVIDER = new BigDecimal(400_000_000D);
	public static SimulationBoard save = null;
	
	private final Gravitation gravitation;
	private final ObservableList<Star> stars;
	private final Simulator simulator;
	private CheckBox keep;
	private Canvas canvas;
	private Scene scene;
	
	private ChangeListener<? super Status> statusListener;
	
	public SimulationBoard(Gravitation gravitation, ObservableList<Star> stars) {
		this.gravitation = gravitation;
		this.stars = FXCollections.observableArrayList();
		this.createStars(stars, false);
		this.simulator = new Simulator(this);
	}
	
	public Scene createScene() {
		// Get the save if it exist
		if(this.scene != null) {
			this.draw(BigDecimal.ZERO);
			this.simulator.start();
			return this.scene;
		}
		
		// Else create a new scene 
		BorderPane root = new BorderPane();
		
		
		// Useful buttons
		GSpinner precision = new GSpinner(1, 10000, Simulator.PRECISION.intValue(), 100);
		Simulator.PRECISION.bind(precision.getValueFactory().valueProperty());
		// TODO in java 9 -> modify javafx.scene.control.Tooltip delay
		Tooltip precisionTooltip = new Tooltip("The precision is the minimum dt (s) computed (and not showed).\nA too high value can cause error in the orbits"
				+ " but a too low value can cause\nstack over flow if you don't adapt the dt and the speed.");
		precision.setTooltip(precisionTooltip);
		precision.getEditor().setTooltip(precisionTooltip);
		precision.setPrefWidth(175);
		
		GSpinner dt = new GSpinner(1, 1_000_000_000, Simulator.DT.intValue(), 1000);
		Simulator.DT.bind(dt.getValueFactory().valueProperty());
		Tooltip dtTooltip = new Tooltip("The dt is the time in seconds between two points in the screen.\nA too high value can cause stack over flow"
				+ " because there are more calculs to do in one time.");
		dt.setTooltip(dtTooltip);
		dt.getEditor().setTooltip(dtTooltip);
		dt.setPrefWidth(175);
		
		GSpinner speed = new GSpinner(25, 3600_000, Simulator.SPEED.intValue(), 100);
		Simulator.SPEED.bind(speed.getValueFactory().valueProperty());
		Tooltip speedTooltip = new Tooltip("The speed is the real time in milli seconds between two update of the screen (and computing).\n"
				+ "A too low speed can cause stack over flow.");
		speed.setTooltip(speedTooltip);
		speed.getEditor().setTooltip(speedTooltip);
		speed.setPrefWidth(175);
		
		CheckBox keep = new CheckBox("Keep route");
		keep.setSelected(true);
		this.keep = keep;
		
		Button infos = new Button("Infos");
		infos.setOnAction(e -> this.stars.forEach(s -> s.createWindow().show()));
		
		Button pause = new Button("Pause");
		pause.setOnAction(e -> {
			if(this.simulator.isRunning())
				this.simulator.pause();
			else
				this.simulator.start();
		});
		this.simulator.statusProperty().addListener(this.statusListener = (obs, old, status) -> pause.setText(status == Status.RUNNING ? "Pause" : "Play"));
		
		Button save = new Button("Save");
		save.setOnAction(e -> this.save());
		
		Button destroy = new Button("Destroy");
		destroy.setOnAction(e -> SimulationBoard.save = null);
		destroy.setTooltip(new Tooltip("Destroy the save only !"));
		
		Button stop = new Button("Stop");
		stop.setOnAction(e -> {
			this.simulator.stop();
			this.stars.forEach(s -> s.closeWindow());
			this.gravitation.getWindow().setScene(HomeGui.getScene(this.gravitation));
		});
		
		ToolBar toolbar = new ToolBar(precision, dt, speed, keep, infos, pause, save, destroy, stop);
		toolbar.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
		toolbar.setPrefHeight(20);
		
		root.setTop(toolbar);
		
		
		// Graphics
		this.canvas = new Canvas(800, 800);
		Pane body = new Pane(new Group(this.canvas));
		body.setPrefSize(800, 800);
		body.widthProperty().addListener((obs, old, value) -> this.canvas.setWidth(value.doubleValue()));
		body.heightProperty().addListener((obs, old, value) -> this.canvas.setHeight(value.doubleValue()));
		body.setOnMouseClicked(e -> body.requestFocus());
		body.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		this.draw(BigDecimal.ZERO);
		this.simulator.start();
		root.setCenter(body);
		
		Scene scene = new Scene(root);
		scene.setCamera(new PerspectiveCamera());
		scene.setOnScroll(e -> {
			if(e.isControlDown())
				this.canvas.translateZProperty().set(this.canvas.getTranslateZ() - 200*Math.signum(e.getDeltaY()));
		});
		
		Platform.runLater(() -> body.requestFocus());
		
		return this.scene = scene;
	}
	
	public void draw(BigDecimal time) {
		GraphicsContext ctx = this.canvas.getGraphicsContext2D();
		if(!keep.isSelected()) {
			ctx.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		}
		this.stars.forEach(star -> {
			ctx.setFill(star.getColor());
			Vec2d pos = this.convertToScreenVec(star.getPosition());
			double radius = star.getScreenRadius(this);
			ctx.fillOval(pos.getX(), pos.getY(), radius, radius);
			if(star.getWindow() != null)
				star.getWindow().update(time);
		});
	}
	
	public Vec2d convertToScreenVec(Vec2d pos) {
		double width = 800; // this.canvas.getWidth();
		double height = 800; // this.canvas.getHeight();
	//	if(width == 0) width = 800;
	//	if(height == 0) height = 800;
		pos = pos.clone().divide(DIVIDER);
		return new Vec2d(pos.getX() + (width / 2), -pos.getY() + (height / 2));
	}
	
	public double convertToScreenNumber(double n) {
		return new BigDecimal(n).divide(DIVIDER, Vec2d.CTX).doubleValue();
	}
	
	public void save() {
		save = this;
		this.stars.forEach(s -> s.save());
	}
	
	public ObservableList<Star> getStars() {
		return stars;
	}
	
	public ChangeListener<? super Status> getStatusListener() {
		return statusListener;
	}
	
	public void createStars(ObservableList<Star> src, boolean simulator) {
		this.stars.clear();
		src.forEach(s -> this.stars.add(s.clone()));
		if(simulator)
			this.simulator.setStars(this.stars);
	}
}
