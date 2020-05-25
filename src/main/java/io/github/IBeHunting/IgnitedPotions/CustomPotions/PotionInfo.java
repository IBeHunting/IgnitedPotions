package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PotionInfo
{
   private static final Map<PotionEffectType, PotionInfo> INFO;

   static
   {
      INFO = new HashMap<>();
      INFO.put(PotionEffectType.SPEED, new PotionInfo(true, PotionColor.LIGHT_BLUE, 180, 480));
      INFO.put(PotionEffectType.INCREASE_DAMAGE, new PotionInfo(true, PotionColor.PURPLE, 180, 480));
      INFO.put(PotionEffectType.REGENERATION, new PotionInfo(true, PotionColor.PINK, 45, 120));
      INFO.put(PotionEffectType.FIRE_RESISTANCE, new PotionInfo(false, PotionColor.ORANGE, 180, 480));
      INFO.put(PotionEffectType.POISON, new PotionInfo(true, PotionColor.GREEN, 45, 120));
      INFO.put(PotionEffectType.HEAL, new PotionInfo(true, PotionColor.RED, 0, 0));
      INFO.put(PotionEffectType.HARM, new PotionInfo(true, PotionColor.DARK_PURPLE, 0, 0));
      INFO.put(PotionEffectType.JUMP, new PotionInfo(true, PotionColor.TURQUOISE, 180, 480));
      INFO.put(PotionEffectType.INVISIBILITY, new PotionInfo(false, PotionColor.LIGHT_GRAY, 180, 480));
      INFO.put(PotionEffectType.NIGHT_VISION, new PotionInfo(false, PotionColor.DARK_BLUE, 180, 480));
      INFO.put(PotionEffectType.WATER_BREATHING, new PotionInfo(false, PotionColor.BLUE, 180, 480));
      INFO.put(PotionEffectType.WEAKNESS, new PotionInfo(true, PotionColor.BLACK, 90, 240));
      INFO.put(PotionEffectType.SLOW, new PotionInfo(true, PotionColor.DARK_GRAY, 90, 240));
      INFO.put(PotionEffectType.FAST_DIGGING, new PotionInfo(true, PotionColor.LIGHT_GRAY, 180, 480));
      INFO.put(PotionEffectType.SLOW_DIGGING, new PotionInfo(true, PotionColor.BLACK, 90, 240));
      INFO.put(PotionEffectType.HUNGER, new PotionInfo(true, PotionColor.GREEN, 90, 240));
      INFO.put(PotionEffectType.SATURATION, new PotionInfo(true, PotionColor.TURQUOISE, 180, 480));
      INFO.put(PotionEffectType.ABSORPTION, new PotionInfo(true, PotionColor.ORANGE, 45, 120));
      INFO.put(PotionEffectType.HEALTH_BOOST, new PotionInfo(true, PotionColor.RED, 90, 240));
      INFO.put(PotionEffectType.BLINDNESS, new PotionInfo(true, PotionColor.DARK_GRAY, 45, 120));
      INFO.put(PotionEffectType.CONFUSION, new PotionInfo(true, PotionColor.DARK_BLUE, 45, 120));
      INFO.put(PotionEffectType.DAMAGE_RESISTANCE, new PotionInfo(true, PotionColor.LIGHT_GRAY, 180, 480));
      INFO.put(PotionEffectType.WITHER, new PotionInfo(true, PotionColor.BLACK, 45, 120));
   }

   public static boolean hasTiers (PotionEffectType type)
   {
      if (INFO.containsKey(type))
      {
         return INFO.get(type).has_tiers;
      }
      return false;
   }

   public static PotionColor getColor (PotionEffectType type)
   {
      if (INFO.containsKey(type))
      {
         return INFO.get(type).color;
      }
      return PotionColor.BLUE;
   }

   public static int getDurationNormal (PotionEffectType type)
   {
      if (INFO.containsKey(type))
      {
         return INFO.get(type).duration;
      }
      return 0;
   }

   public static int getDurationExtended (PotionEffectType type)
   {
      if (INFO.containsKey(type))
      {
         return INFO.get(type).duration_ext;
      }
      return 0;
   }

   private boolean has_tiers;
   private PotionColor color;
   private int duration;
   private int duration_ext;

   private PotionInfo (boolean has_tiers, PotionColor color, int duration, int duration_ext)
   {
      this.has_tiers = has_tiers;
      this.color = color;
      this.duration = duration;
      this.duration_ext = duration_ext;
   }
}
