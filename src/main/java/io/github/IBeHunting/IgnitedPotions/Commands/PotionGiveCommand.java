package io.github.IBeHunting.IgnitedPotions.Commands;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PotionGiveCommand implements CommandExecutor
{
   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String args[])
   {
      /* givepotion <player> <type> [tier=1] [extended=false] [splash=false] */
      Player player;
      PotionEffectType type;
      int tier;
      boolean splash, ext;
      if (sender instanceof ConsoleCommandSender)
      {
         sender.sendMessage(ChatColor.DARK_RED + "This command is for players only");
         return true;
      }

      if (args.length < 2)
      {
         return false;
      }
      player = Bukkit.getPlayer(args[0]);
      if (player == null)
      {
         sender.sendMessage(ChatColor.RED + "Player not found");
         return true;
      }
      type = getTypeArgument(args, 1);
      if (type == null)
      {
         sender.sendMessage(ChatColor.RED + "Potion not found");
         return true;
      }
      tier = getIntArgument(args, 2, 1);
      ext = getBoolArgument(args, 3, false);
      splash = getBoolArgument(args, 4, false);
      player.getInventory().addItem(new CustomPotion(type, tier, ext, splash).getItem());

      return true;
   }

   private int getIntArgument(String args[], int i, int def)
   {
      if (args.length > i)
      {
         try
         {
            return Integer.parseInt(args[i]);
         }
         catch (NumberFormatException e)
         {
            return def;
         }
      }
      return def;
   }

   private boolean getBoolArgument(String args[], int i, boolean def)
   {
      if (args.length > i)
      {
         return Boolean.parseBoolean(args[i]);
      }
      return def;
   }

   public PotionEffectType getTypeArgument(String args[], int argc)
   {
      if (args.length > argc)
      {
         switch(args[argc].toUpperCase())
         {
            case "SPEED": return PotionEffectType.SPEED;
            case "STRENGTH": return PotionEffectType.INCREASE_DAMAGE;
            case "FIRE_RESISTANCE": return PotionEffectType.FIRE_RESISTANCE;
            case "REGENERATION": return PotionEffectType.REGENERATION;
            case "INSTANT_HEALTH": return PotionEffectType.HEAL;
            case "INSTANT_DAMAGE": return PotionEffectType.HARM;
            case "INVISIBILITY": return PotionEffectType.INVISIBILITY;
            case "WATER_BREATHING": return PotionEffectType.WATER_BREATHING;
            case "NIGHT_VISION": return PotionEffectType.NIGHT_VISION;
            case "POISON": return PotionEffectType.POISON;
            case "SLOWNESS": return PotionEffectType.SLOW;
            case "WEAKNESS": return PotionEffectType.WEAKNESS;
            case "JUMP_BOOST": return PotionEffectType.JUMP;
            case "HASTE": return PotionEffectType.FAST_DIGGING;
            case "MINING_FATIGUE": return PotionEffectType.SLOW_DIGGING;
            case "HUNGER": return PotionEffectType.HUNGER;
            case "SATURATION": return PotionEffectType.SATURATION;
            case "RESISTANCE": return PotionEffectType.DAMAGE_RESISTANCE;
            case "NAUSEA": return PotionEffectType.CONFUSION;
            case "BLINDNESS": return PotionEffectType.BLINDNESS;
            case "ABSORPTION": return PotionEffectType.ABSORPTION;
            case "HEALTH_BOOST": return PotionEffectType.HEALTH_BOOST;
            case "WITHER": return PotionEffectType.WITHER;
            default: return null;
         }
      }
      return null;
   }
}
