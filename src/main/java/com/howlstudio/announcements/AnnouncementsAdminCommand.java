package com.howlstudio.announcements;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;
import java.util.List;

public class AnnouncementsAdminCommand extends AbstractPlayerCommand {
    private final AnnouncementsManager manager;

    public AnnouncementsAdminCommand(AnnouncementsManager manager) {
        super("announcements", "[Staff] Manage auto-announcements. Usage: /announcements <list|add|remove|interval|trigger>");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+", 3);
        String sub = parts.length > 1 ? parts[1].toLowerCase() : "list";

        switch (sub) {
            case "list" -> {
                List<String> msgs = manager.getAnnouncements();
                playerRef.sendMessage(Message.raw("§6§l--- Announcements (" + msgs.size() + ") ---"));
                playerRef.sendMessage(Message.raw("§7Interval: §e" + manager.getIntervalSeconds() + "s"));
                for (int i = 0; i < msgs.size(); i++) {
                    playerRef.sendMessage(Message.raw("§7[" + i + "] §f" + msgs.get(i)));
                }
            }
            case "add" -> {
                if (parts.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /announcements add <message>")); return; }
                manager.addAnnouncement(parts[2]);
                playerRef.sendMessage(Message.raw("§aAdded announcement #" + (manager.getCount() - 1) + "."));
            }
            case "remove" -> {
                if (parts.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /announcements remove <index>")); return; }
                try {
                    int idx = Integer.parseInt(parts[2]);
                    if (manager.removeAnnouncement(idx)) {
                        playerRef.sendMessage(Message.raw("§aRemoved announcement #" + idx + "."));
                    } else {
                        playerRef.sendMessage(Message.raw("§cInvalid index. Use /announcements list to see indices."));
                    }
                } catch (NumberFormatException e) {
                    playerRef.sendMessage(Message.raw("§cIndex must be a number."));
                }
            }
            case "interval" -> {
                if (parts.length < 3) { playerRef.sendMessage(Message.raw("§cUsage: /announcements interval <seconds>")); return; }
                try {
                    int secs = Integer.parseInt(parts[2]);
                    manager.setInterval(secs);
                    playerRef.sendMessage(Message.raw("§aInterval set to §e" + secs + "s§a."));
                } catch (NumberFormatException e) {
                    playerRef.sendMessage(Message.raw("§cInterval must be a number."));
                }
            }
            case "trigger" -> {
                manager.broadcastNext();
                playerRef.sendMessage(Message.raw("§aTriggered next announcement."));
            }
            default -> playerRef.sendMessage(Message.raw("§cUsage: /announcements <list|add|remove|interval|trigger>"));
        }
    }
}
