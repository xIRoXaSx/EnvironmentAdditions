package net.lizardnetwork.biomeevents.models;

import java.util.List;

public class CommandModel {
    private List<String> CommandList;
    private ConditionModel conditions = new ConditionModel();
    private boolean pickRandomCommand;

    /**
     * Get the list of commands which should be executed
     * @return <code>List&lt;String&gt;</code> - The list of commands
     */
    public List<String> getCommandList() {
        return CommandList;
    }

    /**
     * Get the ConditionModel of this CommandModel
     * @return <code>ConditionModel</code> - The conditions which must apply in order to execute the commands
     */
    public ConditionModel getConditions() {
        return conditions;
    }

    /**
     * Check whether one random or all CommandModels or just should be picked
     * @return <code>Boolean</code> - Represents whether one random or all CommandModels or just should be picked
     */
    public boolean isRandom() {
        return pickRandomCommand;
    }

    /**
     * Get whether one random or all CommandModels or just should be picked
     * @param commandList <code>List&lt;String&gt;</code> - The list of commands
     */
    public void setCommandList(List<String> commandList) {
        CommandList = commandList;
    }

    /**
     * Set the ConditionModel of this CommandModel
     * @param conditions <code>ConditionModel</code> - The conditions which must apply in order to execute the commands
     */
    public void setConditions(ConditionModel conditions) {
        this.conditions = conditions;
    }

    /**
     * Set whether one random or all CommandModels or just should be picked
     * @param isRandom <code>Boolean</code> - Represents whether one random or all CommandModels or just should be picked.
     */
    public void setRandom(boolean isRandom) {
        this.pickRandomCommand = isRandom;
    }
}
