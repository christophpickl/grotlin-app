package at.cpickl.agrotlin;

public class FloatPoint {

    private final float x;
    private final float y;

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    @Override
    public String toString() {
        return "FloatPoint[" + x + "/" + y + "]";
    }
}
