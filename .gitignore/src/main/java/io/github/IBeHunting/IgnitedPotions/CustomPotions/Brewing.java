package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class Brewing
{
   private static Brewing i;

   private Brewing()
   {
      i = this;
   }

   public static Brewing getInstance()
   {
      if (i == null)
      {
         i = new Brewing();
      }
      return i;
   }

   public CustomPotion[] apply(CustomPotion[] orig, ItemStack ingredient)
   {
      CustomPotion[] results = new CustomPotion[3];
      CustomPotion cp;
      for (int i = 0; i < 3; i++)
      {
         cp = orig[i];
         if (cp == null)
         {
            continue;
         }
         if (cp.isAwkwardPotion())
         {
            results[i] = handleAwkward(cp, ingredient);
         }
         else if (cp.isWater())
         {
            results[i] = handleWater(ingredient);
         }
         else if (cp.getType() != null) /* An actual potion, not water/mundane/awkward/thick */
         {
            results[i] = handlePotion(cp, ingredient);
         }
         else
         {
            results[i] = cp;
         }
      }
      return results;
   }

   private CustomPotion handlePotion (CustomPotion orig, ItemStack ingredient)
   {
      int level;

      /* Vanilla modifiers */
      switch(ingredient.getType())
      {
         case SULPHUR:
            orig.applyVanillaGunpowder();
            return orig;
         case REDSTONE:
            orig.applyVanillaRedstone();
            return orig;
         case GLOWSTONE_DUST:
            orig.applyVanillaGlowstone();
            return orig;
      }
      /* Custom tier modifiers */
      level = Config.getInstance().getModifierLevel(ingredient);
      if (level > 0)
      {
         orig.setTier(level);
      }
      return orig;
   }

   private CustomPotion handleWater(ItemStack ingredient)
   {
      switch(ingredient.getType())
      {
         case NETHER_WARTS: /* Make awkward potion */
            return new CustomPotion(null, 0, false, false);
         case GLOWSTONE_DUST: /* Make thick potion */
            return new CustomPotion(null, 2, false, false);
         default: /* Make mundane Potion */
            return new CustomPotion(null, 1, true, false);
      }
   }

   private CustomPotion handleAwkward(CustomPotion orig, ItemStack ingredient)
   {
      PotionEffectType result = Config.getInstance().getResultingPotion(ingredient);
      if (result == null)
      {
         return orig;
      }
      return new CustomPotion(result);
   }

   public boolean isIngredient(ItemStack item)
   {
      if (item == null)
      {
         return false;
      }
      return Config.getInstance().getResultingPotion(item) != null;
   }

   public boolean isModifier (ItemStack item)
   {
      if (item == null)
      {
         return false;
      }
      switch(item.getType())
      {
         case GLOWSTONE_DUST:
         case REDSTONE:
         case SULPHUR:
            return true;
      }
      return Config.getInstance().getModifierLevel(item) > 0;
   }
}
