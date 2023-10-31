package de.mert.friendssystem.commands;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Friend Command
 */
public class FriendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final String USAGE_MESSAGE = "§cUsage: §7/friend §f[ add | remove | list | requests | acceptall ] <name>";

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommand can only be executed by a player");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage(USAGE_MESSAGE);
            return true;
        }

        Friends friends = new Friends(p.getUniqueId());
        OfflinePlayer friend = null;
        switch (args[0]) {
            case "add":
                if (args.length < 2) {
                    p.sendMessage(USAGE_MESSAGE);
                    return true;
                }

                if (args[1].equals(p.getName())) {
                    p.sendMessage("§cYou can't send yourself a friend request");
                    return true;
                }

                for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                    if (pl.getName().equals(args[1])) {
                        friend = pl;
                        break;
                    }
                }

                if (friend == null) {
                    p.sendMessage("§cCould not find player §7" + args[1]);
                    return true;
                }

                OfflinePlayer fFriend = friend;
                friends.addFriend(friend.getUniqueId(), status -> {
                    switch (status) {
                        case ERROR:
                            p.sendMessage("§cError occurred while adding your friend §7" + args[1]);
                            return;
                        case ALREADY_FRIENDS:
                            p.sendMessage("Already added you friend §7" + args[1]);
                            return;
                        case SUCCESSFUL_ADDED:
                            p.sendMessage("§6Friend §7" + args[1] + " §6added");
                            if (fFriend.isOnline()) {
                                fFriend.getPlayer().sendMessage("§7" + p.getName() + " §6accepted you friend request");
                            }

                            return;
                        case ALREADY_SEND_REQUEST:
                            p.sendMessage("Already sent you friend request to §7" + args[1]);
                            return;
                        case SUCCESSFUL_SEND:
                            p.sendMessage("Sent friend request to §7" + args[1]);

                            if (fFriend.isOnline()) {
                                TextComponent message = new TextComponent("You got a friend request from §7" + p.getName() + " ");

                                TextComponent accept = new TextComponent("§6[Accept] ");
                                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend add " + p.getName()));

                                TextComponent deny = new TextComponent("§c[Deny]");
                                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove " + p.getName()));

                                message.addExtra(accept);
                                message.addExtra(deny);

                                fFriend.getPlayer().spigot().sendMessage(message);
                            }
                    }
                });
                return false;

            case "remove":
                if (args.length < 2) {
                    p.sendMessage(USAGE_MESSAGE);
                    return true;
                }

                for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                    if (pl.getName().equals(args[1])) {
                        friend = pl;
                        break;
                    }
                }

                if (friend == null) {
                    p.sendMessage("§cCould not find player §7" + args[1]);
                    return true;
                }

                friends.removeFriend(friend.getUniqueId(), status -> {
                    switch (status) {
                        case ERROR:
                            p.sendMessage("§cError occurred while removing your friend §7" + args[1]);
                            return;
                        case SUCCESSFUL_REMOVED:
                            p.sendMessage("Friend §7" + args[1] + " §fremoved");
                            return;
                        case SUCCESSFUL_REMOVED_REQ:
                            p.sendMessage("Denied friend request from §7" + args[1]);
                            return;
                        case NOT_FRIENDS:
                            p.sendMessage("§7" + args[1] + " §cis not your friend");
                    }
                });
                return false;

            case "list":
                friends.getFriends(result -> {
                    if (result.isPresent()) {
                        String out = Helper.uuidArrayToPlayerNames(result.get());

                        if (out != null) {
                            p.sendMessage("Friends: §7" + out);
                        } else
                            p.sendMessage("§cYou have no friends :(");

                    } else
                        p.sendMessage("§cError occurred while getting you friends");
                });
                return false;

            case "requests":
                friends.getFriendRequests(result -> {
                    if (result.isPresent()) {
                        String out = Helper.uuidArrayToPlayerNames(result.get());

                        if (out != null) {
                            p.sendMessage("Friend requests: §7" + out);
                        } else
                            p.sendMessage("§cYou have no friends requests :(");

                    } else
                        p.sendMessage("§cError occurred while getting you friends");
                });
                return false;

            case "acceptall":
                friends.acceptAllRequests(status -> {
                    switch (status) {
                        case ERROR:
                            p.sendMessage("§cError occurred while accepting your requests");

                        case SUCCESSFUL_ADDED:
                            p.sendMessage("§6You accepted all you friend requests");
                    }
                });
                return false;

            default:
                p.sendMessage(USAGE_MESSAGE);
                return true;
        }
    }
}