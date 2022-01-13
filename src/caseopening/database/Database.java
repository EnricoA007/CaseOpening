package caseopening.database;

import caseopening.api.CaseOpeningAPI;
import com.google.gson.*;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Database {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static File data = new File("plugins/CaseOpening/");
    public static File casesDat = new File(data + "/cases.json");
    public static File itemsDat = new File(data + "/items.json");
    public static File chestsDat = new File(data + "/chests.json");

    public static void importItemsData(byte[] b){
        JsonArray items = gson.fromJson(new String(b, StandardCharsets.UTF_8), JsonArray.class);

        for(JsonElement e : items){
            JsonObject obj= e.getAsJsonObject();
            CaseOpeningAPI.getCaseContent().put(jsonToItem(obj.get("Item").getAsJsonObject()),obj.get("Percent").getAsDouble());
        }
    }

    public static void importCasesData(byte[] b){
        JsonArray data = gson.fromJson(new String(b, StandardCharsets.UTF_8), JsonArray.class);

        for(JsonElement e : data){
            JsonObject obj =e.getAsJsonObject();

            String uuid = obj.get("UUID").getAsString();
            int cases = obj.get("Amount").getAsInt();

            CaseOpeningAPI.registerPlayerCase(uuid, cases);
        }
    }

    public static void importChestsData(byte[] b){
        JsonArray data = gson.fromJson(new String(b, StandardCharsets.UTF_8), JsonArray.class);

        for(JsonElement e : data){
            CaseOpeningAPI.registerChest(decodeLocation(e.getAsString()));
        }
    }

    public static byte[] exportItemsData() {
        JsonArray array = new JsonArray();

        CaseOpeningAPI.getCaseContent().forEach((i,d) ->{
            JsonObject obj = new JsonObject();

            obj.add("Item", itemToJson(i));
            obj.addProperty("Percent", d);

            array.add(obj);
        });

        return gson.toJson(array).getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] exportCasesData() {
        JsonArray array = new JsonArray();

        CaseOpeningAPI.getCasesList().forEach((uuid,amount) ->{
            JsonObject obj = new JsonObject();

            obj.addProperty("UUID", uuid);
            obj.addProperty("Amount", amount);

            array.add(obj);
        });

        return gson.toJson(array).getBytes(StandardCharsets.UTF_8);
    }
    public static byte[] exportChestsData() {
        JsonArray array = new JsonArray();

        CaseOpeningAPI.getRegisteredChests().forEach(location -> {
            array.add(new JsonPrimitive(encodeLocation(location)));
        });

        return gson.toJson(array).getBytes(StandardCharsets.UTF_8);
    }

    public static JsonObject itemToJson(ItemStack item) {
        JsonObject obj = new JsonObject();

        obj.addProperty("Material", item.getType().name());
        obj.addProperty("Amount", item.getAmount());
        obj.addProperty("Durability", item.getDurability());

        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);

        if(nms.hasTag()){
            obj.addProperty("Tag", nms.getTag().toString());
        }else{
            obj.addProperty("Tag", "none");
        }

        return obj;
    }

    public static ItemStack jsonToItem(JsonObject json) {
        ItemStack item = new ItemStack(Material.valueOf(json.get("Material").getAsString()), json.get("Amount").getAsInt(), json.get("Durability").getAsShort());
        String nbt = json.get("Tag").getAsString();

        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);

        if(!nbt.equals("none")){
            try {
                nms.setTag(MojangsonParser.parse(nbt));
            } catch (MojangsonParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        return CraftItemStack.asBukkitCopy(nms);
    }

    public static Location decodeLocation(String s){
        String[] a = s.split(":");
        return new Location(Bukkit.getWorld(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]), Float.parseFloat(a[4]), Float.parseFloat(a[5]));
    }

    public static String encodeLocation(Location loc){
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getPitch() + ":" + loc.getYaw();
    }

}
