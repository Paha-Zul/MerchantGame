package com.quickbite.economy.gui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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

    float padLeft = 50, padBot = 25, padTop = 50, padRight = 50;

    private Label maxLabel, minLabel;

    public Graph(List<Integer> points, int maxPointsHoriz, GraphStyle style){
        this.points = points;
        this.maxPointsHoriz = maxPointsHoriz;
        this.style = style;

        Label.LabelStyle labelStyle = new Label.LabelStyle(style.font, style.fontColor);

        //TODO Figure out how to deal with the font timeScale from outside
        maxLabel = new Label("max", labelStyle);
        maxLabel.setSize(50f, 20f);
        maxLabel.setAlignment(Align.right);

        minLabel = new Label("min", labelStyle);
        minLabel.setSize(50f, 20f);
        minLabel.setAlignment(Align.right);

        setPoints(points);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float _x = this.getX() + padLeft;
        float _y = this.getY() + padBot;
        float _width = this.getWidth() - padLeft;
        float _height = this.getHeight() - padBot;

        if(style.background != null)
            style.background.draw(batch, getX(), getY(), getWidth(), getHeight());

        if(style.graphBackground != null)
            style.graphBackground.draw(batch, _x, _y, _width, _height);

        float heightScale = _height/Math.max((highestPoint - lowestPoint), 1);
        int size = points.size() - 1; //We are going to be doing i and i+1, so stay one back
        int count = Math.min(size, maxPointsHoriz); //Only loop to the smallest
        int offset = size - maxPointsHoriz;
        if(offset < 0)
            offset = 0;

        drawLabels( _x, _y, _y + _height, batch);

        if(size < 1)
            return;

        float midpoint = (highestPoint + lowestPoint)/2;

        for(int i=0; i < count; i++){
            int i1 = offset + i;
            int i2 = offset + i + 1;

            //use the adjusted indexCounter here for getting the points
            float point = points.get(i1);
            float nextPoint = points.get(i2);

            float yPosition = (point - lowestPoint)*heightScale + _y;
            float nextYPosition = (nextPoint - lowestPoint)*heightScale + _y;

            //Use the regular i indexCounter here for calculations
            tmp1.set(i*distanceBetweenHorizPoints + _x, yPosition);
            tmp2.set((i+1)*distanceBetweenHorizPoints + _x, nextYPosition );

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

    private void drawLabels(float xpos, float yMin, float yMax, Batch batch){
        minLabel.setPosition(xpos - minLabel.getWidth(), yMin);
        minLabel.draw(batch, 1f);

        maxLabel.setPosition(xpos - maxLabel.getWidth(), yMax - maxLabel.getHeight());
        maxLabel.draw(batch, 1f);

        minLabel.debug();
        maxLabel.debug();
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

        drawDebugLabels(shapes);

        float _x = this.getX() + padLeft;
        float _y = this.getY() + padBot;
        float _height = this.getHeight() - padBot;

        float heightScale = _height/Math.max((highestPoint - lowestPoint), 1);
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

            //use the adjusted indexCounter here for getting the points
            float point = points.get(i1);
            float nextPoint = points.get(i2);

            //Use the regular i indexCounter here for calculations
            tmp1.set(i*distanceBetweenHorizPoints + _x, (point - lowestPoint)*heightScale + _y);
            tmp2.set((i+1)*distanceBetweenHorizPoints +  _x, (nextPoint - lowestPoint)*heightScale + _y);

            //Draw the line
            shapes.line(tmp1, tmp2);

            //Draw the point
            shapes.arc(tmp1.x, tmp1.y, 5f, 0f, 360f);

            //If we are at the end, render the last point too!
            if(i == size - 1)
                shapes.arc(tmp2.x, tmp2.y, 5f, 0f, 360f);
        }
    }

    private void drawDebugLabels(ShapeRenderer shapes){
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.BLACK);

        shapes.rect(minLabel.getX(), minLabel.getY(), minLabel.getOriginX(), minLabel.getOriginY(), minLabel.getWidth(), minLabel.getHeight(),
                minLabel.getScaleX(), minLabel.getScaleY(), minLabel.getRotation());

        shapes.rect(maxLabel.getX(), maxLabel.getY(), maxLabel.getOriginX(), maxLabel.getOriginY(), maxLabel.getWidth(), maxLabel.getHeight(),
                maxLabel.getScaleX(), maxLabel.getScaleY(), maxLabel.getRotation());
    }

    public void setPoints(List<Integer> incomingPoints) {
        int start = Math.max(incomingPoints.size() - maxPointsHoriz, 0); //Don't let this be negative

        this.points = incomingPoints.subList(start, incomingPoints.size());

        float max = 0f;
        float min = Float.MAX_VALUE;
        for (float p : points) {
            max = Math.max(max, p);
            min = Math.min(min, p);
        }

        if(min == Float.MAX_VALUE)
            min = 0;

        maxLabel.setText(""+(int)max);
        minLabel.setText(""+(int)min);

        highestPoint = max;
        lowestPoint = min;
    }

    public List<Integer> getPoints() {
        return points;
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        distanceBetweenHorizPoints = (getWidth() - padLeft)/maxPointsHoriz;
    }

    /** The style for a label, see {@link Label}.
     * @author Nathan Sweet */
    static public class GraphStyle {
        /** Optional. */
        public Drawable background;

        /** Optional. */
        public Drawable graphBackground;

        /** Optional. */
        public Drawable lineDrawable;

        /** Optional. */
        public Color lineColor = new Color(0f,0f,0f,1f);

        public BitmapFont font;

        public Color fontColor = new Color(1f,1f,1f,1f);

        /** Optional. */
        public Drawable pointDrawable;

        /** Optional. */
        public Color pointColor = new Color(0f,0f,0f,1f);

        public float lineThickness = 1f;

        public float pointThickness = 5f;

        public GraphStyle () {

        }

        public GraphStyle (TextureRegionDrawable lineDrawable, Color lineColor, BitmapFont font) {
            this.lineDrawable = lineDrawable;
            this.lineColor = lineColor;
            this.font = font;
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
