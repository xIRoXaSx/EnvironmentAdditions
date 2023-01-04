package net.lizardnetwork.environmentadditions.models;

public class ModelConditionBlock {
    private final String block;
    private final ModelPosOffset posOffset;

    public ModelConditionBlock(String block, ModelPosOffset offset) {
        this.block = block;
        this.posOffset = offset;
    }

    public String getMaterial() {
        return block;
    }

    public ModelPosOffset getPosOffset() {
        return posOffset;
    }
}
