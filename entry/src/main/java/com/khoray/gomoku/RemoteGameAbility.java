package com.khoray.gomoku;

import com.khoray.gomoku.slice.RemoteGameAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class RemoteGameAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(RemoteGameAbilitySlice.class.getName());
    }
}
