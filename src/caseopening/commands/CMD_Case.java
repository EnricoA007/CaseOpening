package caseopening.commands;

import caseopening.api.CaseOpeningAPI;
import caseopening.main.CaseOpeningMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;


public class CMD_Case implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(!sender.hasPermission("caseopening.case")){
            sender.sendMessage(CaseOpeningMain.prefix + "§cYou need the permission §a\"§2lottery.manage§a\"");
            return true;
        }

        if(args.length == 2) {
            int amount = -1;

            try{
                amount = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                sender.sendMessage(CaseOpeningMain.prefix + "§cThe entered number isn't a valid number");
                return true;
            }

            if(!(sender instanceof Player)){
                sender.sendMessage(CaseOpeningMain.prefix + "§cThe console does not have an account");
            }

            Player p = (Player) sender;

            if (args[0].equalsIgnoreCase("add")) {
                p.sendMessage(CaseOpeningMain.prefix + "§aYou've got §e" + amount + "§a new cases");
                CaseOpeningAPI.addCases(p, amount);
            } else if (args[0].equalsIgnoreCase("set")) {
                p.sendMessage(CaseOpeningMain.prefix + "§aYou've set your amount to §e" + amount + "§a cases");
                CaseOpeningAPI.setCases(p, amount);
            } else if (args[0].equalsIgnoreCase("remove")) {
                p.sendMessage(CaseOpeningMain.prefix + "§aYou've removed §e" + amount + "§a cases");
                CaseOpeningAPI.removeCases(p, amount);
            } else {
                sender.sendMessage(CaseOpeningMain.prefix + "§cUsage: " + cmd.getUsage());
            }
        }else if(args.length == 3){
            int amount = -1;
            Player target = null;

            try{
                amount = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                sender.sendMessage(CaseOpeningMain.prefix + "§cThe entered number isn't a valid number");
                return true;
            }

            try{
                target = Bukkit.getPlayer(args[2]);
            }catch(NullPointerException e){
                sender.sendMessage(CaseOpeningMain.prefix + "§cThe player is not online");
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                sender.sendMessage(CaseOpeningMain.prefix + "§aYou've added an amount of §e" + amount + "§a new cases");
                CaseOpeningAPI.addCases(target, amount);
            } else if (args[0].equalsIgnoreCase("set")) {
                sender.sendMessage(CaseOpeningMain.prefix + "§aYou've set the amount to §e" + amount + "§a cases");
                CaseOpeningAPI.setCases(target, amount);
            } else if (args[0].equalsIgnoreCase("remove")) {
                sender.sendMessage(CaseOpeningMain.prefix + "§aYou've removed §e" + amount + "§a cases");
                CaseOpeningAPI.removeCases(target, amount);
            } else {
                sender.sendMessage(CaseOpeningMain.prefix + "§cUsage: " + cmd.getUsage());
            }
        }else{
            sender.sendMessage(CaseOpeningMain.prefix + "§cUsage: " + cmd.getUsage());
        }

        return true;
    }

}
