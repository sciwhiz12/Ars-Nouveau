package com.hollingsworth.craftedmagic.spell.augment;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

public class AugmentAOE extends AbstractAugment {
    public AugmentAOE() {
        super(ModConfig.AugmentAOEID, "AOE");
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
