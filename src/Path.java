import java.awt.*;
import java.util.*;

public class Path {

    private Color color;
    private LinkedList<Dot> pathDots;

    public Path() {
        this.color = Color.GRAY;
        pathDots = new LinkedList<>();
    }

    public void addDot(Dot dot) {
        if (color.equals(Color.GRAY)) {
            pathDots.add(dot);
            if (!dot.isClearDot()) {
                color = dot.getColor();
                for (Dot d: pathDots) {
                    d.setColor(color);
                }
            }
        }
        else if (dot.isClearDot()) {
            pathDots.add(dot);
            dot.setColor(color);
        }
        else if (color.equals(dot.getColor())) {
            pathDots.add(dot);

        }
    }

    public boolean hasDot(Dot dot) {
        return pathDots.contains(dot);
    }

    public int getLength() {
        return pathDots.size();
    }

    public boolean isClosed() {
        if (pathDots.size() >= 2 && pathDots.indexOf(pathDots.peekLast()) != pathDots.size() - 1) {
            return true;
        }
        return false;
    }

    public Dot lastElement() {
        return pathDots.peekLast();
    }

    public Dot getElem(int index) {
        return pathDots.get(index);
    }

    public void removeElem(int index) {
        Dot removedDot = pathDots.get(index);
        if (removedDot.isClearDot() && pathDots.indexOf(removedDot) == pathDots.lastIndexOf(removedDot)) {
            removedDot.setColor(Color.GRAY);
        }
        pathDots.remove(index);
        int clearCount = 0;
        for (Dot d: pathDots) {
            if (d.isClearDot()) {
                clearCount++;
            }
        }
        if (clearCount == pathDots.size()) {
            for (Dot d: pathDots) {
                d.setColor(Color.GRAY);
            }
            color = Color.GRAY;
        }
    }

    public Color getColor() {
        return color;
    }


    public void draw(Graphics g) {
        g.setColor(color);
        if (pathDots.size()!= 0) {
            Object[] a = pathDots.toArray();
            Dot[] pathArray = Arrays.copyOf(a, a.length, Dot[].class);
            for (int i = 0; i < pathArray.length - 1; i++) {
                g.drawLine(pathArray[i].getCenterX(), pathArray[i].getCenterY(), pathArray[i + 1].getCenterX(),
                        pathArray[i + 1].getCenterY());
            }
        }
    }
}
