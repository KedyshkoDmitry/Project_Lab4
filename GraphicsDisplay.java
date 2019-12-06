package bsu.rfe.java.group8.lab4.Kedyshko.var7;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel
{
    // СПИСОК КООРДИНАТ И ФЛАГОВЫЕ ПЕРЕМЕННЫЕ
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;

    // ГРАНИЦЫ ИНТЕРВАЛА И МАСШТАБ
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;

    // СТИЛИ РИСОВКИ ЛИНИЙ И ШРИФТ
    private BasicStroke graphicsStroke;
    private BasicStroke graphicsAbsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private Font axisFont;

    // ОПИСАНИЕ СТИЛЕЙ РИСОВКИ И ШРИФТА
    public GraphicsDisplay()
    {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{10, 10, 20, 10, 10, 10, 50, 10, 20, 10, 10}, 0.0f);
        graphicsAbsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{10, 10}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    // ЗАПИСЬ ДАННЫХ КООРДИНАТ
    public void showGraphics(Double[][] graphicsData)
    {
        this.graphicsData = graphicsData;
        repaint();
    }

    // МОДИФИКАТОРЫ
    public void setShowAxis(boolean showAxis)
    {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers)
    {
        this.showMarkers = showMarkers;
        repaint();
    }

    // ОТОБРАЖЕНИЕ
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (graphicsData==null || graphicsData.length==0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if (scale==scaleX)
        {
            double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY)
        {
            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        // НАСТРОЙКИ ХОЛСТА
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        // ОТОБРАЖЕНИЕ ЭЛЕМЕНТОВ
        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        paintAbsGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);

        // ВОЗВРАЩЕНИЕ К НАСТРОЙКАМ ХОЛСТА
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    // ОТРИСОВКА ГРАФИКА
    protected void paintGraphics(Graphics2D canvas)
    {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.YELLOW);
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++)
        {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i>0)
            {
                graphics.lineTo(point.getX(), point.getY());
            }
            else
                {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }

    // ОТРИСОВКА МОДУЛЬНОГО ГРАФИКА
    protected void paintAbsGraphics(Graphics2D absCanvas)
    {
        absCanvas.setStroke(graphicsAbsStroke);
        absCanvas.setColor(Color.BLACK);
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++)
        {
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    Math.abs(graphicsData[i][1]));
            if (i>0)
            {
                graphics.lineTo(point.getX(), Math.abs(point.getY()));
            }
            else {
                graphics.moveTo(point.getX(), Math.abs(point.getY()));
            }
        }
        absCanvas.draw(graphics);
    }

    // ОТРИСОВКА МАРКЕРОВ
    protected void paintMarkers(Graphics2D canvas)
    {
        for (Double[] point : graphicsData)
        {
            boolean check = false;
            double value = point[1];
            int temp = (int)value;
            int num = 1;
            System.out.println(temp);
            while(temp>=0)
            {
                temp -= num;
                num += 2;
                if(temp==0)
                {
                    check = true;
                }
            }

            if (check)
            {
                canvas.setColor(Color.GREEN);
                canvas.setPaint(Color.GREEN);
            }
            else
                {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            }

            canvas.setStroke(markerStroke);
            Point2D.Double center = xyToPoint(point[0], point[1]);
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, -5.5), shiftPoint(center, 5.5, 5.5)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, -5.5), shiftPoint(center, -5.5, 5.5)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -5.5, 5.5), shiftPoint(center, 5.5, 5.5)));
        }
    }

    // ОТРИСОВКА ОСЕЙ КООРДИНАТ
    protected void paintAxis(Graphics2D canvas)
    {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
            arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() - bounds.getY()));
        }

        if (minY<=0.0 && maxY>=0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }

    }

    // ПРЕОБРАЗОВАНИЕ КООРДИНАТ
    protected Point2D.Double xyToPoint(double x, double y)
    {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }

    // ВОЗВРАЩЕНИЕ ЭКЗЕМПЛЯРА КЛАССА
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}

