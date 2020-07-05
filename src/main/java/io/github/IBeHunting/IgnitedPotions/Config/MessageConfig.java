package io.github.IBeHunting.IgnitedPotions.Config;

import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;

public class MessageConfig implements PluginConfig
{
   private static MessageConfig i;

   private File file;
   private YamlConfiguration conf;

   public MessageConfig()
   {
      i = this;
      file =  new File(PotionsPlugin.getInstance().getDataFolder(), "messages.yml");
      conf = new YamlConfiguration();
   }

   public static MessageConfig getInstance()
   {
      return i;
   }

   public String getReloadMessage()
   {
      return getMessage("reload-message", "&aPlugin has been reloaded");
   }

   public String getSaveMessage()
   {
      return getMessage("save-message", "&aPlugin has been saved");
   }

   public String getPotionName(PotionEffectType type)
   {
      return getMessage("potion-names." + type.getName(), "Potion of " + PotionsPlugin.util().format(type.getName()));
   }

   public String getStandName()
   {
      return getMessage("brewing-stand-title", "&aBrewing Stand");
   }

   private String getMessage(String message, String def)
   {
      return ChatColor.translateAlternateColorCodes('&',
              conf.getString(message, def));
   }

   public File getFile()
   {
      return file;
   }

   public void load()
   {
      if (!file.exists())
      {
         PotionsPlugin.getInstance().saveResource("messages.yml", true);
      }
      this.conf = YamlConfiguration.loadConfiguration(file);
   }

   public void save()
   {
      try
      {
         conf.save(file);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
