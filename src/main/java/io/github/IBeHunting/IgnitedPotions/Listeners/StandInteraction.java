package io.github.IBeHunting.IgnitedPotions.Listeners;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomBrewingStand;
import io.github.IBeHunting.IgnitedPotions.Util.HopperMovementHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StandInteraction implements Listener
{

   private Map<Location, CustomBrewingStand> STANDS = new HashMap<>();

   @EventHandler
   public void onStandOpen(PlayerInteractEvent event)
   {
      BrewingStand stand;
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK
              && !event.getPlayer().isSneaking()
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

   @EventHandler (priority = EventPriority.MONITOR)
   public void onHopperMovement(InventoryMoveItemEvent event)
   {
      CustomBrewingStand cbs;
      ItemStack item = event.getItem();
      if (event.getSource().getHolder() instanceof BrewingStand)
      {
         event.setCancelled(true);
         cbs = getBrewingInventory((BrewingStand) event.getSource().getHolder());
         HopperMovementHandler.getInstance().handleTransferFrom(cbs, item, event.getDestination());
      }
      if (event.getDestination().getHolder() instanceof BrewingStand)
      {
         event.setCancelled(true);
         cbs = getBrewingInventory((BrewingStand) event.getDestination().getHolder());
         HopperMovementHandler.getInstance().handleTransferTo(cbs, item, event.getSource());
      }
   }

   private void removeStand(BrewingStand stand)
   {
      Location loc = stand.getLocation();
      CustomBrewingStand custom = getBrewingInventory(stand);
      List<HumanEntity> viewers;

      loc.getWorld().dropItem(loc, new ItemStack(Material.BREWING_STAND_ITEM));

      stand.getInventory().clear();
      stand.getBlock().setType(Material.AIR);
      STANDS.remove(loc);

      /* Anyone viewing the brewing stand must close the inventory */
      viewers = new ArrayList<>(custom.getInventory().getViewers());
      viewers.forEach(HumanEntity::closeInventory);

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
      CustomBrewingStand cbs;
      if (STANDS.containsKey(stand.getLocation()))
      {
         return STANDS.get(stand.getLocation());
      }
      cbs = new CustomBrewingStand(stand);
      STANDS.put(stand.getLocation(), cbs);
      return cbs;
   }
}
