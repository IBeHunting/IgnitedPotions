package io.github.IBeHunting.IgnitedPotions.Listeners;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomBrewingStand;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PotionInventoryClick implements Listener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      InventoryHolder holder = event.getInventory().getHolder();
      if (event.getWhoClicked() instanceof Player
              && holder instanceof CustomBrewingStand
              && event.getClickedInventory() != null) {
         brewingClick(event, (CustomBrewingStand) holder);
      }
   }

   private void brewingClick(InventoryClickEvent event, CustomBrewingStand stand) {
      Player player = (Player) event.getWhoClicked();

      if (!isValidClick(event)) {
         event.setCancelled(true);
         return;
      }

      /* Allow user to shift click potions into potion slots */
      if (event.getClick().isShiftClick() && event.getInventory() != event.getClickedInventory()) {
         event.setCancelled(true);
         handleShiftClick(event, stand);
      }

      /* Set the display of the brewing stand to be consistent with the inventory */
      Bukkit.getScheduler().runTaskLater(PotionsPlugin.getInstance(), () -> {
         updateBrewingStands(stand);
         if (Brewing.getInstance().canStartBrew(player, stand))
         {
            new ActiveBrew(stand, player).start();
         }
      }, 1);

   }

   private void updateBrewingStands(CustomBrewingStand stand)
   {
      int potion_slot = 0;
      stand.getStand().getInventory().setIngredient(stand.getIngredient());
      for (ItemStack potion : stand.getPotions())
      {
         stand.getStand().getInventory().setItem(potion_slot++, potion);
      }
   }

   private void tryAddIngredient(InventoryClickEvent event, CustomBrewingStand stand, ItemStack ing)
   {
      if (stand.getIngredient() == null || stand.getIngredient().getType() == Material.AIR)
      {
         event.setCurrentItem(new ItemStack(Material.AIR));
         stand.setIngredient(ing);
      }
   }

   private void tryAddPotion(InventoryClickEvent event, CustomBrewingStand stand, ItemStack potion)
   {
      ItemStack[] orig = stand.getPotions();
      for (int i = 0; i < orig.length; i++)
      {
         if (orig[i] == null || orig[i].getType() == Material.AIR)
         {
            event.setCurrentItem(new ItemStack(Material.AIR));
            stand.setPotion(i, potion);
            return;
         }
      }
   }

   private void handleShiftClick(InventoryClickEvent event, CustomBrewingStand stand)
   {
      ItemStack clicked = event.getCurrentItem();
      if (clicked == null)
      {
         return;
      }
      if (clicked.getType() == Material.POTION)
      {
         tryAddPotion(event, stand, clicked);
      }
      else
      {
         tryAddIngredient(event, stand, clicked);
      }
   }

   private boolean isValidClick(InventoryClickEvent event)
   {
      switch(event.getClick())
      {
         case RIGHT:
         case LEFT:
            if (CustomBrewingStand.isPotionSlot(event.getSlot())
                    && !PotionsPlugin.util().isNone(event.getCursor())
                    && event.getCursor().getType() != Material.POTION)
               return false;
         case NUMBER_KEY:
         case DROP:
         case SHIFT_LEFT:
         case SHIFT_RIGHT:
            return event.getClickedInventory() != event.getInventory()
                    || CustomBrewingStand.isIngredientSlot(event.getSlot())
                    || CustomBrewingStand.isPotionSlot(event.getSlot());
         default: return false;
      }
   }
}
