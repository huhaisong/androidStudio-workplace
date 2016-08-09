package zengshi.test.cardboard2.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


public class Vector3d {
    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    public Vector3d(double xx, double yy, double zz) {
        this.set(xx, yy, zz);
    }

    //设置向量
    public void set(double xx, double yy, double zz) {
        this.x = xx;
        this.y = yy;
        this.z = zz;
    }

    //设置第i个元素的值为val
    public void setComponent(int i, double val) {
        if(i == 0) {
            this.x = val;
        } else if(i == 1) {
            this.y = val;
        } else {
            this.z = val;
        }

    }

    //设置成0向量
    public void setZero() {
        this.x = this.y = this.z = 0.0D;
    }

    //设置成other向量
    public void set(Vector3d other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    //缩放
    public void scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    //变成单位向量
    public void normalize() {
        double d = this.length();
        if(d != 0.0D) {
            this.scale(1.0D / d);
        }
    }

    //点乘
    public static double dot(Vector3d a, Vector3d b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    //获得向量的模
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    //a+b得到向量result
    public static void add(Vector3d a, Vector3d b, Vector3d result) {
        result.set(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    //a-b得到向量result
    public static void sub(Vector3d a, Vector3d b, Vector3d result) {
        result.set(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    //a叉乘b得到向量result
    public static void cross(Vector3d a, Vector3d b, Vector3d result) {
        result.set(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    //获得垂直于v的单位向量
    public static void ortho(Vector3d v, Vector3d result) {
        int k = largestAbsComponent(v) - 1;
        if(k < 0) {
            k = 2;
        }
        result.setZero();
        result.setComponent(k, 1.0D);
        cross(v, result, result);
        result.normalize();
    }

    public String toString() {
        return String.format("%+05f %+05f %+05f", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z)});
    }

    //获得三个元素绝对值最大的那个元素的位置
    public static int largestAbsComponent(Vector3d v) {
        double xAbs = Math.abs(v.x);
        double yAbs = Math.abs(v.y);
        double zAbs = Math.abs(v.z);
        return xAbs > yAbs?(xAbs > zAbs?0:2):(yAbs > zAbs?1:2);
    }

    public double maxNorm() {
        return Math.max(Math.abs(this.x), Math.max(Math.abs(this.y), Math.abs(this.z)));
    }

    public boolean sameValues(Vector3d other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }
}
