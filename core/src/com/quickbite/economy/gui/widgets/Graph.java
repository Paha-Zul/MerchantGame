package com.quickbite.economy.gui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.quickbite.economy.util.Util;

import java.util.List;

/**
 * Created by Paha on 4/10/2017.
 */
public class Graph extends Actor {
    private final int maxPointsHoriz;
    private final Graph.GraphStyle style;
    private List<Integer> points;

    private float highestPoint, lowestPoint;
    private float distanceBetweenHorizPoints = 0f;

    private Vector2 tmp1 = new Vector2();
    private Vector2 tmp2 = new Vector2();

    public Graph(List<Integer> points, int maxPointsHoriz, GraphStyle style){
        this.points = points;
        this.maxPointsHoriz = maxPointsHoriz;
        this.style = style;
        setPoints(points);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(style.background != null)
            style.background.draw(batch, getX(), getY(), getWidth(), getHeight());

        float heightScale = getHeight()/Math.max((highestPoint - lowestPoint), 1);
        int size = points.size() - 1; //We are going to be doing i and i+1, so stay one back
        int count = Math.min(size, maxPointsHoriz); //Only loop to the smallest
        int offset = size - maxPointsHoriz;
        if(offset < 0)
            offset = 0;

        if(size < 1)
            return;

        float midpoint = (highestPoint + lowestPoint)/2;

        for(int i=0; i < count; i++){
            int i1 = offset + i;
            int i2 = offset + i + 1;

            //use the adjusted index here for getting the points
            float point = points.get(i1);
            float nextPoint = points.get(i2);

            float yPosition = (point - lowestPoint)*heightScale;
            float nextYPosition = (nextPoint - lowestPoint)*heightScale;

            //Use the regular i index here for calculations
            tmp1.set(i*distanceBetweenHorizPoints + this.getX(), yPosition + this.getY());
            tmp2.set((i+1)*distanceBetweenHorizPoints + this.getX(), nextYPosition + this.getY());

            //Draw the line
            if(style.lineDrawable != null)
                Util.INSTANCE.drawLineTo(tmp1, tmp2, (TextureRegionDrawable)style.lineDrawable, style.lineThickness, batch);

            //Draw the points
            if(style.pointDrawable != null) {
                style.pointDrawable.draw(batch, tmp1.x, tmp1.y, style.pointThickness, style.pointThickness);
                if (i == size - 1)
                    style.pointDrawable.draw(batch, tmp2.x, tmp2.y, style.pointThickness, style.pointThickness);
            }
        }
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

        float heightScale = getHeight()/Math.max((highestPoint - lowestPoint), 1);
        int size = points.size() - 1; //We are going to be doing i and i+1, so stay one back
        int count = Math.min(size, maxPointsHoriz); //Only loop to the smallest
        int offset = size - maxPointsHoriz;
        if(offset < 0)
            offset = 0;

        if(size < 1)
            return;

        for(int i=0; i < count; i++){
            int i1 = offset + i;
            int i2 = offset + i + 1;

            //use the adjusted index here for getting the points
            float point = points.get(i1);
            float nextPoint = points.get(i2);

            //Use the regular i index here for calculations
            tmp1.set(i*distanceBetweenHorizPoints + this.getX(), (point - lowestPoint)*heightScale + this.getY());
            tmp2.set((i+1)*distanceBetweenHorizPoints + this.getX(), (nextPoint - lowestPoint)*heightScale + this.getY());

            //Draw the line
            shapes.line(tmp1, tmp2);

            //Draw the point
            shapes.arc(tmp1.x, tmp1.y, 5f, 0f, 360f);

            //If we are at the end, draw the last point too!
            if(i == size - 1)
                shapes.arc(tmp2.x, tmp2.y, 5f, 0f, 360f);
        }
    }

    public void setPoints(List<Integer> points) {
        this.points = points;

        float max = 0f;
        float min = Float.MAX_VALUE;
        for (float p : points) {
            max = Math.max(max, p);
            min = Math.min(min, p);
        }

        highestPoint = max;
        lowestPoint = min;
    }

    public List<Integer> getPoints() {
        return points;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        distanceBetweenHorizPoints = getWidth()/maxPointsHoriz;
    }

    /** The style for a label, see {@link Label}.
     * @author Nathan Sweet */
    static public class GraphStyle {
        /** Optional. */
        public Drawable background;

        /** Optional. */
        public Drawable lineDrawable;

        /** Optional. */
        public Color lineColor = new Color(0f,0f,0f,1f);

        /** Optional. */
        public Drawable pointDrawable;

        /** Optional. */
        public Color pointColor = new Color(0f,0f,0f,1f);

        public float lineThickness = 1f;

        public float pointThickness = 5f;

        public GraphStyle () {

        }

        public GraphStyle (TextureRegionDrawable lineDrawable, Color lineColor) {
            this.lineDrawable = lineDrawable;
            this.lineColor = lineColor;
        }

        public GraphStyle (TextureRegionDrawable lineDrawable, Color lineColor, TextureRegionDrawable pointDrawable, Color pointColor) {
            this.lineDrawable = lineDrawable;
            this.lineColor = lineColor;
            this.pointDrawable = pointDrawable;
            this.pointColor = pointColor;
        }

        public GraphStyle (Graph.GraphStyle style) {
            this.lineDrawable = style.lineDrawable;
            this.lineColor = style.lineColor;
            this.pointDrawable = style.pointDrawable;
            this.pointColor = style.pointColor;
        }
    }
}
