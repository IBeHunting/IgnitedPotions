package io.github.IBeHunting.IgnitedPotions.Util;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
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

   public boolean checkPermission(CommandSender player, PotionEffectType effect)
   {
      String perm = getPermission(effect);
      Permission p;
      if (perm == null)
      {
         return true;
      }
      if (player == null)
      {
         p = Bukkit.getPluginManager().getPermission(perm);
         if (p == null)
         {
            Bukkit.getLogger().warning("No permission found for effect " + effect.getName());
            return true;
         }
         return p.getDefault() == PermissionDefault.TRUE;
      }
      return player.hasPermission(perm);
   }

   public String getPermission (PotionEffectType type)
   {
      if (type == null)
      {
         return null;
      }
      switch (type.getId())
      {
         case 1: return "potions.brew.speed";
         case 2: return "potions.brew.slowness";
         case 3: return "potions.brew.haste";
         case 4: return "potions.brew.mining_fatigue";
         case 5: return "potions.brew.strength";
         case 6: return "potions.brew.instant_health";
         case 7: return "potions.brew.instant_damage";
         case 8: return "potions.brew.jump_boost";
         case 9: return "potions.brew.nausea";
         case 10: return "potions.brew.regeneration";
         case 11: return "potions.brew.resistance";
         case 12: return "potions.brew.fire_resistance";
         case 13: return "potions.brew.water_breathing";
         case 14: return "potions.brew.invisibility";
         case 15: return "potions.brew.blindness";
         case 16: return "potions.brew.night_vision";
         case 17: return "potions.brew.hunger";
         case 18: return "potions.brew.invisibility";
         case 19: return "potions.brew.poison";
         case 20: return "potions.brew.wither";
         case 21: return "potions.brew.health_boost";
         case 22: return "potions.brew.absorption";
         case 23: return "potions.brew.saturation";
         default: return null;
      }
   }

   public PotionEffectType getCorruptedEffect(CustomPotion potion)
   {
      if (potion.isWater())
      {
         return PotionEffectType.WEAKNESS;
      }
      if (potion.getType() == null)
      {
         return null;
      }
      switch (potion.getType().getId())
      {
         case 1: /* Orig = speed */
         case 8: /* Orig = Jump boost */
            return PotionEffectType.SLOW;
         case 6: /* Orig = Inst. Health */
         case 19: /* Orig = Poison */
            return PotionEffectType.HARM;
         case 16: /* Orig = Night Vision */
            return PotionEffectType.INVISIBILITY;
         default: return null;
      }
   }

   public boolean isNone (ItemStack item)
   {
      return item == null || item.getType() == Material.AIR;
   }
}
