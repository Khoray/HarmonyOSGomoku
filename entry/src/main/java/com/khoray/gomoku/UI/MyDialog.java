package com.khoray.gomoku.UI;

import com.khoray.gomoku.ResourceTable;
import ohos.agp.components.*;
import ohos.agp.window.dialog.CommonDialog;
import ohos.app.Context;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

public class MyDialog {
    CommonDialog cd;
    public interface ClickedListener {
        public void click(MyDialog myDialog);
    }
    public MyDialog(Context context, String title, String text, String btn1Str, String btn2Str, ClickedListener listener1, ClickedListener listener2, CommonDialog.DestroyedListener destroyedListener) {
        cd = new CommonDialog(context);

        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_my_dialog, null, false);
        Text titleText = dl.findComponentById(ResourceTable.Id_mydialog_title);
        titleText.setText(title);
        Text textText = dl.findComponentById(ResourceTable.Id_mydialog_text);
        textText.setText(text);
        Button btn1 = dl.findComponentById(ResourceTable.Id_mydialog_btn1);
        btn1.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                listener1.click(MyDialog.this);
            }
        });
        btn1.setText(btn1Str);
        Button btn2 = dl.findComponentById(ResourceTable.Id_mydialog_btn2);
        btn2.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                listener2.click(MyDialog.this);
            }
        });
        btn2.setText(btn2Str);
        cd.setSize(600, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.setDestroyedListener(destroyedListener);
        cd.show();

    }
    public void destroy() {
        cd.destroy();
    }
}
