package com.khoray.gomoku;

import com.khoray.gomoku.slice.AboutAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class AboutAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(AboutAbilitySlice.class.getName());
    }
}
