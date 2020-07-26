package io.github.IBeHunting.IgnitedPotions.Util;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomBrewingStand;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperMovementHandler
{
   private static HopperMovementHandler i;

   private HopperMovementHandler() {}

   public static HopperMovementHandler getInstance()
   {
      if (i == null)
      {
         i = new HopperMovementHandler();
      }
      return i;
   }

   public void handleTransferTo(CustomBrewingStand cbs, ItemStack item, Inventory source)
   {
      boolean above = isSourceAbove(cbs, source);
      if (above)
      {
         tryAddIngredient(cbs, item, source);
      }
      else
      {
         tryAddPotion(cbs, item, source);
      }
   }

   private void tryAddIngredient(CustomBrewingStand cbs, ItemStack item, Inventory source)
   {
      Bukkit.getScheduler().runTaskLater(PotionsPlugin.getInstance(), () -> {
         ItemStack orig = cbs.getIngredient();
         if (!source.containsAtLeast(item, 1))
         {
            return;
         }
         if (PotionsPlugin.util().isNone(orig))
         {
            source.removeItem(item);
            cbs.setIngredient(item);
         }
         else if (orig.getType() == item.getType()
                 && orig.getAmount() + item.getAmount() < orig.getMaxStackSize())
         {
            source.removeItem(item);
            orig.setAmount(orig.getAmount() + item.getAmount());
         }
         else
         {
            return;
         }
         if (Brewing.getInstance().canStartBrew(null, cbs))
         {
            new ActiveBrew(cbs, null).start();
         }
      }, 1L);
   }

   private void tryAddPotion(CustomBrewingStand cbs, ItemStack item, Inventory source)
   {
      if (item.getType() != Material.POTION)
      {
         return;
      }
      Bukkit.getScheduler().runTaskLater(PotionsPlugin.getInstance(), () -> {
         ItemStack potion;
         if (!source.containsAtLeast(item, 1))
         {
            return;
         }
         for (int i = 0; i < cbs.getPotions().length; i++)
         {
            potion = cbs.getPotions()[i];
            if (PotionsPlugin.util().isNone(potion))
            {
               source.removeItem(item);
               cbs.setPotion(i, item);
               if (Brewing.getInstance().canStartBrew(null, cbs))
               {
                  new ActiveBrew(cbs, null).start();
               }
               break;
            }
         }
      }, 1L);
   }

   public void handleTransferFrom(CustomBrewingStand cbs, ItemStack item, Inventory dest)
   {
      Bukkit.getScheduler().runTaskLater(PotionsPlugin.getInstance(), () -> {
         int dest_slot = dest.firstEmpty();
         if (dest_slot >= 0 && cbs.removePotion(item))
         {
            dest.setItem(dest_slot, item);
         }
      }, 1L);
   }

   private boolean isSourceAbove(CustomBrewingStand s, Inventory source)
   {
      /* Yes, I know this is not very elegant, but I could'nt find a better interface in spigot 1.8 */
      Block sourceBlock =
              (source.getHolder() instanceof Hopper) ?
                      ((Hopper)source.getHolder()).getBlock() :
              (source.getHolder() instanceof Dropper) ?
                      ((Dropper)source.getHolder()).getBlock() :
              (source.getHolder() instanceof Dispenser) ?
                      ((Dispenser)source.getHolder()).getBlock() :
              (source.getHolder() instanceof Chest) ?
                      ((Dispenser)source.getHolder()).getBlock() :
              (source.getHolder() instanceof Furnace) ?
                      ((Furnace)source.getHolder()).getBlock() :
              (source.getHolder() instanceof BrewingStand) ?
                      ((BrewingStand)source.getHolder()).getBlock() :
              (source.getHolder() instanceof Beacon) ?
                      ((Beacon)source.getHolder()).getBlock() :
                      null;

      if (sourceBlock == null)
      {
         return false;
      }
      return s.getStand().getBlock().getFace(sourceBlock) == BlockFace.UP;
   }
}
