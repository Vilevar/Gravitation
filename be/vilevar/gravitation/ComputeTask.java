package be.vilevar.gravitation;

public class ComputeTask {}






















/*public class ComputeTask extends Task<Simulator> {

	public static final int DT = 100;
	private static final int TIMES = 100;
	private static final int SLEEP = 5;
	
	private final SimulationBoard simulationBoard;
	private final ObservableList<Star> stars;
//	private final HashMap<Star, HashMap<Double, SimulationTime>> simulation = new HashMap<>();
	private final double time;
//	private int duration;
	
	public ComputeTask(SimulationBoard simulation, int time, int duration) {
		this.simulationBoard = simulation;
		this.stars = simulation.getStars();
		this.time = (double) time * 31557600D; // * 365.25 * 24 * 3600;
	//	this.duration = duration;
		
		this.stars.forEach(s -> {
			s.initialize();
			s.setSimulation(new HashMap<>());
		//	this.simulation.put(s, new HashMap<>());
		//	Vec2d screenPos = simulation.convertToScreenVec(s.getPosition());
		//	s.setShape(new Circle(screenPos.getX(), screenPos.getY(), Math.ceil(simulation.convertToScreenNumber(s.getRadius())) + 5, s.getBodyColor()));
		//	s.setPath(new Path(new MoveTo(screenPos.getX(), screenPos.getY())));
		});
	}

	@Override
	protected Simulator call() throws Exception {
		double t;
		for(t = DT; t <= this.time;) {
			for(int j = 0; j < TIMES && t <= this.time; j++, t += DT) {
				this.compute(DT, t);
			}
			Thread.sleep(SLEEP);
			this.updateProgress(t, this.time);
		}
		if(t < this.time) {
			this.compute(this.time - t, t);
		}
		this.stars.forEach(star -> {
			HashMap<Double, SimulationTime> simulation = star.getSimulation();
			star.initialize();
			star.setSimulation(simulation);
		//	PathTransition animation = new PathTransition(Duration.seconds(this.duration), star.getPath(), star.getShape());
		//	animation.setCycleCount(1);
		//	animation.setInterpolator(Interpolator.LINEAR);
		//	star.setAnimation(animation);
		});
		return new Simulator(this.simulationBoard, DT, this.time);
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		boolean cancel = super.cancel(mayInterruptIfRunning);
		if(cancel)
			this.stars.forEach(s -> s.initialize());
		return cancel;
	}
	
	public void compute(double dt, double t) {
		// Compute accelerations
		this.stars.forEach(star -> star.setAcceleration(Vec2d.NULL.clone()));
		for(int i = 0; i < this.stars.size(); i++) {
			Star s1 = this.stars.get(i);
			for(int j = i + 1; j < this.stars.size(); j++) {
				Star s2 = this.stars.get(j);
				
				double u = G / Math.pow(s1.getPosition().distance(s2.getPosition()), 2);
				
				double a1 = u * s2.getMass();
				double a2 = u * s1.getMass();
				
				Vec2d acc = s2.getPosition().clone().subtract(s1.getPosition()).normalize();
				Vec2d acc1 = acc.clone().multiply(a1);
				Vec2d acc2 = acc.negate().multiply(a2);
				
				s1.getAcceleration().add(acc1);
				s2.getAcceleration().add(acc2);
			}
		}
		
		// Compute speeds and positions and add point to path
		this.stars.forEach(star -> {
			Vec2d dv = star.getAcceleration().clone().multiply(dt);
			star.getPosition().add(star.getSpeed().clone().multiply(dt)).add(dv.clone().multiply(dt).divide(2));
			star.getSpeed().add(dv);
			star.getSimulation().put(t, new SimulationTime(t, star.getPosition().clone(), star.getSpeed().clone(), star.getAcceleration().clone()));
		//	Vec2d screenPos = this.simulationBoard.convertToScreenVec(star.getPosition());
		//	star.getPath().getElements().add(new LineTo(screenPos.getX(), screenPos.getY()));
		});
	}
	
	/**
	 * Method from be.vilevar.gravitation.SimulationBoard
	 */
/*	public Optional<Boolean> compute() {
		if(this.simulator != null)
			throw new IllegalStateException("This simulation has computed yet.");
			
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Computing data's");
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Spinner<Integer> years = new Spinner<>();
		SpinnerValueFactory<Integer> yearsValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1);
		yearsValues.setConverter(new StringConverter<Integer>() {
			public String toString(Integer object) {
				return object.toString();
			}
			public Integer fromString(String string) {
				try {
					return Math.min(100, Math.max(Integer.parseInt(string), 1));
				} catch (Exception e) {
					return years.getValue();
				}
			}
		});
		years.setValueFactory(yearsValues);
		years.setEditable(true);
		TextField yearsEditor = years.getEditor();
		yearsEditor.focusedProperty().addListener((obs, old, value) -> {
			if(value) {
				yearsEditor.selectAll();
			} else {
				StringConverter<Integer> converter = yearsValues.getConverter();
				yearsValues.setValue(converter.fromString(yearsEditor.getText()));
				yearsEditor.setText(converter.toString(yearsValues.getValue()));
			}
		});
		
		Spinner<Integer> seconds = new Spinner<>();
		SpinnerValueFactory<Integer>  secondsValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 18000, 60, 10);
		secondsValues.setConverter(new StringConverter<Integer>() {
			public String toString(Integer object) {
				return object.toString();
			}
			public Integer fromString(String string) {
				try {
					return Math.min(18000, Math.max(Integer.parseInt(string), 1));
				} catch (Exception e) {
					return seconds.getValue();
				}
			}
		});
		seconds.setValueFactory(secondsValues);
		seconds.setEditable(true);
		TextField secondsEditor = seconds.getEditor();
		secondsEditor.focusedProperty().addListener((obs, old, value) -> {
			if(value) {
				secondsEditor.selectAll();
			} else {
				StringConverter<Integer> converter = secondsValues.getConverter();
				secondsValues.setValue(converter.fromString(secondsEditor.getText()));
				secondsEditor.setText(converter.toString(secondsValues.getValue()));
			}
		});
		
		Button compute = new Button("Compute");
		compute.setOnAction(event -> {
			Dialog<Boolean> computingDialog = new Dialog<>();
			computingDialog.setTitle("Computing...");
			
			Label label = new Label("Compute :");
			label.setPrefWidth(70);
			ComputeTask task = new ComputeTask(this, yearsValues.getValue(), secondsValues.getValue());
			ProgressBar progressBar = new ProgressBar(0);
			ProgressIndicator progressIndi = new ProgressIndicator(0);
		//	progressBar.progressProperty().unbind();
		//	progressIndi.progressProperty().unbind();
			progressBar.progressProperty().bind(task.progressProperty());
			progressIndi.progressProperty().bind(task.progressProperty());
			
			new Thread(task).start();
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
				try {
					this.simulator = task.get();
				} catch (InterruptedException | ExecutionException e1) {
					this.simulator = null;
				}
				computingDialog.close();
			});
			
			computingDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
			computingDialog.getDialogPane().setContent(new HBox(10, label, progressBar, progressIndi));
			computingDialog.setResultConverter(buttonType -> this.simulator != null);
			
			computingDialog.showAndWait();
			dialog.close();
		});
		
		Platform.runLater(() -> compute.requestFocus());
		
		grid.add(new Label("Earth years :"), 0, 0);
		grid.add(years, 1, 0);
		grid.add(new Label("Seconds"), 0, 1);
		grid.add(seconds, 1, 1);
		grid.add(compute, 1, 2);
		
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		dialog.setResultConverter(buttonType -> this.simulator != null);
		return dialog.showAndWait();
	}
}*/
