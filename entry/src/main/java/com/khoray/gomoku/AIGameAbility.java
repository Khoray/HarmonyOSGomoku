package com.khoray.gomoku;

import com.khoray.gomoku.slice.AIGameAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class AIGameAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(AIGameAbilitySlice.class.getName());
    }
}
