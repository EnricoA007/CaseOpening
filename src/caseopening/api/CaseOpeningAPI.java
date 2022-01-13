package caseopening.api;

import caseopening.main.CaseOpeningMain;
import caseopening.util.CallableChanger;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CaseOpeningAPI {

    public static HashMap<Player, ItemStack> caseContentAdd = new HashMap<>();

    private static ArrayList<Player> chestInteract = new ArrayList<>();
    private static ArrayList<Location> registeredChests = new ArrayList<>();
    private static HashMap<String, Integer> cases = new HashMap<>();
    private static HashMap<ItemStack, Double> caseContent = new HashMap<>();

    public static void detectForInteract(Player p){
        chestInteract.add(p);
    }

    public static boolean hasInteracted(Player p){
         if(chestInteract.contains(p)){
             chestInteract.remove(p);
             return true;
         }
         return false;
    }

    public static void registerChest(Location loc) {
        registeredChests.add(loc);
    }

    public static void unregisterChest(Location loc){
        registeredChests.remove(loc);
    }

    public static List<Location> getRegisteredChests(){
        return (List<Location>) registeredChests.clone();
    }

    public static boolean isChestRegistered(Location location) {
        return registeredChests.contains(location);
    }

    public static void registerPlayerCase(String uuid, int i) {
        if (cases.containsKey(uuid)) {
            cases.remove(uuid);
        }
        cases.put(uuid, i);
    }

    public static void registerPlayerCase(Player p, int i) {
        if (cases.containsKey(p.getUniqueId().toString())) {
            cases.remove(p.getUniqueId().toString());
        }
        cases.put(p.getUniqueId().toString(), i);
    }

    public static HashMap<ItemStack, Double> getCaseContent(){
        return caseContent;
    }

    public static boolean hasCaseAccount(Player p){
        return cases.containsKey(p.getUniqueId().toString());
    }

    public static int getCases(Player p) {
        return cases.get(p.getUniqueId().toString());
    }

    public static void setCases(Player p, int amount){
        registerPlayerCase(p, amount);
    }

    public static void removeCases(Player p, int amount){
        setCases(p,getCases(p)-amount);
    }

    public static void addCases(Player p, int amount){
        setCases(p,getCases(p)+amount);
    }

    public static HashMap<String, Integer> getCasesList() {
        return cases;
    }

    public static List<ItemStack> getCaseRepeatingTemplate(){
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        for(int i = 0; i<27; i++){
            items.add(generateItem());
        }

        ((ArrayList<ItemStack>)items.clone()).forEach(items::add);

        return items;
    }

    public static ItemStack generateItem(){
        List<Double> percents = new ArrayList<>(caseContent.values());
        List<Double> percentsList = new ArrayList<>(caseContent.values());

        double[] arrayPercents = new double[percents.size()];

        for (int i = 0; i < arrayPercents.length; i++) {
            arrayPercents[i] = percents.get(i);
        }

        Arrays.sort(arrayPercents);
        percents = new ArrayList<Double>();

        for(double d : arrayPercents){
            if(percents.contains(d)) continue;
            percents.add(d);
        }

        double chance = 100;

        for(double d : percents){
            int rand = new Random().nextInt(100);

            if(rand < d){
                chance = d;
                break;
            }
        }

        ArrayList<ItemStack> selected = new ArrayList<ItemStack>();

        double finalChance = chance;
        caseContent.entrySet().stream().filter(entry -> {
            double percent = entry.getValue();
            return percent== finalChance;
        }).forEach(entry -> selected.add(entry.getKey()));

        return selected.get(new Random().nextInt(selected.size()));
    }

    public static void openCaseInventory(Player p) {
        List<ItemStack> items= getCaseRepeatingTemplate();

        Inventory inv = Bukkit.createInventory(null, 27, "§e§f§6CaseOpening");

        ItemStack hopper = new ItemStack(Material.HOPPER);
        ItemMeta meta = hopper.getItemMeta();
        meta.setDisplayName("§aGewinn:");
        hopper.setItemMeta(meta);

        inv.setItem(4,hopper);

        Iterator<ItemStack> iterator= items.iterator();

        inv.setItem(9, iterator.next());

        p.openInventory(inv);

        CallableChanger<Integer> callableChanger = new CallableChanger<Integer>() {
            private int i = 0;

            @Override
            public Integer call() {
                return i;
            }

            @Override
            public void change(Integer integer) {
                this.i=integer;
            }
        };

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                inv.setItem(17, inv.getItem(16));
                inv.setItem(16, inv.getItem(15));
                inv.setItem(15, inv.getItem(14));
                inv.setItem(14, inv.getItem(13));
                inv.setItem(13, inv.getItem(12));
                inv.setItem(12, inv.getItem(11));
                inv.setItem(11, inv.getItem(10));
                inv.setItem(10, inv.getItem(9));

                inv.setItem(9, iterator.next());

                p.updateInventory();

                if(!iterator.hasNext()){
                    Bukkit.getScheduler().cancelTask(callableChanger.call());

                    ItemStack item = inv.getItem(13);
                    p.getInventory().addItem(item);
                    p.sendMessage(CaseOpeningMain.prefix + "§aYou've won §e\"§c" + item.getType().name().toLowerCase() + "§e\"");
                }
            }

        };

        callableChanger.change(Bukkit.getScheduler().scheduleSyncRepeatingTask(CaseOpeningMain.plugin, runnable, 2, 2));
    }


}
