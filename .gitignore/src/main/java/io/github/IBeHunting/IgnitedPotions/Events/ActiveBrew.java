package io.github.IBeHunting.IgnitedPotions.Events;

import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ActiveBrew
{
   private static Map<Location, ActiveBrew> BREWS = new HashMap<>();
   private static final int BREW_TICKS = 400;

   private BrewingStand stand;
   private Player player;
   private Location loc;

   private ItemStack ingredient;
   private BukkitTask task;
   private int time;

   public ActiveBrew(BrewingStand stand, Player player)
   {
      this.stand = stand;
      this.loc = stand.getLocation();
      this.player = player;
   }

   public static boolean isActive(Location loc)
   {
      return BREWS.containsKey(loc);
   }

   public static ActiveBrew get(Location loc)
   {
      return BREWS.get(loc);
   }

   public void start()
   {
      if (BREWS.containsKey(loc))
      {
         BREWS.get(loc).cancel();
      }
      this.time = BREW_TICKS;
      BREWS.put(loc, this);
      this.task = Bukkit.getScheduler().runTaskTimer(PotionsPlugin.getInstance(), () -> {
         if (time == BREW_TICKS)
         {
            this.ingredient = stand.getInventory().getIngredient();
         }
         if (!isValidBrew())
         {
            cancel();
         }
         stand.setBrewingTime(time);
         time--;
         if (time <= 1)
         {
            finish();
         }
      }, 1, 1);
   }

   private void finish()
   {
      CustomPotion[] results;
      BREWS.remove(loc);
      this.stand.setBrewingTime(-1);
      this.task.cancel();
      CustomBrewEvent event = new CustomBrewEvent(player, stand.getInventory().getContents());
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled())
      {
         results = Brewing.getInstance().apply(event.getPotions(), event.getIngredient());
         decrementIngredient();
         stand.getInventory().setItem(0, results[0].getItem());
         stand.getInventory().setItem(1, results[1].getItem());
         stand.getInventory().setItem(2, results[2].getItem());
      }

   }

   private void decrementIngredient()
   {
      int amount;
      if (stand.getInventory().getIngredient() == null)
      {
         return;
      }
      amount = stand.getInventory().getIngredient().getAmount();
      if (amount <= 1)
      {
         stand.getInventory().setIngredient(null);
      }
      else
      {
         stand.getInventory().getIngredient().setAmount(amount - 1);
      }
   }

   public void cancel()
   {
      BREWS.remove(loc);
      this.stand.setBrewingTime(-1);
      this.task.cancel();
   }

   private boolean isValidBrew()
   {
      BrewerInventory inv;
      if( loc.getBlock() != null
              && (loc.getBlock().getState() instanceof BrewingStand))
      {
         inv = ((BrewingStand) loc.getBlock().getState()).getInventory();
         if (inv.getIngredient() == null || !inv.getIngredient().isSimilar(ingredient))
         {
            return false;
         }
         for (int i = 0; i < 3; i++)
         {
            if (inv.getItem(i) != null
                    && CustomPotion.fromItem(inv.getItem(i)).canBeBrewed(ingredient))
            {
               return true;
            }
         }
      }
      return false;
   }
}
