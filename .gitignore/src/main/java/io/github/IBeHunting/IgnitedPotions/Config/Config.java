package io.github.IBeHunting.IgnitedPotions.Config;

import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config implements PluginConfig
{
   private static Config i;

   private FileConfiguration config;
   private File file;

   private Map<String, PotionEffectType> ingredients;
   private Map<String, Integer> custom_modifiers;

   public Config()
   {
      i = this;
      this.file = new File(PotionsPlugin.getInstance().getDataFolder(), "config.yml");
      load();
   }

   public static Config getInstance()
   {
      return i;
   }

   public void load()
   {
      PotionsPlugin.getInstance().reloadConfig();
      this.config = PotionsPlugin.getInstance().getConfig();
      loadIngredients();
      loadModifiers();
   }

   private void loadIngredients()
   {
      ConfigurationSection section = config.getConfigurationSection("potion-ingredients");
      PotionEffectType e_type;
      Material i_type;
      short i_data;
      this.ingredients = new HashMap<>();
      if (section == null)
      {
         PotionsPlugin.getInstance().getLogger().warning("No potion-ingredients config section");
         return;
      }
      for (String effect : section.getKeys(false))
      {
         try
         {
            e_type = PotionEffectType.getByName(effect);
            i_type = Material.valueOf(section.getString(effect, "AIR"));
            i_data = (short) section.getInt(effect + ".data", 0);
            ingredients.put(i_type.name() + ":" + i_data, e_type);
         }
         catch(IllegalArgumentException e)
         {
            PotionsPlugin.getInstance().getLogger().warning("Bad value type found in ingredients config");
         }
      }
   }

   private void loadModifiers()
   {
      ConfigurationSection section = config.getConfigurationSection("extra-tiers");
      Material i_type;
      short i_data;
      int level;
      this.custom_modifiers = new HashMap<>();
      if (section == null)
      {
         PotionsPlugin.getInstance().getLogger().warning("No custom-modifier config section");
         return;
      }
      for (String key : section.getKeys(false))
      {
         try
         {
            level = Integer.valueOf(key);
            i_type = Material.valueOf(section.getString(key, "AIR"));
            i_data = (short) section.getInt(key + ".data", 0);

            custom_modifiers.put(i_type.name() + ":" + i_data, level);
         }
         catch (IllegalArgumentException e)
         {
            PotionsPlugin.getInstance().getLogger().warning("Bad value type found in modifiers config");
         }
      }
   }

   public PotionEffectType getResultingPotion(ItemStack item)
   {
      String s = item.getType().name() + ":" + item.getDurability();
      return ingredients.get(s);
   }

   public Map<PotionEffectType, ItemStack> getRecipies()
   {
      Map<PotionEffectType, ItemStack> recipes = new HashMap<>();
      Material mat;
      short data;
      for (Map.Entry<String, PotionEffectType> entry : ingredients.entrySet())
      {
         try
         {
            if (!entry.getKey().contains(":"))
            {
               mat = Material.valueOf(entry.getKey());
               data = 0;
            }
            else
            {
               mat = Material.valueOf(entry.getKey().split(":")[0]);
               data = Short.valueOf(entry.getKey().split(":")[1]);
            }
            recipes.put(entry.getValue(), new ItemStack(mat, 1, data));
         }
         catch (IllegalArgumentException e) { }
      }
      return recipes;
   }

   public int getModifierLevel (ItemStack item)
   {
      String s = item.getType().name() + ":" + item.getDurability();
      return custom_modifiers.getOrDefault(s, -1);
   }

   public boolean isDisabled(PotionEffectType type)
   {
      return config.getStringList("disable-potions").contains(type.getName());
   }

   public void save()
   {
      PotionsPlugin.getInstance().saveConfig();
   }

   public File getFile()
   {
      return file;
   }
}
