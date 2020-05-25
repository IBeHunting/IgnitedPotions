package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.Config.MessageConfig;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Function;

public class CustomPotion
{
   private PotionEffectType type;
   private int tier;
   private boolean splash;
   private boolean extended;

   public CustomPotion(PotionEffectType type)
   {
      this(type, 1, false, false);
   }

   public CustomPotion(PotionEffectType type, int tier, boolean extended, boolean splash)
   {
      this.type = type;
      this.tier = tier;
      this.extended = extended;
      this.splash = splash;
   }

   private PotionEffectType typeFromData(short data)
   {
      switch(data % 16)
      {
         case 1: return PotionEffectType.REGENERATION;
         case 2: return PotionEffectType.SPEED;
         case 3: return PotionEffectType.FIRE_RESISTANCE;
         case 4: return PotionEffectType.POISON;
         case 5: return PotionEffectType.HEAL;
         case 6: return PotionEffectType.NIGHT_VISION;
         case 8: return PotionEffectType.WEAKNESS;
         case 9: return PotionEffectType.INCREASE_DAMAGE;
         case 10: return PotionEffectType.SLOW;
         case 11: return PotionEffectType.JUMP;
         case 12: return PotionEffectType.HARM;
         case 13: return PotionEffectType.WATER_BREATHING;
         case 14: return PotionEffectType.INVISIBILITY;
         default: return null;
      }
   }

   private int tierFromData(short data)
   {
      if (data == 16)
      {
         return 0;
      }
      return (data & 32) > 0 ? 2 : 1;
   }

   private CustomPotion(short data)
   {
      this.type = typeFromData(data);
      this.tier = tierFromData(data);
      this.extended = (data | 64) > 0;
      this.splash = (data | 16384) > 0;
   }

   public PotionEffectType getType()
   {
      return type;
   }

   public void applyVanillaGlowstone()
   {
      this.tier = 2;
      this.extended = false;
   }

   public void applyVanillaRedstone()
   {
      this.tier = 1;
      this.extended = true;
   }

   public void applyVanillaGunpowder()
   {
      this.splash = true;
   }

   public void setTier(int tier)
   {
      this.tier = tier;
      this.extended = false;
   }

   public int getDurationSeconds()
   {
      int seconds = extended ? PotionInfo.getDurationExtended(type) : PotionInfo.getDurationNormal(type);

      if (tier > 1 && PotionInfo.hasTiers(type))
      {
         seconds *= 0.5;
      }
      if (splash)
      {
         seconds *= 0.75;
      }
      return seconds;
   }

   private boolean isCustomEffect()
   {
      if (type == null)
      {
         return false;
      }
      return type.equals(PotionEffectType.FAST_DIGGING)
              || type.equals(PotionEffectType.SLOW_DIGGING)
              || type.equals(PotionEffectType.SATURATION)
              || type.equals(PotionEffectType.HUNGER)
              || type.equals(PotionEffectType.ABSORPTION)
              || type.equals(PotionEffectType.HEALTH_BOOST)
              || type.equals(PotionEffectType.BLINDNESS)
              || type.equals(PotionEffectType.CONFUSION)
              || type.equals(PotionEffectType.DAMAGE_RESISTANCE)
              || type.equals(PotionEffectType.WITHER);
   }

   public ItemStack getItem()
   {
      ItemStack potion;
      PotionMeta meta;
      int adjusted_tier;

      potion = new ItemStack(Material.POTION, 1, getData());
      if (isCustomEffect())
      {
         meta = (PotionMeta) potion.getItemMeta();
         adjusted_tier = PotionInfo.hasTiers(type) ? tier - 1 : 0;
         meta.addCustomEffect(new PotionEffect(type, getDurationSeconds() * 20, adjusted_tier), true);
         meta.setDisplayName(ChatColor.WHITE + MessageConfig.getInstance().getPotionName(type));
         potion.setItemMeta(meta);
      }

      return potion;
   }

   private short getData()
   {
      short data;
      Function<PotionEffectType, Short> dt = (type) ->
      {
         switch(PotionInfo.getColor(type))
         {
            case PINK: return (short) 1;
            case LIGHT_BLUE: return (short) 2;
            case ORANGE: return (short) 3;
            case GREEN: return (short) 4;
            case RED: return (short) 5;
            case DARK_BLUE: return (short) 6;
            case BLACK: return (short) 8;
            case PURPLE: return (short) 9;
            case DARK_GRAY: return (short) 10;
            case TURQUOISE: return (short) 11;
            case DARK_PURPLE: return (short) 12;
            case BLUE: return (short) 13;
            case LIGHT_GRAY: return (short) 14;

            default: return (short) 0;
         }
      };
      if (isAwkwardPotion())
      {
         return 16;
      }
      if (type == null)
      {
         return (short) (extended ? 64 : tier > 1 ? 32 : 0);
      }
      data = dt.apply(type);
      if (splash)
      {
         data |= 16384;
      }
      if (extended)
      {
         data |= 64;
      }
      if (tier > 1)
      {
         data |= 32;
      }
      return data;
   }

   public static CustomPotion fromItem (ItemStack item)
   {
      PotionMeta meta;
      PotionEffect effect;
      boolean splash, extended;
      if (item == null)
      {
         return null;
      }
      if (!(item.getItemMeta() instanceof PotionMeta))
      {
         PotionsPlugin.getInstance().getLogger().warning("Cannot create Custom potion: No potionmeta found");
         return null;
      }
      if (item.getDurability() == 16)
      {
         // Awkward Potion
         return new CustomPotion(null, 0, false, false);
      }
      meta = (PotionMeta) item.getItemMeta();
      if (meta.hasCustomEffects())
      {
         effect = meta.getCustomEffects().get(0);
         splash = (item.getDurability() & 16384) != 0;
         extended = effect.getDuration() >  20 *  PotionInfo.getDurationNormal(effect.getType());
         return new CustomPotion(effect.getType(), effect.getAmplifier() + 1, extended, splash);
      }
      return new CustomPotion(item.getDurability());
   }

   public boolean canBeBrewed(ItemStack ingredient)
   {
      return ingredient != null && (
              (ingredient.getType() == Material.NETHER_WARTS && isWater())
              || (Brewing.getInstance().isIngredient(ingredient) && isAwkwardPotion())
              || (ingredient.getType() == Material.SULPHUR && !splash)
              || (ingredient.getType() == Material.REDSTONE && !extended)
              || (ingredient.getType() == Material.GLOWSTONE_DUST && tier < 2)
              || (tier < Config.getInstance().getModifierLevel(ingredient))
      );
   }

   boolean isAwkwardPotion()
   {
      return type == null && tier == 0;
   }

   boolean isWater()
   {
      return type == null && tier == 1;
   }
}
