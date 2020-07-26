package io.github.IBeHunting.IgnitedPotions.Config;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config implements PluginConfig
{
   private static Config i;

   private FileConfiguration config;
   private File file;

   private Map<ItemStack, PotionEffectType> ingredients;
   private Map<ItemStack, Integer> custom_modifiers;

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
      if (!file.exists())
      {
         PotionsPlugin.getInstance().saveResource("config.yml", true);
      }
      this.config = YamlConfiguration.loadConfiguration(file);

      loadIngredients();
      loadModifiers();
   }

   private ItemStack parseItem(String serialized)
   {
      try
      {
         if (serialized.contains(":"))
         {
            return new ItemStack(
                    Material.valueOf(serialized.split(":")[0]),
                    Short.valueOf(serialized.split(":")[1])
            );
         }
         else
         {
            return new ItemStack(Material.valueOf(serialized));
         }
      }
      catch (IllegalArgumentException | NullPointerException e)
      {
         Bukkit.getLogger().warning("Unable to parse ItemStack " + serialized);
         return null;
      }
   }

   private void loadIngredients()
   {
      ConfigurationSection section;
      String serialized;
      ItemStack ingredient;
      PotionEffectType result;
      this.ingredients = new HashMap<>();
      if (!config.isConfigurationSection("potion-ingredients"))
      {
         Bukkit.getLogger().warning("potion ingredients not found in config");
         return;
      }
      section = config.getConfigurationSection("potion-ingredients");
      for (String effect : section.getKeys(false))
      {
         result = PotionEffectType.getByName(effect);
         if (result == null)
         {
            Bukkit.getLogger().warning("Potion effect type " + effect + " in config is invalid");
            continue;
         }
         serialized = section.getString(effect);
         if (serialized == null)
         {
            Bukkit.getLogger().warning("Invalid config entry on ingredient for " + effect);
            continue;
         }
         ingredient = parseItem(serialized);
         if (ingredient != null)
         {
            this.ingredients.put(ingredient, result);
         }
      }
   }

   private void loadModifiers()
   {
      ConfigurationSection section;
      int level;
      ItemStack modifier;
      this.custom_modifiers = new HashMap<>();
      if (!config.isConfigurationSection("extra-tiers"))
      {
         Bukkit.getLogger().warning("No custom-modifier config section");
         return;
      }
      section = config.getConfigurationSection("extra-tiers");
      for (String tier : section.getKeys(false))
      {
         try
         {
            level = Integer.valueOf(tier);
         }
         catch (IllegalArgumentException e)
         {
            Bukkit.getLogger().warning("Invalid tier value in config");
            continue;
         }
         modifier = parseItem(section.getString(tier));
         if (modifier != null)
         {
            this.custom_modifiers.put(modifier, level);
         }
      }
   }

   public PotionEffectType getResultingPotion(ItemStack ingredient)
   {
      /* Create a new item with amount set to 1 */
      ItemStack type = new ItemStack(ingredient.getType(), 1, ingredient.getDurability());
      return ingredients.get(type);
   }

   public Map<ItemStack, PotionEffectType> getRecipies()
   {
      return ingredients;
   }

   public int getModifierLevel (ItemStack item)
   {
      ItemStack type = new ItemStack(item.getType(), 1, item.getDurability());
      return custom_modifiers.getOrDefault(type, -1);
   }

   public boolean isDisabled(PotionEffectType type)
   {
      return config.getStringList("disable-potions").contains(type.getName());
   }

   public boolean usingConflictingModifiers()
   {
      return config.getBoolean("conflicting_amplifier_duration", false);
   }

   public int getBrewingTicks()
   {
      return config.getInt("brewing_time", 400);
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
