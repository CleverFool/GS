package Predictor;

public class DropSimulator {
	/*
	 * all in English units:
	 *     mass in slugs,
	 *     length in feet,
	 *     time in seconds.
	 */
	private static double bomb_weight = 2.0; //slug-ft^2, i.e., pounds
	private static double shape_coeff = 1.0; //unitless. (for cylinder, ~0.85 - 1.15.)
	private static double air_density = 0.00286; //slugs/ft^3 (in Houston's 80Fahrenheit climate)
	private static double grav_accel = 32.17; //ft/s^2
	private static double bomb_area = 0.1; //ft^2 (cross-sectional area)
	
	private double bomb_mass = bomb_weight/grav_accel; //slugs
	private double drag_coeff = shape_coeff*bomb_area*air_density/2; //slugs / ft.
	private Vector3D grav_vec = new Vector3D(0.0,0.0,-grav_accel); //ft/s^2
	
	public DropSimulator() {
	}

	private Vector3D calculate_force(Vector3D vel, Vector3D wind) {
		Vector3D force = wind.minus(vel);
		force.scale(drag_coeff * force.norm()); // force goes as vel^2 
		force.plus_equals(grav_vec, bomb_mass);
		return force;
	}
	public Vector3D end_location(Vector3D pos, Vector3D vel, Vector3D wind, double dt) {
		double time=0.0;
		while(pos.above_ground()) {
			pos.plus_equals(vel, dt);
			vel.plus_equals(calculate_force(vel,wind), dt/bomb_mass);
			time += dt;
		}
		System.out.println("time="+time);
		return pos;
	}
}
