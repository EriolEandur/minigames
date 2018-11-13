package com.mcmiddleearth.minigames.pvp;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.PvPGame;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * @author Planetology
 */
public class PvPLoadoutManager {

    private PvPGame game;

    @Getter private LinkedList<String> armorContents, hotbarContents;
    @Getter private String shieldContent;
    @Getter private LinkedList<Object> armor, hotbar;
    @Getter private Object shield;

    public PvPLoadoutManager(PvPGame game) {
        this.game = game;

        armorContents = new LinkedList<>();
        hotbarContents = new LinkedList<>();
        armor = new LinkedList<>();
        hotbar = new LinkedList<>();
    }

    public void saveLoadout(File file, String description, Player player) throws IOException {
        for(final ItemStack item : player.getInventory().getArmorContents()) {
            if(item != null) {
                armorContents.add(new PvPLoadoutItem(item).toJson());
            } else {
                armorContents.add(new PvPLoadoutItem().toJson());
            }
        }

        for(int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if(item != null) {
                hotbarContents.add(new PvPLoadoutItem(item).toJson());
            }
        }

        ItemStack shield = player.getInventory().getItemInOffHand();
        if (!shield.getType().equals(Material.AIR)) shieldContent = new PvPLoadoutItem(shield).toJson();

        JSONArray jArmorArray = new JSONArray();
        JSONArray jHotbarArray = new JSONArray();

        for (String armor : armorContents) {
            jArmorArray.add(armor);
        }

        for (String item : hotbarContents) {
            jHotbarArray.add(item);
        }

        JSONObject jLoadout = new JSONObject();
        jLoadout.put("armor", jArmorArray);
        jLoadout.put("hotbar", jHotbarArray);
        if (!shield.getType().equals(Material.AIR)) jLoadout.put("shield", shieldContent);
        jLoadout.put("description", description);

        try(FileWriter fw = new FileWriter(file)) {
            jLoadout.writeJSONString(fw);
        }
    }

    public void loadLoadout(File file) throws FileNotFoundException, ParseException {
        try {
            String input;
            try (Scanner reader = new Scanner(file)) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }

            JSONObject jInput = (JSONObject) new JSONParser().parse(input);
            JSONArray jArmorArray = (JSONArray) jInput.get("armor");
            JSONArray jHotbarArray = (JSONArray) jInput.get("hotbar");

            for (Object jItem : jArmorArray) {
                armor.add(jItem);
            }

            for (Object jItem : jHotbarArray) {
                hotbar.add(jItem);
            }

            if (jInput.get("shield") != null) shield = jInput.get("shield");

            game.setLoadout(true);
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
}
