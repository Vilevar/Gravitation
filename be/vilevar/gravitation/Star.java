package be.vilevar.gravitation;

import javafx.scene.paint.Color;

public class Star implements Cloneable {

	private String name;
	private double mass;
	private double radius;
	private double x0;
	private double y0;
	private double vx0;
	private double vy0;
	private Color color;
	
	private Vec2d position;
	private Vec2d speed;
	private Vec2d acc;
	
	public Star() {
		this("Name", 1, 0, 0, 0, 0, 0, Color.NAVY);
	}
	
	public Star(String name, double mass, double radius, double x0, double y0, double vx0, double vy0, Color color) {
		this.name = name;
		this.mass = mass;
		this.radius = radius;
		this.x0 = x0;
		this.y0 = y0;
		this.vx0 = vx0;
		this.vy0 = vy0;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getMass() {
		return mass;
	}
	
	public void setMass(double mass) {
		if(mass <= 0) throw new IllegalArgumentException("The mass of a star cannot be negative or 0.");
		this.mass = mass;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		if(radius < 0) throw new IllegalArgumentException("The radius of a star cannot be negative.");
		this.radius = radius;
	}
	
	public double getScreenRadius(SimulationBoard board) {
		return Math.ceil(board.convertToScreenNumber(this.radius)) + 2;
	}
	
	public double getX0() {
		return x0;
	}
	
	public void setX0(double x0) {
		this.x0 = x0;
	}
	
	public double getY0() {
		return y0;
	}
	
	public void setY0(double y0) {
		this.y0 = y0;
	}
	
	public double getVx0() {
		return vx0;
	}
	
	public void setVx0(double vx0) {
		this.vx0 = vx0;
	}
	
	public double getVy0() {
		return vy0;
	}
	
	public void setVy0(double vy0) {
		this.vy0 = vy0;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(color == null) throw new IllegalArgumentException("The color cannot be null");
		this.color = color;
	}

	
	public void initialize() {
		this.position = new Vec2d(this.x0, this.y0);
		this.speed = new Vec2d(this.vx0, this.vy0);
		this.acc = Vec2d.NULL;
	}

	public Vec2d getPosition() {
		return position;
	}

	public void setPosition(Vec2d position) {
		this.position = position;
	}

	public Vec2d getSpeed() {
		return speed;
	}

	public void setSpeed(Vec2d speed) {
		this.speed = speed;
	}
	
	public Vec2d getAcceleration() {
		return acc;
	}
	
	public void setAcceleration(Vec2d acc) {
		this.acc = acc;
	}
	
	public void save() {
		this.x0 = this.position.getX();
		this.y0 = this.position.getY();
		this.vx0 = this.speed.getX();
		this.vy0 = this.speed.getY();
	}
	
	@Override
	protected Star clone() {
		Star star = new Star(name, mass, radius, x0, y0, vx0, vy0, color);
		if(position != null)
			star.setPosition(position.clone());
		if(speed != null)
		star.setSpeed(speed.clone());
		if(acc != null)
			star.setAcceleration(acc.clone());
		return star;
	}
}
