package com.khoray.gomoku.slice;

import com.khoray.gomoku.ResourceTable;
import com.khoray.gomoku.UI.ChessBoardView;
import com.khoray.gomoku.UI.ChessPieceComponent;
import com.khoray.gomoku.model.RemoteGame;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.CommonDialog;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

public class MainAbilitySlice extends AbilitySlice {
    Button singleGameBtn, AIGameBtn, remoteGameBtn;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        super.setUIContent(ResourceTable.Layout_ability_main);

        initComponents();
    }

    void initComponents() {
        singleGameBtn = findComponentById(ResourceTable.Id_single_game_btn);
        singleGameBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                present(new SingleGameAbilitySlice(), new Intent());
            }
        });
        AIGameBtn = findComponentById(ResourceTable.Id_ai_game_btn);
        AIGameBtn.setClickedListener(component -> present(new AIGameAbilitySlice(), new Intent()));

        remoteGameBtn = findComponentById(ResourceTable.Id_remote_game_btn);
        remoteGameBtn.setClickedListener(component -> {
            CommonDialog cd = new CommonDialog(getContext());

            DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_remote_ack, null, false);
            TextField tf = dl.findComponentById(ResourceTable.Id_room_id_text);
            Button joinRoom = dl.findComponentById(ResourceTable.Id_join_room);
            joinRoom.setClickedListener(c -> {
                Intent intent = new Intent();
                intent.setParam("roomID", tf.getText());
                present(new RemoteGameAbilitySlice(), intent);
                cd.destroy();
            });

            cd.setContentCustomComponent(dl);
            cd.setSize(600, MATCH_CONTENT);
            cd.show();

        });

    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
