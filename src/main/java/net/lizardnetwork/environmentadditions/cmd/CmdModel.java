package net.lizardnetwork.environmentadditions.cmd;

public class CmdModel {
    private final String cmd;
    private final String permission;
    private final String[] aliases;
    private final String description;
    private final String usage;

    public CmdModel(String command, String permission, String[] aliases, String description, String usage) {
        this.cmd = command;
        this.permission = permission;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
    }

    public String getCmd() {
        return cmd;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
