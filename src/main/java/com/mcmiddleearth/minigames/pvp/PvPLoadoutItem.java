package com.mcmiddleearth.minigames.pvp;

import com.google.common.primitives.Ints;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Planetology
 */
public class PvPLoadoutItem {

    private String material, name;
    private List<String> lore;
    private HashMap<String, Long> enchantments = new HashMap<>();
    private long amount;

    public PvPLoadoutItem(ItemStack item) {
        material = item.getType().name();

        ItemMeta meta = item.getItemMeta();

        name = meta.getDisplayName();
        lore = meta.getLore();

        for(Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            enchantments.put(entry.getKey().getName(), Long.valueOf(entry.getValue()));
        }

        amount = item.getAmount();
    }

    public PvPLoadoutItem() {
        material = Material.AIR.toString();
        amount = 1;
    }

    private PvPLoadoutItem(String material, String name, List<String> lore, HashMap<String, Long> enchantments, Long amount) {
        this.material = material;
        this.name = name;
        this.lore = lore;

        if (this.enchantments != null) {
            this.enchantments.putAll(enchantments);
        }

        this.amount = amount == null ? 1L : amount;
    }

    public String toJson() {
        JSONObject object = new JSONObject();

        object.put("material", material);
        object.put("name", name);
        object.put("lore", lore);
        object.put("enchantments", enchantments);
        object.put("amount", amount);

        return object.toJSONString();
    }

    public static PvPLoadoutItem fromJson(Object itemObject) {
        JSONObject array = (JSONObject) JSONValue.parse((String) itemObject);

        return new PvPLoadoutItem((String)array.get("material"), (String)array.get("name"), (List<String>)array.get("lore"),
                (HashMap<String, Long>)array.get("enchantments"), (Long)array.get("amount"));
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(material == null ? Material.GRASS : Material.valueOf(material));
        ItemMeta meta = itemStack.getItemMeta();

        if (Material.getMaterial(material) != Material.AIR) {
            meta.setDisplayName(name);
            meta.setLore(lore);

            if(enchantments.size() != 0) {
                for(Map.Entry<String, Long> entry : enchantments.entrySet()) {
                    meta.addEnchant(Enchantment.getByName(entry.getKey()), Ints.checkedCast(entry.getValue()), true);
                }
            }

        }

        itemStack.setItemMeta(meta);
        itemStack.setAmount(Math.toIntExact(amount));

        return itemStack;
    }
}
