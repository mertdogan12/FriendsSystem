package de.mert.friendssystem.commands;

import de.mert.friendssystem.Friends;
import org.bukkit.Bukkit;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("$cCommand can only be executed by a player");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
            return true;
        }

        switch (args[0]) {
            case "add":
                if (args.length < 2) {
                    p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
                    return true;
                }

                Player friend = Bukkit.getPlayer(args[1]);
                if (friend == null) {
                    p.sendMessage("§cCould not find player §7" + args[1]);
                    return true;
                }

                Friends friends = new Friends(p.getUniqueId());
                if (!friends.addFriend(friend.getUniqueId())) {
                    p.sendMessage("§cError occurred while adding you friend");
                    return true;
                }

                p.sendMessage("§6Friend request send to §7" + args[0]);
                return false;

            case "remove":
            case "list":
                return false;

            default:
                p.sendMessage("§cUsage: §7/friend §f[add|remove|list] <name>");
                return true;
        }
    }
}
