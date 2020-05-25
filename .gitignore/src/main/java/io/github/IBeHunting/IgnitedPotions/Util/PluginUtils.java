package io.github.IBeHunting.IgnitedPotions.Util;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PluginUtils
{
   public String toTime (int seconds)
   {
      StringBuilder sb = new StringBuilder();
      sb.append(seconds / 60 % 60);
      sb.append(":");
      sb.append(String.format("%02d", seconds % 60));
      return sb.toString();
   }

   public String toNumeral (int n)
   {
      switch(n)
      {
         case 1: return "I";
         case 2: return "II";
         case 3: return "III";
         case 4: return "IV";
         case 5: return "V";
         case 6: return "VI";
         case 7: return "VII";
         case 8: return "VIII";
         case 9: return "IX";
         case 10: return "X";
         default: return String.valueOf(n);
      }
   }

   public String format (String orig)
   {
      StringBuilder sb = new StringBuilder();
      boolean empty = true;
      String[] split = orig.split("_");
      for (String str : split)
      {
         if (!empty)
         {
            sb.append(" ");
         }
         empty = false;
         if (str.length() > 1)
         {
            sb.append(str.substring(0, 1).toUpperCase());
            sb.append(str.substring(1).toLowerCase());
         }
         else
         {
            sb.append(str.toUpperCase());
         }
      }
      return sb.toString();
   }

   public boolean checkPermission(CommandSender player, ItemStack ingredient)
   {
      PotionEffectType type = Config.getInstance().getResultingPotion(ingredient);
      if (type == null)
      {
         return true;
      }
      switch(type.getId())
      {
         case 3: return player.hasPermission("potions.brew.haste");
         case 4: return player.hasPermission("potions.brew.mining_fatigue");
         case 9: return player.hasPermission("potions.brew.nausea");
         case 11: return player.hasPermission("potions.brew.resistance");
         case 15: return player.hasPermission("potions.brew.blindness");
         case 17: return player.hasPermission("potions.brew.hunger");
         case 20: return player.hasPermission("potions.brew.wither");
         case 21: return player.hasPermission("potions.brew.health_boost");
         case 22: return player.hasPermission("potions.brew.absorption");
         case 23: return player.hasPermission("potions.brew.saturation");
         default: return true;
      }
   }
}
