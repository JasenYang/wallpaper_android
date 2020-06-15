package cs.hku.wallpaper.service.model;

import android.util.Log;

import androidx.annotation.NonNull;

public class Point {
    private double x;
    private double y;
    private double z;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point RotateX(double r) {
        return new Point(x, Math.cos(r) * y - Math.sin(r) * z, Math.cos(r) * z + Math.sin(r) * y);
    }
    public Point RotateY(double r) {
        return new Point(Math.cos(r) * x + Math.sin(r) * z, y, Math.cos(r) * z - Math.sin(r) * x);
    }
    public Point RotateZ(double r) {
        return new Point(Math.cos(r) * x - Math.sin(r) * y, Math.sin(r) * x + Math.cos(r) * y, z);
    }

    @NonNull
    @Override
    public String toString() {
        return "x:"+x+" y:"+y+" z:"+z;
    }

    public Direction direction(Point point) {
        return new Direction(x-point.getX(), y-point.getY(), z-point.getZ());
    }
}

