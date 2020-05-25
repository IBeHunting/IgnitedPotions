package io.github.IBeHunting.IgnitedPotions;

import io.github.IBeHunting.IgnitedPotions.Commands.PotionCommand;
import io.github.IBeHunting.IgnitedPotions.Commands.PotionGiveCommand;
import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.Config.MessageConfig;
import io.github.IBeHunting.IgnitedPotions.Config.PluginConfig;
import io.github.IBeHunting.IgnitedPotions.Listeners.PotionInventoryClick;
import io.github.IBeHunting.IgnitedPotions.Util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PotionsPlugin extends JavaPlugin
{
   private static PotionsPlugin i;
   private static PluginUtils util;

   public static PotionsPlugin getInstance()
   {
      return i;
   }

   public static PluginUtils util()
   {
      return util;
   }

   private List<PluginConfig> configs;

   @Override
   public void onEnable()
   {
      i = this;
      util = new PluginUtils();
      this.configs = new ArrayList<>();
      addConfigurations();
      load();
      registerCommands();
      registerEvents();
   }

   @Override
   public void onDisable()
   {
      save();
      getLogger().info("Plugin has been sucessfully saved");
   }

   public void save()
   {
      saveConfig();
      configs.forEach(PluginConfig::save);

   }

   public void load()
   {
      if (!new File(getDataFolder(), "config.yml").exists())
      {
         saveResource("config.yml", false);
      }
      reloadConfig();
      configs.forEach(PluginConfig::load);
   }

   private void addConfigurations()
   {
      configs.add(new Config());
      configs.add(new MessageConfig());
   }

   private void registerCommands()
   {
      getCommand("potions").setExecutor(new PotionCommand());
      getCommand("givepotion").setExecutor(new PotionGiveCommand());
   }

   private void registerEvents()
   {
      PluginManager manager = Bukkit.getPluginManager();

      manager.registerEvents(new PotionInventoryClick(),this);
   }
}
