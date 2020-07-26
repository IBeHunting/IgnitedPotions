package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.Config.MessageConfig;
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
      this.extended = (data & 64) > 0;
      this.splash = (data & 16384) > 0;
   }

   public PotionEffectType getType()
   {
      return type;
   }

   public void applyVanillaGlowstone()
   {
      this.tier = 2;
      if (Config.getInstance().usingConflictingModifiers())
      {
         this.extended = false;
      }
   }

   public void applyVanillaRedstone()
   {
      this.extended = true;
      if (Config.getInstance().usingConflictingModifiers())
      {
         this.tier = 1;
      }
   }

   public void applyVanillaGunpowder()
   {
      this.splash = true;
   }

   public void corrupt()
   {
      if (type == null)
      {
         return;
      }
      switch(type.getId())
      {
         case 1: /* Speed */
         case 8: /* Jump */
            this.type = PotionEffectType.SLOW;
            break;
         case 6: /* Healing */
         case 19: /* Poison */
            this.type = PotionEffectType.HARM;
            break;
         case 16: /* Night Vision */
            this.type = PotionEffectType.INVISIBILITY;
            break;

      }
   }

   public void setTier(int tier)
   {
      this.tier = tier;
      if (Config.getInstance().usingConflictingModifiers())
      {
         this.extended = false;
      }
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

   public ItemStack getItem()
   {
      ItemStack potion;
      PotionMeta meta;
      int adjusted_tier;

      potion = new ItemStack(Material.POTION, 1, getData());
      if (type != null)
      {
         meta = (PotionMeta) potion.getItemMeta();
         adjusted_tier = PotionInfo.hasTiers(type) ? tier - 1 : 0;
         meta.addCustomEffect(new PotionEffect(type, getDurationSeconds() * 20, adjusted_tier), true);
         meta.setDisplayName(ChatColor.WHITE +
                 (splash ? "Splash " : "") + MessageConfig.getInstance().getPotionName(type));
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
      if (type == null)
      {
         return (short) (extended ? 64 : tier > 1 ? 32 : isAwkward()  ? 16 : 0);
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
      if (ingredient == null)
      {
         return false;
      }
      if (isWater())
      {
         switch(ingredient.getType())
         {
            case NETHER_STALK:
            case SULPHUR:
            case REDSTONE:
            case GLOWSTONE_DUST:
            case SPIDER_EYE:
            case MAGMA_CREAM:
            case GHAST_TEAR:
            case SUGAR:
            case RABBIT_FOOT:
            case BLAZE_POWDER:
            case SPECKLED_MELON:
            case FERMENTED_SPIDER_EYE:
               return true;
            default: return false;
         }
      }
      if (isAwkward())
      {
         return Brewing.getInstance().isIngredient(ingredient);
      }
      switch(ingredient.getType())
      {
         case REDSTONE:
            return !extended;
         case GLOWSTONE_DUST:
            return tier < 2;
         case SULPHUR:
            return !splash;
         case FERMENTED_SPIDER_EYE:
            return type != null && (type.equals(PotionEffectType.NIGHT_VISION)
                    || type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.HEAL)
                    || type.equals(PotionEffectType.SPEED) || type.equals(PotionEffectType.JUMP));
      }
      return (tier < Config.getInstance().getModifierLevel(ingredient));
   }

   public boolean isWater()
   {
      return type == null && tier == 1;
   }

   public boolean isAwkward()
   {
      return type == null && tier <= 0;
   }

   @Override
   public String toString()
   {
      return "POTION{type="
              + (type == null ? "none": type.getName())
              + (tier != 1 ? ", tier=" + tier : "")
              + (extended ? ", extended" : "")
              + (splash ? ", splash": "")
              + "}";
   }
}
