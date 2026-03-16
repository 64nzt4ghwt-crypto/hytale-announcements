package com.howlstudio.announcements;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class AnnouncementsListener {

    private final AnnouncementsManager manager;

    public AnnouncementsListener(AnnouncementsManager manager) {
        this.manager = manager;
    }

    public void register() {
        HytaleServer.get().getEventBus().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        PlayerRef ref = player.getPlayerRef();
        if (ref == null) return;

        String motd = manager.getJoinMessage();
        if (motd != null && !motd.isBlank()) {
            ref.sendMessage(Message.raw(motd));
        }
    }
}
