package io.github.IBeHunting.IgnitedPotions.Events;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class CustomBrewEvent extends Event implements Cancellable
{
   private static final HandlerList HANDLERS = new HandlerList();
   private boolean cancelled;

   private BrewingStand stand;
   private Player player;
   private ItemStack ingredient;
   private CustomPotion[] potions;

   CustomBrewEvent(Player player, BrewerInventory inv)
   {
      this.player = player;
      this.ingredient = inv.getIngredient();
      this.potions = new CustomPotion[3];
      this.stand = inv.getHolder();
      for (int i = 0; i < 3; i++)
      {
         potions[i] = CustomPotion.fromItem(inv.getContents()[i]);
      }
   }

   public HandlerList getHandlers()
   {
      return HANDLERS;
   }

   public static HandlerList getHandlerList()
   {
      return HANDLERS;
   }

   public void setCancelled(boolean cancelled)
   {
      this.cancelled = cancelled;
   }

   public boolean isCancelled()
   {
      return cancelled;
   }

   public ItemStack getIngredient()
   {
      return ingredient;
   }

   public CustomPotion[] getPotions()
   {
      return potions;
   }

   public BrewingStand getStand()
   {
      return stand;
   }

   public Player getWhoBrewed()
   {
      return player;
   }

}
