package com.howlstudio.announcements;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class AnnounceCommand extends AbstractPlayerCommand {
    private final AnnouncementsManager manager;

    public AnnounceCommand(AnnouncementsManager manager) {
        super("announce", "[Staff] Broadcast a one-time message to all players. Usage: /announce <message>");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        String input = ctx.getInputString().trim();
        int space = input.indexOf(' ');
        if (space < 0 || space + 1 >= input.length()) {
            playerRef.sendMessage(Message.raw("§cUsage: /announce <message>"));
            return;
        }
        String msg = input.substring(space + 1);
        manager.broadcastMessage("§6[Announcement] §f" + msg);
        playerRef.sendMessage(Message.raw("§aAnnouncement broadcast to all players."));
    }
}
