package io.github.IBeHunting.IgnitedPotions.Commands;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.Config.MessageConfig;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PotionCommand implements CommandExecutor
{
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
   {
      if (args.length > 0)
      {
         switch(args[0].toLowerCase())
         {
            case "reload":
               if (sender.hasPermission("potions.admin"))
               {
                  PotionsPlugin.getInstance().load();
                  sender.sendMessage(MessageConfig.getInstance().getReloadMessage());
               }
               else
               {
                  sendPermissionsMessage(sender);
               }
               return true;
            case "save":
               if (sender.hasPermission("potions.admin"))
               {
                  PotionsPlugin.getInstance().save();
                  sender.sendMessage(MessageConfig.getInstance().getSaveMessage());
               }
               else
               {
                  sendPermissionsMessage(sender);
               }
               return true;
            case "view":
               sendAvailablePotions(sender);
               return true;
         }
         return false;
      }
      sendPluginInfo(sender);
      return true;
   }

   private void sendPluginInfo(CommandSender sender) {
      PluginDescriptionFile desc = PotionsPlugin.getInstance().getDescription();
      String messages[] = {
         ChatColor.AQUA + "" + ChatColor.UNDERLINE + String.format("   %s %s", desc.getName(), desc.getVersion()),
         ChatColor.DARK_AQUA + desc.getDescription(),
         ChatColor.BLUE + "Written by " + String.join(",", desc.getAuthors())};

      sender.sendMessage(messages);
   }

   private void sendPermissionsMessage(CommandSender sender)
   {
      sender.sendMessage(ChatColor.RED + "You do not have permission");
   }

   private void sendAvailablePotions(CommandSender sender)
   {

      Map<String, ItemStack> recipes = new HashMap<>();
      for (Map.Entry<ItemStack, PotionEffectType> entry : Config.getInstance().getRecipies().entrySet())
      {
         if (PotionsPlugin.util().checkPermission(sender, entry.getValue()))
         {
            recipes.put(MessageConfig.getInstance().getPotionName(entry.getValue()), entry.getKey());
         }
      }

      sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Custom Potions Available:");
      for (Map.Entry<String, ItemStack> entry : recipes.entrySet())
      {
         String message = ChatColor.AQUA + entry.getKey() + ChatColor.BLUE +
                 " <--- " + ChatColor.AQUA + PotionsPlugin.util().format(entry.getValue().getType().name());
         if (entry.getValue().getDurability() > 0)
         {
            message = message.concat(":" + entry.getValue().getDurability());
         }
         sender.sendMessage(message);
      }

   }
}
