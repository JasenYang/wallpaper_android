package cs.hku.wallpaper.service.model;

import java.util.PriorityQueue;

public class Flat {
    // 三个点确定平面
    private Point a;
    private Point b;
    private Point c;

    // 另外一个点用来确定平面的方向
    private Point d;

    public Flat(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
