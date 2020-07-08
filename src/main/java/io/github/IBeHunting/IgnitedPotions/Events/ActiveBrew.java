package io.github.IBeHunting.IgnitedPotions.Events;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomBrewingStand;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import io.github.IBeHunting.IgnitedPotions.PotionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ActiveBrew
{
   private static Map<Location, ActiveBrew> BREWS = new HashMap<>();

   private CustomBrewingStand stand;
   private Player player;
   private Location loc;
   private ItemStack ingredient;
   private BukkitTask task;
   private int time, remaining;

   public ActiveBrew(CustomBrewingStand stand, Player player)
   {
      this.stand = stand;
      this.loc = stand.getStand().getLocation();
      this.player = player;
      this.ingredient = stand.getIngredient();
      this.time = this.remaining = Config.getInstance().getBrewingTicks();
   }

   public static boolean isActive(Location loc)
   {
      return BREWS.containsKey(loc);
   }

   public static ActiveBrew get(Location loc)
   {
      return BREWS.get(loc);
   }

   public double getProgress()
   {
      return 1 - (double) remaining / time;
   }

   public CustomBrewingStand getStand()
   {
      return stand;
   }

   public void start()
   {
      if (BREWS.containsKey(loc))
      {
         BREWS.get(loc).cancel();
      }
      BREWS.put(loc, this);
      this.task = Bukkit.getScheduler().runTaskTimer(PotionsPlugin.getInstance(), () ->
      {
         if (!isValidBrew())
         {
            cancel();
            return;
         }
         stand.setProgress(getProgress());
         remaining--;
         if (remaining <= 1)
         {
            finish();
         }
      }, 1, 1);
   }

   private void finish()
   {
      int slot = 0;
      BREWS.remove(loc);
      this.stand.setProgress(-1);
      this.task.cancel();
      CustomBrewEvent event = new CustomBrewEvent(player, stand);
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled())
      {
         decrementIngredient();
         for (CustomPotion potion : event.getResults())
         {
            if (potion != null)
            {
               stand.setPotion(slot, potion.getItem());
            }
            slot++;
         }
      }

   }

   private void decrementIngredient()
   {
      int amount;
      ItemStack ingredient = stand.getIngredient();
      if (ingredient == null)
      {
         return;
      }
      amount = ingredient.getAmount();
      if (amount <= 1)
      {
         stand.setIngredient(null);
      }
      else
      {
         ingredient.setAmount(amount - 1);
         stand.setIngredient(ingredient);
      }
   }

   public void cancel()
   {
      BREWS.remove(loc);
      this.stand.setProgress(-1);
      this.task.cancel();
   }

   private boolean isValidBrew()
   {
      CustomPotion cp;
      if( loc.getBlock() != null
              && (loc.getBlock().getState() instanceof BrewingStand))
      {
         if (!stand.hasIngredient() || !stand.getIngredient().isSimilar(ingredient))
         {
            return false;
         }
         for (ItemStack potion : stand.getPotions())
         {
            cp = CustomPotion.fromItem(potion);
            if (cp != null && cp.canBeBrewed(ingredient))
            {
               return true;
            }
         }
      }
      return false;
   }
}
