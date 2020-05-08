package be.vilevar.gravitation;

public class SimulationTime {

	private final double t;
	private final Vec2d position;
	private final Vec2d speed;
	private final Vec2d acceleration;
	
	public SimulationTime(double t, Vec2d position, Vec2d speed, Vec2d acceleration) {
		this.t = t;
		this.position = position;
		this.speed = speed;
		this.acceleration = acceleration;
	}

	public double getT() {
		return t;
	}

	public Vec2d getPosition() {
		return position;
	}

	public Vec2d getSpeed() {
		return speed;
	}

	public Vec2d getAcceleration() {
		return acceleration;
	}
}
