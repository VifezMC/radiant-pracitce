package neptune.dev.utils.render;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private final ItemStack itemStack;

    public static ItemStack build(Material material, int amount, int setShort, String itemName, List<String> setLore) {
        ItemStack itemStack = new ItemStack(material, amount, (short) setShort);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(CC.translate(itemName));
        itemMeta.setLore(setLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(ItemStack is) {
        this.itemStack = is;
    }

    public ItemBuilder(Material m, int amount) {
        this.itemStack = new ItemStack(m, amount);
    }

    public ItemBuilder(Material m, int amount, byte durability) {
        this.itemStack = new ItemStack(m, amount, durability);
    }

    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder setDurability(short dur) {
        itemStack.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(int dur) {
        return setDurability((short) dur);
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(CC.translate(name));
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setUnColoredName(String name) {
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setUnTranslatedName(String name) {
        return setUnColoredName(ChatColor.translateAlternateColorCodes('&', name));
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        itemStack.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        itemStack.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            skullMeta.setOwner(owner);
            itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        ItemMeta im = itemStack.getItemMeta();
        im.addEnchant(ench, level, true);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        itemStack.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index >= lore.size())
            return this;
        lore.remove(index);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(CC.translate(line));
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnTranslatedLoreLine(String line) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (pos < 0 || pos >= lore.size())
            return this;
        lore.set(pos, CC.translate(line));
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setDyeColor(DyeColor color) {
        this.itemStack.setDurability(color.getDyeData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
            leatherMeta.setColor(color);
            itemStack.setItemMeta(leatherMeta);
        }
        return this;
    }

    public ItemStack toItemStack() {
        return itemStack;
    }
}
