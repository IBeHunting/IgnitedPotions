package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

   public CustomPotion[] apply(Player player, CustomPotion[] orig, ItemStack ingredient)
   {
      CustomPotion[] results = new CustomPotion[orig.length];
      CustomPotion before, after;
      for (int i = 0; i < orig.length; i++)
      {
         before = orig[i];
         if (before == null)
         {
            continue;
         }
         if (before.isAwkward())
         {
            after = handleAwkward(before, ingredient);
         }
         else if (before.isWater())
         {
            after = handleWater(before, ingredient);
         }
         else if (before.getType() != null) /* An actual potion, not water/mundane/awkward/thick */
         {
            after = handlePotion(before, ingredient);
         }
         else
         {
            continue;
         }

         if (PotionsPlugin.util().checkPermission(player, after.getType()))
         {
            results[i] = after;
         }
         else
         {
            results[i] = before;
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
         case FERMENTED_SPIDER_EYE:
            orig.corrupt();
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

   private CustomPotion handleWater(CustomPotion orig, ItemStack ingredient)
   {
      switch(ingredient.getType())
      {
         case NETHER_STALK:
            /* Make awkward potion */
            return new CustomPotion(null, 0, false, false);
         case GLOWSTONE_DUST:
            /* Make thick potion */
            return new CustomPotion(null, 2, false, false);
         case FERMENTED_SPIDER_EYE:
            /* Make Potion of weakness */
            return new CustomPotion(PotionEffectType.WEAKNESS);
         default:
            /* Make mundane Potion */
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

   public boolean canStartBrew(Player player, CustomBrewingStand stand)
   {
      return brewable(stand)
              && !ActiveBrew.isActive(stand.getStand())
              && hasPermission(player, stand.getIngredient(), stand.getPotions());
   }

   private boolean brewable(CustomBrewingStand stand)
   {
      CustomPotion pot;
      for (ItemStack item : stand.getPotions())
      {
         pot = CustomPotion.fromItem(item);
         if (pot != null && pot.canBeBrewed(stand.getIngredient()))
         {
            return true;
         }
      }
      return false;
   }

   private boolean hasPermission(Player player, ItemStack ingredient, ItemStack[] potions)
   {
      PotionEffectType result = Config.getInstance().getResultingPotion(ingredient);
      CustomPotion cp;
      if (result != null)
      {
         return PotionsPlugin.util().checkPermission(player, result)
                 && !Config.getInstance().isDisabled(result);
      }
      if (ingredient.getType() == Material.FERMENTED_SPIDER_EYE)
      {
         /* If the player has permission to corrupt any of the 3 potions, a brew can start */
         for (ItemStack potion : potions)
         {
            cp = CustomPotion.fromItem(potion);
            if (cp == null)
            {
               continue;
            }
            result = PotionsPlugin.util().getCorruptedEffect(cp);
            if (result == null)
            {
               continue;
            }
            if (PotionsPlugin.util().checkPermission(player, result)
                    && !Config.getInstance().isDisabled(result))
            {
               return true;
            }
         }
         return false;
      }
      return true;
   }
}
