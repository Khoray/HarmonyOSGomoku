package com.khoray.gomoku;

import com.khoray.gomoku.slice.SingleGameAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class SingleGameAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(SingleGameAbilitySlice.class.getName());
    }
}
