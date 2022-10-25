package net.lizardnetwork.environmentadditions.models;

public class ModelBiomeEvent {
    private final ModelCondition condition = new ModelCondition();
    private final ModelCommand[] commands = new ModelCommand[]{};
    private final ModelParticle[] particles = new ModelParticle[]{};
    private final ModelSound[] sounds = new ModelSound[]{};

    public ModelCondition getCondition() {
        return condition;
    }

    public ModelCommand[] getCommands() {
        return commands;
    }

    public ModelParticle[] getParticles() {
        return particles;
    }

    public ModelSound[] getSounds() {
        return sounds;
    }
}
