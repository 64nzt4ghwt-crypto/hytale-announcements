# AnnouncementsPlugin for Hytale

Rotating automated server announcements with configurable interval and MOTD.

## Commands
| Command | Description |
|---------|-------------|
| `/announce <message>` | Broadcast a one-time message (staff) |
| `/announcements list` | List all configured announcements |
| `/announcements add <msg>` | Add a new announcement |
| `/announcements remove <n>` | Remove announcement by index |
| `/announcements interval <s>` | Set broadcast interval (seconds) |
| `/announcements trigger` | Fire next announcement immediately |

## Features
- **Auto-rotation** — broadcasts announcements on a configurable schedule (default: 5 min)
- **Sequential or random** order
- **MOTD** — custom join message shown to players on connect
- **6 default announcements** included out of the box
- **Persistent config** — announcements saved to `announcements.json`
- **Color codes** — full `§` color code support
