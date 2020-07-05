package io.github.IBeHunting.IgnitedPotions.Listeners;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomBrewingStand;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class StandInteraction implements Listener
{
   @EventHandler
   public void onStandOpen(PlayerInteractEvent event)
   {
      BrewingStand stand;
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK
              && event.getClickedBlock() != null
              && event.getClickedBlock().getState() instanceof BrewingStand)
      {
         event.setCancelled(true);
         stand = (BrewingStand) event.getClickedBlock().getState();
         getBrewingInventory(stand).open(event.getPlayer());
      }
   }

   @EventHandler
   public void onStandBreak(BlockBreakEvent event)
   {
      if (event.getBlock().getState() instanceof BrewingStand)
      {
         event.setCancelled(true);
         removeStand((BrewingStand) event.getBlock().getState());
      }
   }

   @EventHandler
   public void onStandExplode(EntityExplodeEvent event)
   {
      Iterator<Block> iter = event.blockList().iterator();
      Block current;
      while (iter.hasNext())
      {
         current = iter.next();
         if (current.getState() instanceof BrewingStand)
         {
            iter.remove();
            removeStand((BrewingStand) current.getState());
         }
      }
   }

   private void removeStand(BrewingStand stand)
   {
      Location loc = stand.getLocation();
      CustomBrewingStand custom = getBrewingInventory(stand);

      loc.getWorld().dropItem(loc, new ItemStack(Material.BREWING_STAND_ITEM));

      stand.getInventory().clear();
      stand.getBlock().setType(Material.AIR);

      /* Anyone viewing the brewing stand must close the inventory */
      stand.getInventory().getViewers().forEach(HumanEntity::closeInventory);

      if (custom.getIngredient() != null && custom.getIngredient().getType() != Material.AIR)
      {
         loc.getWorld().dropItem(loc, custom.getIngredient());
      }

      for (ItemStack potion : custom.getPotions())
      {
         if (potion != null && potion.getType() != Material.AIR)
         {
            loc.getWorld().dropItem(loc, potion);
         }
      }
   }

   private CustomBrewingStand getBrewingInventory(BrewingStand stand)
   {
      if (ActiveBrew.isActive(stand.getLocation()))
      {
         return ActiveBrew.get(stand.getLocation()).getStand();
      }
      return new CustomBrewingStand(stand);
   }
}
