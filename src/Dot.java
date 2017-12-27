import java.awt.*;

public class Dot implements Comparable<Dot> {
    private int xCoord;
    private int yCoord;
    private boolean isClear;
    private Color color;

    public Dot(int xCoord, int yCoord, boolean isClear, Color color) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.isClear = isClear;
        this.color = color;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.fillOval(xCoord, yCoord, 40, 40);
        g2.setStroke(new BasicStroke(5));
        g2.setColor(color.darker());
        g2.drawOval(xCoord, yCoord, 40, 40);
    }

    public int getCenterX() {
        return xCoord + 20;
    }

    public int getCenterY() {
        return yCoord + 20;
    }

    public int getXIndex() {
        return (xCoord - 20)/60;
    }

    public int getYIndex() {
        return (yCoord - 20)/60;
    }

    public boolean isInside(int x, int y) {
        return 20 > Math.sqrt(Math.pow(getCenterX() - x, 2) + Math.pow(getCenterY() - y, 2));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public boolean isClearDot() {
        return isClear;
    }

    public void setClarity(boolean clear) {
        isClear = clear;
    }

    @Override
    public int compareTo(Dot d) {
        return 0;
    }

}