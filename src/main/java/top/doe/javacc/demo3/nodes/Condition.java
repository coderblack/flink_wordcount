package top.doe.javacc.demo3.nodes;

public class Condition {

    Column left;
    Column right;
    String op;


    public Condition(Column left, Column right, String op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public Column getLeft() {
        return left;
    }

    public void setLeft(Column left) {
        this.left = left;
    }

    public Column getRight() {
        return right;
    }

    public void setRight(Column right) {
        this.right = right;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "left=" + left +
                ", right=" + right +
                ", op='" + op + '\'' +
                '}';
    }
}
