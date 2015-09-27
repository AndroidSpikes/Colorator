package org.bitsome.colorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorChooser extends View {

    static float[] hsv = { 0, 1f, 1f };
    float offsetX;
    float offsetY;
    float outerRadius;
    int selectionWidth = 150;
    int selectionStroke = 30;

    int[] colors = { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED };

    private int colorBackground = Color.BLACK;
    private int colorSelected = Color.RED;
    private int selectedDegree = 0;

    private Paint paintGradient = new Paint();
    private Paint paintSelectionFill = new Paint();
    private Paint paintSelectionStroke = new Paint();
    private Paint paintBackground = new Paint();
    private SweepGradient sweepGradient;

    public ColorChooser(Context context) {
        super(context);
        setup();
    }

    public ColorChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ColorChooser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    public int getColor() {
        return colorSelected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        offsetX = width/2;
        offsetY = height/2;
        outerRadius = Math.min(width,height)/2;
        sweepGradient = new SweepGradient(offsetX, offsetY, colors, null);
        setMeasuredDimension(width, height);
        updateValues();
    }

    public boolean onTouchEvent(MotionEvent e) {

        float xDiff = e.getX() - offsetX;
        float yDiff = e.getY() - offsetY;
        double angle = Math.atan2(yDiff, xDiff);

        selectedDegree = (int) Math.toDegrees(angle) + (angle < 0 ? 360 : 0);

        updateValues();
        invalidate();

        return true;
    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        canvas.drawColor(colorBackground);

        // Draw Color Circle
        canvas.drawCircle(offsetX, offsetY, outerRadius - selectionStroke, paintGradient);
        canvas.drawCircle(offsetX, offsetY, outerRadius - (selectionWidth - selectionStroke), paintBackground);

        float newAngle = (float) Math.toRadians(selectedDegree);

        // Draw selection knob

        float x = (float) (offsetX + ((outerRadius-selectionWidth/2)) * Math.cos(newAngle));
        float y = (float) (offsetY + ((outerRadius-selectionWidth/2)) * Math.sin(newAngle));
        canvas.drawCircle(x, y, selectionWidth/2, paintSelectionFill);
        canvas.drawCircle(x, y, selectionWidth/2, paintSelectionStroke);

        float ang = (float) Math.PI*2F/3F;
        float x2 = (float) (offsetX + (outerRadius-selectionWidth) * Math.cos(ang));
        float y2 = (float) (offsetY + (outerRadius-selectionWidth) * Math.sin(ang));
        float x3 = (float) (offsetX + ((outerRadius-selectionWidth) + 20) * Math.cos(ang*2));
        float y3 = (float) (offsetY + ((outerRadius-selectionWidth) + 20) * Math.sin(ang*2));
        float x4 = (float) (offsetX + ((outerRadius-selectionWidth) + 20) * Math.cos(ang*3));
        float y4 = (float) (offsetY + ((outerRadius-selectionWidth) + 20) * Math.sin(ang*3));
        Path path = new Path();
        path.reset();
        path.moveTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x2, y2);
        Paint tri = new Paint();
        tri.setColor(Color.WHITE);

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LinearGradient val = new LinearGradient(x2, y2, x3, y3, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        LinearGradient sat = new LinearGradient(x3, y3, x4, y4, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        ComposeShader merged = new ComposeShader(val, sat, PorterDuff.Mode.MULTIPLY);

        tri.setShader(merged);
        canvas.drawPath(path, tri);

    }

    private void setup() {
        paintSelectionStroke.setStyle(Paint.Style.STROKE);
        paintSelectionStroke.setStrokeWidth(selectionStroke);
        paintSelectionStroke.setColor(colorBackground);
        paintSelectionStroke.setAntiAlias(true);
        paintGradient.setAntiAlias(true);
        paintBackground.setAntiAlias(true);
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(colorBackground);
    }

    private void updateValues() {
        hsv[0] = selectedDegree;
        colorSelected = Color.HSVToColor(hsv);
        paintGradient.setShader(sweepGradient);
        paintSelectionFill.setColor(colorSelected);
    }
}