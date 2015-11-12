package Predictor;
import java.lang.Math;

public class Vector3D {
	/* Implements some basic vector arithmetic
	 * for our physical simulations and camera
	 * calculations. Coordinate system is:
	 *     x=eastness
	 *     y=northness
	 *     z=upness.
	 * all in English Units (so, __feet__.)
	 */
	private double x,y,z;//ft.
	public Vector3D() {
		x=y=z=0.0;
	}
	public Vector3D(double xx, double yy, double zz) {
		x=xx; y=yy; z=zz;
	}
	public boolean above_ground() {
		return z>0.0;
	}
	public Vector3D plus(Vector3D other) {
		return new Vector3D(x+other.x, y+other.y, z+other.z);
	}
	public Vector3D times(double scale) {
		return new Vector3D(x*scale, y*scale, z*scale);
	}
	public Vector3D minus(Vector3D other) {
		return this.plus(other.times(-1.0));
	}
	public void plus_equals(Vector3D other, double scale) {
		x += other.x*scale;
		y += other.y*scale;
		z += other.z*scale;
	}
	public void scale(double scale) {
		x *= scale;
		y *= scale;
		z *= scale;
	}
	public double norm2() {
		return x*x + y*y + z*z;
	}
	public double norm() {
		return java.lang.Math.sqrt(norm2());
	}
	public String str() {
		return "("+x+","+y+","+z+")";
	}
}
