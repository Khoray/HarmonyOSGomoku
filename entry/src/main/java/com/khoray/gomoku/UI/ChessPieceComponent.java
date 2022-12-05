package com.khoray.gomoku.UI;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

public class ChessPieceComponent extends Component implements Component.DrawTask, Component.TouchEventListener, Component.EstimateSizeListener {
    Paint chessPainter, backgroundPainter;

    public int type = 0, width, height;
    boolean isFocusing;
    public int posX, posY;

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
        chessPainter = new Paint();
        chessPainter.setColor(Color.BLACK);
        chessPainter.setStrokeWidth(20);
        chessPainter.setStyle(Paint.Style.STROKE_STYLE);
        backgroundPainter = new Paint();
        backgroundPainter.setColor(Color.GRAY);
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
        if(type == 2) {
            canvas.drawCircle(width / 2, height / 2, width / 2, chessPainter);
        } else if(type == 1) {
            canvas.drawLine(0, 0, width, height, chessPainter);
            canvas.drawLine(0, height, width, 0, chessPainter);
        } else {
            if(isFocusing) {
                canvas.drawRect(0, 0, width, height, backgroundPainter);
            }
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        System.out.println("on Touch by khoray");
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                System.out.println("hahaha pressed down");
                isFocusing = true;
                invalidate();
                break;
            case TouchEvent.PRIMARY_POINT_UP:
                System.out.println("hahaha pressed up");
                isFocusing = false;
                listener.click(this);
                invalidate();
                break;

        }
        return true;
    }
}
