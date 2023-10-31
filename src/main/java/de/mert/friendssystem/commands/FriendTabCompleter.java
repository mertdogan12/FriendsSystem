package de.mert.friendssystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class FriendTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        LinkedList<String> list =  new LinkedList<String>() {
            {
                add("add");
                add("remove");
                add("list");
                add("requests");
                add("acceptall");
            }
        };

        if (args.length == 1) {
            list.sort(Comparator.comparing(s -> {
                if (args[0].isEmpty())
                    return 1;

                if (s.length() > args[0].length())
                    return 1;

                if (s.equals(args[0].substring(0, s.length())))
                    return 0;
                else
                    return 1;
            }));

            return list;
        } else
            return new LinkedList<String>(){
                {
                    Bukkit.getOnlinePlayers().forEach(player -> add(player.getName()));
                }
            };
    }
}