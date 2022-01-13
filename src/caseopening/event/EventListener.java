package caseopening.event;

import caseopening.api.CaseOpeningAPI;
import caseopening.commands.CMD_CaseOpening;
import caseopening.main.CaseOpeningMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!CaseOpeningAPI.hasCaseAccount(e.getPlayer())){
            CaseOpeningAPI.registerPlayerCase(e.getPlayer(),0);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p =e.getPlayer();
        if(CaseOpeningAPI.caseContentAdd.containsKey(p)){
            e.setCancelled(true);

            double percent = -1;
            try{
                percent = Double.parseDouble(e.getMessage());
                if(percent > 100){
                    p.sendMessage(CaseOpeningMain.prefix + "§cPercent can't be over 100%");
                    return;
                }else if(percent < 0){
                    p.sendMessage(CaseOpeningMain.prefix + "§cPercent can't be below 0%");
                    return;
                }
            }catch(Exception f){
                p.sendMessage(CaseOpeningMain.prefix + "§cThe number you've entered is invalid, try again");
                return;
            }

            ItemStack current =CaseOpeningAPI.caseContentAdd.get(p);
            CaseOpeningAPI.caseContentAdd.remove(p);

            p.sendMessage(CaseOpeningMain.prefix + "§aThe item was added");
            CaseOpeningAPI.getCaseContent().put(current, percent);
        }
    }

    @EventHandler
    public void chestManagement(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block b =e.getClickedBlock();

        if(b.getType() == Material.CHEST){
            if(CaseOpeningAPI.hasInteracted(e.getPlayer())){
                e.setCancelled(true);
                if(!CaseOpeningAPI.isChestRegistered(b.getLocation())){
                    CaseOpeningAPI.registerChest(b.getLocation());
                    e.getPlayer().sendMessage(CaseOpeningMain.prefix + "The chest was registered");
                }else{
                    CaseOpeningAPI.unregisterChest(b.getLocation());
                    e.getPlayer().sendMessage(CaseOpeningMain.prefix + "The chest was unregistered");
                }
            }else if(CaseOpeningAPI.isChestRegistered(b.getLocation())){
                e.setCancelled(true);

                if(CaseOpeningAPI.getCaseContent().size() == 0){
                    e.getPlayer().sendMessage(CaseOpeningMain.prefix + "You can't win without any registered items");
                    return;
                }

                Inventory inv = Bukkit.createInventory(null,27, "§d§r§6Case Opening");

                ItemStack cases = new ItemStack(Material.CHEST);
                ItemMeta meta = cases.getItemMeta();
                meta.setDisplayName("§8>> §eOpen Case §8<<");
                meta.setLore(Arrays.asList(new String[]{"§6Cases§e: §c" + CaseOpeningAPI.getCases(e.getPlayer())}));
                cases.setItemMeta(meta);

                inv.setItem(13, cases);

                e.getPlayer().openInventory(inv);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
       try{
           Inventory inv =e.getInventory();
           Player p=(Player) e.getWhoClicked();
           if(inv.getTitle().equals("§d§r§6Case Opening")) {
               e.setCancelled(true);

               if (e.getSlot() == 13 && CaseOpeningAPI.getCases(p) != 0) {
                   CaseOpeningAPI.removeCases(p, 1);
                   p.sendMessage(CaseOpeningMain.prefix + "§aGood Luck!");
                   p.closeInventory();

                   CaseOpeningAPI.openCaseInventory(p);
               }
           }else if(inv.getTitle().equals("§e§f§6CaseOpening")){
               e.setCancelled(true);
           }else if(inv.getTitle().equals("§6CO §8✕ §6Manager")){
               e.setCancelled(true);
               ItemStack current = e.getCurrentItem();

               if(CMD_CaseOpening.ADD_ITEM.equals(current)) {
                   p.closeInventory();

                   Inventory si = Bukkit.createInventory(null, InventoryType.FURNACE, "§6CO §8✕ §6Select Item");

                   ItemStack inputItem = new ItemStack(Material.EMERALD);
                   ItemMeta meta = inputItem.getItemMeta();
                   meta.setDisplayName("§eItem §8✕ §eDrop here");
                   inputItem.setItemMeta(meta);

                   si.setItem(0, inputItem);

                   p.openInventory(si);
               }else if(current.getType() != Material.AIR){
                   ItemMeta meta = current.getItemMeta();
                   int index = 0;

                   for(String l: meta.getLore()) {
                       if(l.startsWith("§eIndex§6:")){
                           break;
                       }
                       index++;
                   }

                   String rin = meta.getLore().get(index);
                   index = Integer.parseInt(rin.substring(13, rin.length()));

                   CaseOpeningAPI.getCaseContent().remove(new ArrayList<ItemStack>(CaseOpeningAPI.getCaseContent().keySet()).get(index));
                   p.closeInventory();
                   p.performCommand("caseopening manage");
               }
           }else if(e.getClickedInventory().getTitle().equals("§6CO §8✕ §6Select Item")){
               if(e.getSlot() == 0 && inv.getItem(0).getType() == Material.EMERALD) {
                   e.setCancelled(true);

                   if(e.getCursor().getType() != Material.AIR){
                       ItemStack selectedItem = e.getCursor();
                       e.setCursor(new ItemStack(Material.AIR));
                       CaseOpeningAPI.caseContentAdd.put(p, selectedItem);
                       p.sendMessage(CaseOpeningMain.prefix + "§aWrite the chance in percent in the chat");
                       p.closeInventory();
                   }
               }

           }
       }catch(Exception ex){

       }
    }


}
