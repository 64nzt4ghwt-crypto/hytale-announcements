package com.howlstudio.announcements;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class AnnouncementsManager {

    private final Path dataDir;
    private final List<String> announcements = new ArrayList<>();
    private int intervalSeconds = 300; // 5 minutes
    private boolean randomOrder = false;
    private int currentIndex = 0;
    private String joinMessage = "§6Welcome to the server! Use §e/vote §6to earn rewards.";

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> task;

    public AnnouncementsManager(Path dataDir) {
        this.dataDir = dataDir;
        loadDefaults();
        load();
    }

    private void loadDefaults() {
        announcements.add("§6[Server] §eUse §f/vote §eto vote for us and earn great rewards!");
        announcements.add("§6[Server] §fNeed help? Type §e/help §for ask a staff member.");
        announcements.add("§6[Server] §fReport rule-breakers with §e/warn §f— keep our server clean!");
        announcements.add("§6[Server] §eDid you know? §fYou can claim free kits with §e/kit§f!");
        announcements.add("§6[Server] §fJoin our Discord for events, giveaways, and announcements!");
        announcements.add("§6[Server] §fTop voters this month earn §6exclusive cosmetics§f. Vote with §e/vote§f!");
    }

    public void startScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "announcements-scheduler");
            t.setDaemon(true);
            return t;
        });
        scheduleNext();
    }

    private void scheduleNext() {
        if (scheduler == null || scheduler.isShutdown()) return;
        task = scheduler.schedule(this::broadcastNext, intervalSeconds, TimeUnit.SECONDS);
    }

    public void broadcastNext() {
        if (announcements.isEmpty()) { scheduleNext(); return; }

        int idx = randomOrder
            ? ThreadLocalRandom.current().nextInt(announcements.size())
            : currentIndex % announcements.size();

        broadcastMessage(announcements.get(idx));

        if (!randomOrder) currentIndex = (currentIndex + 1) % announcements.size();
        scheduleNext();
    }

    public void broadcastMessage(String message) {
        try {
            Universe.get().getPlayers().forEach(ref ->
                ref.sendMessage(Message.raw(message))
            );
        } catch (Exception ignored) {}
    }

    public void stopScheduler() {
        if (task != null) task.cancel(false);
        if (scheduler != null) scheduler.shutdownNow();
    }

    // --- Accessors ---
    public List<String> getAnnouncements() { return Collections.unmodifiableList(announcements); }
    public int getCount() { return announcements.size(); }
    public int getIntervalSeconds() { return intervalSeconds; }
    public String getJoinMessage() { return joinMessage; }

    public void addAnnouncement(String msg) { announcements.add(msg); save(); }
    public boolean removeAnnouncement(int index) {
        if (index < 0 || index >= announcements.size()) return false;
        announcements.remove(index);
        save();
        return true;
    }
    public void setInterval(int seconds) {
        this.intervalSeconds = Math.max(10, seconds);
        if (task != null) task.cancel(false);
        scheduleNext();
        save();
    }
    public void setJoinMessage(String msg) { this.joinMessage = msg; save(); }
    public void setRandomOrder(boolean random) { this.randomOrder = random; save(); }

    private void load() {
        Path file = dataDir.resolve("announcements.json");
        if (!Files.exists(file)) return;
        try {
            String json = Files.readString(file);
            // Simple JSON parse: look for "interval", "messages" array
            var intervalMatch = json.replaceAll(".*\"interval\":\\s*(\\d+).*", "$1");
            try { if (!intervalMatch.equals(json)) intervalSeconds = Integer.parseInt(intervalMatch.trim()); }
            catch (NumberFormatException ignored) {}

            // Parse messages array
            int start = json.indexOf("[");
            int end = json.lastIndexOf("]");
            if (start >= 0 && end > start) {
                String arr = json.substring(start + 1, end);
                announcements.clear();
                for (String item : arr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
                    String cleaned = item.trim().replaceAll("^\"(.*)\"$", "$1");
                    if (!cleaned.isEmpty()) announcements.add(cleaned);
                }
            }
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            Files.createDirectories(dataDir);
            StringBuilder sb = new StringBuilder("{\n  \"interval\": ")
                .append(intervalSeconds).append(",\n  \"random\": ").append(randomOrder)
                .append(",\n  \"joinMessage\": \"").append(joinMessage.replace("\"", "\\\"")).append("\",\n  \"messages\": [\n");
            for (int i = 0; i < announcements.size(); i++) {
                sb.append("    \"").append(announcements.get(i).replace("\"", "\\\"")).append("\"");
                if (i < announcements.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n}");
            Files.writeString(dataDir.resolve("announcements.json"), sb.toString());
        } catch (IOException ignored) {}
    }
}
