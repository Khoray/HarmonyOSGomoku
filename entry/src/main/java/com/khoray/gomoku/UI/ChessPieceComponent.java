package com.khoray.gomoku.UI;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class ChessPieceComponent extends Component implements Component.DrawTask, Component.TouchEventListener, Component.EstimateSizeListener {
    Paint chessPainter, linePainter, chessFillPainter, focusingPaint;

    public int type = 0, width, height;
    boolean isFocusing;
    public int posX, posY;
    float focusingX, focusingY;

    public interface ClickedListener {
        void click(ChessPieceComponent cpc);
    }

    ClickedListener listener;

    public ChessPieceComponent(Context context, ClickedListener listener) {
        this(context, (AttrSet) null, listener);
    }

    public ChessPieceComponent(Context context, AttrSet attrSet, ClickedListener listener) {
        super(context, attrSet);
        this.listener = listener;
        focusingPaint = new Paint();
        focusingPaint.setColor(Color.RED);
        focusingPaint.setStrokeWidth(5);
        focusingPaint.setStyle(Paint.Style.STROKE_STYLE);

        chessPainter = new Paint();
        chessPainter.setColor(Color.BLACK);
        chessPainter.setStrokeWidth(6);
        chessPainter.setStyle(Paint.Style.STROKE_STYLE);

        linePainter = new Paint();
        linePainter.setColor(Color.BLACK);
        linePainter.setStrokeWidth(5);
        linePainter.setStyle(Paint.Style.STROKE_STYLE);

        chessFillPainter = new Paint();
        chessFillPainter.setStyle(Paint.Style.FILLANDSTROKE_STYLE);

        setEstimateSizeListener(this);
        setTouchEventListener(this);
        addDrawTask(this);
    }

    @Override
    public boolean onEstimateSize(int widthEstimateConfig, int heightEstimateConfig) {
        int widthSpce = EstimateSpec.getMode(widthEstimateConfig);
        int heightSpce = EstimateSpec.getMode(heightEstimateConfig);

        int widthConfig = 0;
        switch (widthSpce) {
            case EstimateSpec.UNCONSTRAINT:
            case EstimateSpec.PRECISE:
                width = EstimateSpec.getSize(widthEstimateConfig);
                widthConfig = EstimateSpec.getSizeWithMode(width, EstimateSpec.PRECISE);
                break;
            case EstimateSpec.NOT_EXCEED:
                widthConfig = EstimateSpec.getSizeWithMode(width, EstimateSpec.PRECISE);
                break;
            default:
                break;
        }

        int heightConfig = 0;
        switch (heightSpce) {
            case EstimateSpec.UNCONSTRAINT:
            case EstimateSpec.PRECISE:
                height = EstimateSpec.getSize(heightEstimateConfig);
                heightConfig = EstimateSpec.getSizeWithMode(height, EstimateSpec.PRECISE);
                break;
            case EstimateSpec.NOT_EXCEED:
                heightConfig = EstimateSpec.getSizeWithMode(height, EstimateSpec.PRECISE);
                break;
            default:
                break;
        }
        System.out.println("WYT_width:" + width + "   height:" + height + "     width_spec:" + widthSpce + "     height_spec:" + heightSpce);
        setEstimatedSize(widthConfig, heightConfig);
        return true;
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        // draw bg
        if(posX != 0) canvas.drawLine(width / 2f, 0f, width / 2f, height / 2f, linePainter);
        if(posX != 14) canvas.drawLine(width / 2f, height / 2f, width / 2f, height, linePainter);
        if(posY != 0) canvas.drawLine(0f, height / 2f, width / 2f, height / 2f, linePainter);
        if(posY != 14) canvas.drawLine(width / 2f, height / 2f, width, height / 2f, linePainter);


        if(type == 2) {
            chessFillPainter.setColor(Color.WHITE);
            canvas.drawCircle(width / 2.0f, height / 2.0f, width / 2.0f - 3, chessPainter);
            canvas.drawCircle(width / 2.0f, height / 2.0f, width / 2.0f - 5.8f, chessFillPainter);
        } else if(type == 1) {
//            canvas.drawLine(0, 0, width, height, chessPainter);
//            canvas.drawLine(0, height, width, 0, chessPainter);
            chessFillPainter.setColor(Color.BLACK);
            canvas.drawCircle(width / 2f, height / 2f, width / 2f - 3, chessPainter);
            canvas.drawCircle(width / 2f, height / 2f, width / 2f - 5.8f, chessFillPainter);
//            canvas.draw(width / 2, height / 2, width / 2, chessPainter);
        } else {
            if(isFocusing) {
                canvas.drawRect(0, 0, width, height, focusingPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        System.out.println("on Touch by khoray");
        int index = touchEvent.getIndex();
        MmiPoint p = touchEvent.getPointerPosition(index);
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                System.out.println("hahaha pressed down");
                focusingX = p.getX();
                focusingY = p.getY();
                isFocusing = true;
                invalidate();
                break;
            case TouchEvent.PRIMARY_POINT_UP:
                System.out.println("hahaha pressed up");
                isFocusing = false;
                if(Math.abs(focusingX - p.getX()) <= 30 && Math.abs(focusingY - p.getY()) <= 30) listener.click(this);
                invalidate();
                break;

        }
        return true;
    }
}
