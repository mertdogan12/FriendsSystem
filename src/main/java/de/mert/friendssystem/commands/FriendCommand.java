package de.mert.friendssystem.commands;

import de.mert.friendssystem.Friends;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Friend Command
 */
public class FriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("$cCommand can only be executed by a player");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
            return true;
        }

        Friends friends = new Friends(p.getUniqueId());
        switch (args[0]) {
            case "add":
                if (args.length < 2) {
                    p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
                    return true;
                }

                if (args[1].equals(p.getName())) {
                    p.sendMessage("§cYou can't send yourself a friend request");
                    return true;
                }

                Player friend = Bukkit.getPlayer(args[1]);
                if (friend == null) {
                    p.sendMessage("§cCould not find player §7" + args[1]);
                    return true;
                }

                friends.addFriend(friend.getUniqueId(), status -> {
                    switch (status) {
                        case ERROR:
                            p.sendMessage("§cError occurred while adding your friend §7" + args[1]);
                            return;
                        case ALREADY_FRIENDS:
                            p.sendMessage("§6Already added you friend §7" + args[1]);
                            return;
                        case SUCCESSFUL_ADDED:
                            p.sendMessage("§6Friend §7" + args[1] + " §6added");
                            return;
                        case ALREADY_SEND_REQUEST:
                            p.sendMessage("§6Already sent you friend request to §7" + args[1]);
                            return;
                        case SUCCESSFUL_SEND:
                            p.sendMessage("§6Sent friend request to §7" + args[1]);
                    }
                });
                return false;

            case "remove":
                return false;

            case "list":
                friends.getFriends(result -> {
                    if (result.isPresent()) {
                        StringBuilder builder = new StringBuilder();
                        UUID[] data = result.get();

                        if (data.length == 0) {
                            p.sendMessage("§cYou have no friends :(");
                            return;
                        }

                        builder.append("§7");
                        for (int i = 0; i < data.length; i++) {
                            if (i == data.length - 1)
                                continue;

                            String name = Bukkit.getOfflinePlayer(data[i]).getName();
                            builder.append(name).append(", ");
                        }
                        builder.append(Bukkit.getOfflinePlayer(data[data.length - 1]).getName());

                        p.sendMessage(builder.toString());
                    } else
                        p.sendMessage("§cError occurred while getting you friends");
                });
                return false;

            default:
                p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
                return true;
        }
    }
}
