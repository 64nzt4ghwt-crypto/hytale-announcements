package com.howlstudio.announcements;

import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * AnnouncementsPlugin — Rotating automated server announcements.
 *
 * Features:
 *   - Configurable announcement list (stored in announcements.json)
 *   - Random or sequential rotation
 *   - Configurable interval (default: 5 minutes)
 *   - Color code support (§a, §b, etc.)
 *   - Join message (MOTD shown to player on connect)
 *   - Admin commands to add/remove/list/trigger announcements
 *
 * Commands:
 *   /announce <message>        — broadcast a one-time announcement (staff)
 *   /announcements list        — list all configured announcements (staff)
 *   /announcements add <msg>   — add a new announcement (staff)
 *   /announcements remove <n>  — remove announcement by index (staff)
 *   /announcements interval <s>— set interval in seconds (staff)
 *   /announcements trigger     — fire the next announcement now (staff)
 */
public final class AnnouncementsPlugin extends JavaPlugin {

    private AnnouncementsManager manager;

    public AnnouncementsPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[Announcements] Loading...");

        manager = new AnnouncementsManager(getDataDirectory());

        CommandManager cmd = CommandManager.get();
        cmd.register(new AnnounceCommand(manager));
        cmd.register(new AnnouncementsAdminCommand(manager));

        new AnnouncementsListener(manager).register();

        manager.startScheduler();

        System.out.println("[Announcements] Ready! " + manager.getCount()
            + " announcements, interval=" + manager.getIntervalSeconds() + "s.");
    }

    @Override
    protected void shutdown() {
        if (manager != null) {
            manager.stopScheduler();
            manager.save();
            System.out.println("[Announcements] Stopped.");
        }
    }
}
