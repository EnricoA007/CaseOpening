package caseopening.main;

import caseopening.commands.CMD_Case;
import caseopening.commands.CMD_CaseOpening;
import caseopening.database.Database;
import caseopening.event.EventListener;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaseOpeningMain extends JavaPlugin {

    public static String prefix = "§6» §eCase Opening §6┃ §e";
    public static Plugin plugin;

    /**
     * Start 16:28 am 12.01.2022
     * Ende 21:57 am 12.01.2022 (5h und 30 Minuten)
     */

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("caseopening").setExecutor(new CMD_CaseOpening());
        this.getCommand("case").setExecutor(new CMD_Case());

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        if(!Database.data.exists()){
            Database.data.mkdir();
        }

        if(Database.casesDat.exists()){
            try {
                Database.importCasesData(IOUtils.toByteArray(new FileInputStream(Database.casesDat)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(Database.itemsDat.exists()){
            try {
                Database.importItemsData(IOUtils.toByteArray(new FileInputStream(Database.itemsDat)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(Database.chestsDat.exists()){
            try {
                Database.importChestsData(IOUtils.toByteArray(new FileInputStream(Database.chestsDat)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDisable(){
        try{
            FileOutputStream out = new FileOutputStream(Database.casesDat);

            out.write(Database.exportCasesData());

            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            FileOutputStream out = new FileOutputStream(Database.itemsDat);

            out.write(Database.exportItemsData());

            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            FileOutputStream out = new FileOutputStream(Database.chestsDat);

            out.write(Database.exportChestsData());

            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
