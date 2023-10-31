package de.mert.friendssystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
            String input = args[0].toLowerCase();
            return list.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .sorted()
                    .collect(Collectors.toList());
        } else
            return new LinkedList<String>(){
                {
                    Bukkit.getOnlinePlayers().forEach(player -> add(player.getName()));
                }
            };
    }
}