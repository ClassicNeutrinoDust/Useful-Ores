package com.neutrinodust.useful_ores.util;

import net.minecraft.world.InteractionResult;

public enum ItemInteractionResult {
    SUCCESS, CONSUME, CONSUME_PARTIAL, PASS_TO_DEFAULT_BLOCK_INTERACTION, SKIP_DEFAULT_BLOCK_INTERACTION, FAIL;

    public boolean consumesAction() {
        return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
    }

    public InteractionResult result() {
        switch (this) {
            case SUCCESS: return InteractionResult.SUCCESS;
            case CONSUME:
            case CONSUME_PARTIAL: return InteractionResult.CONSUME;
            case FAIL: return InteractionResult.FAIL;
            default: return InteractionResult.PASS;
        }
    }

    public static ItemInteractionResult sidedSuccess(boolean clientSide) {
        return clientSide ? SUCCESS : CONSUME;
    }
}
