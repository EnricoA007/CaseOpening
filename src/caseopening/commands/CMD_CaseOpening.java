package caseopening.commands;

import caseopening.api.CaseOpeningAPI;
import caseopening.main.CaseOpeningMain;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CMD_CaseOpening implements CommandExecutor {

    public static ItemStack ADD_ITEM = null;

    static {
        ADD_ITEM = new ItemStack(Material.BANNER, 1, (short)15);
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(ADD_ITEM);

        try {
            NBTTagCompound b = MojangsonParser.parse("{display:{Name:\"§aAdd\"},BlockEntityTag:{Patterns:[{Color:10,Pattern:\"sc\"},{Color:15,Pattern:\"bs\"},{Color:15,Pattern:\"ts\"},{Color:15,Pattern:\"bo\"}]}}");
            nms.setTag(b);
        }catch(Exception e) {
            e.printStackTrace();
        }

        ADD_ITEM= CraftItemStack.asBukkitCopy(nms);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(sender instanceof Player){
            Player p = ((Player)sender);
            if(!p.hasPermission("caseopening.manage")){
                sender.sendMessage(CaseOpeningMain.prefix + "§cYou need the permission §a\"§2caseopening.manage§a\"");
                return true;
            }

            if(args.length == 1){

                if(args[0].equalsIgnoreCase("manage")){
                    int size = CaseOpeningAPI.getCaseContent().size();

                    int repeat = 1;
                    while(9 < size){
                        repeat++;
                        size--;
                    }

                    size = CaseOpeningAPI.getCaseContent().size();

                    Inventory inv = Bukkit.createInventory(null, repeat*9, "§6CO §8✕ §6Manager");
                    int counter = 0;

                    for(ItemStack item : CaseOpeningAPI.getCaseContent().keySet()){
                        ItemMeta meta = item.getItemMeta();
                        ArrayList<String> lores = new ArrayList<>();
                        if(meta.hasLore()) {
                            lores.addAll(meta.getLore());
                        }
                        lores.add("§8§m-*-------------------*-");
                        lores.add("§eChance§6: §c" + CaseOpeningAPI.getCaseContent().get(item));
                        lores.add("§eIndex§6: §c" + getIndex(item));
                        lores.add("§eClick to remove");
                        lores.add("§8§m-*-------------------*-");
                        meta.setLore(lores);

                        ItemStack it = item.clone();
                        it.setItemMeta(meta);

                        inv.setItem(counter, it);
                        counter++;
                    }

                    inv.setItem(counter, ADD_ITEM);

                    p.openInventory(inv);

                }else if(args[0].equalsIgnoreCase("chest")){
                    CaseOpeningAPI.detectForInteract(p);
                    sender.sendMessage(CaseOpeningMain.prefix + "§nRight click§e on a chest to select it");
                }

            }else{
                sender.sendMessage(CaseOpeningMain.prefix + "§cUsage: " + cmd.getUsage());
            }

        }else{
            sender.sendMessage(CaseOpeningMain.prefix + "§cYou must be a player");
        }

        return true;
    }

    private int getIndex(ItemStack item) {
        int i = 0;

        for(ItemStack it : CaseOpeningAPI.getCaseContent().keySet()) {
            if(it.equals(item)){
                return i;
            }

            i++;
        }

        return i;
    }


}
