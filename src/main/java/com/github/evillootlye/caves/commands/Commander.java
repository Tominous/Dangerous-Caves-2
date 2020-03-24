package com.github.evillootlye.caves.commands;

import com.github.evillootlye.caves.DangerousCaves;
import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.mobs.MobsManager;
import com.github.evillootlye.caves.ticks.Dynamics;
import com.github.evillootlye.caves.ticks.TickLevel;
import com.github.evillootlye.caves.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// TODO: Classes for each subcommand
// TODO: TabCompleter
public class Commander implements CommandExecutor {
    private final MobsManager mobsManager;
    private final Configuration cfg;
    private final Dynamics dynamics;

    public Commander(MobsManager mobsManager, Configuration cfg, Dynamics dynamics) {
        this.mobsManager = mobsManager;
        this.cfg = cfg;
        this.dynamics = dynamics;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            help(sender, label);
        } else switch (args[0].toLowerCase()) {
            case "info":
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if(!sender.hasPermission("dangerous.caves.command.info")) return false;
                Player player = (Player)sender;
                Location loc = player.getLocation();
                sender.sendMessage(Utils.clr("&bWorld: &f") + loc.getWorld().getName());
                sender.sendMessage(Utils.clr("&bChunk: &f") + loc.getChunk().getX() + "," + loc.getChunk().getZ());
                sender.sendMessage(Utils.clr("&bHand: &f") + player.getInventory().getItemInMainHand().getType());
                break;
            case "summon":
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Utils.clr("&cYou can't execute this subcommand from console!"));
                    return true;
                } else if(!sender.hasPermission("dangerous.caves.command.summon")) return false;
                if(args.length < 2) {
                    sender.sendMessage(Utils.clr("&cYou should specify a mob to summon!"));
                    return true;
                }
                if(mobsManager.summon(args[1], ((Player)sender).getLocation())) {
                    sender.sendMessage(Utils.clr("&aMob " + args[1] + " was successfully summoned."));
                } else {
                    sender.sendMessage(Utils.clr("&cThere's no mobs called " + args[1] + "!"));
                }
                break;
            case "tick":
                if(!sender.hasPermission("dangerous.caves.command.tick")) return false;
                for(TickLevel level : TickLevel.values()) dynamics.tick(level);
                sender.sendMessage(Utils.clr("&aTicked every tickables."));
                break;
            case "reload":
                if(!sender.hasPermission("dangerous.caves.command.reload")) return false;
                cfg.reloadYml();
                sender.sendMessage(Utils.clr("&aPlugin was successfully reloaded."));
                cfg.checkVersion();
                break;
            default: help(sender, label);
        }
        return true;
    }

    private static void help(CommandSender sender, String label) {
        String[] split = DangerousCaves.PLUGIN.getDescription().getVersion().split("-");
        sender.sendMessage(Utils.clr("&6&lDangerousCaves &ev" + split[0] + " c" + split[1]));
        sender.sendMessage(Utils.clr("&a /" + label + " info &7- Get some info about your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " summon [mob] &7- Spawn a mob on your location."));
        sender.sendMessage(Utils.clr("&a /" + label + " tick &7- Tick everything manually."));
        sender.sendMessage(Utils.clr("&a /" + label + " reload &7- Reload plugin configuration."));
    }
}
