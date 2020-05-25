package io.github.IBeHunting.IgnitedPotions.Listeners;

import io.github.IBeHunting.IgnitedPotions.Config.Config;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.Brewing;
import io.github.IBeHunting.IgnitedPotions.CustomPotions.CustomPotion;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PotionInventoryClick implements Listener
{
   private static final int INGREDIENT_SLOT = 3;
   @EventHandler
   public void onClick(InventoryClickEvent event) {
      BrewerInventory inv;
      Player player;

      /* Player must be interacting with the ingredient slot of a brewing stand */
      if (!(event.getWhoClicked() instanceof Player
              && event.getInventory() instanceof BrewerInventory
              && event.getClickedInventory() != null))
      {
         return;
      }
      player = (Player) event.getWhoClicked();
      inv = (BrewerInventory) event.getInventory();

      if (preformClick(event))
      {
         event.setCancelled(true);
         if (brewable(inv) && checkPermission(player, inv.getIngredient()) && !isDisabled(inv.getIngredient()))
         {
            new ActiveBrew(inv.getHolder(), (Player)event.getWhoClicked()).start();
         }
      }
   }

   private boolean checkPermission(Player player, ItemStack ingredient)
   {
      PotionEffectType type = Config.getInstance().getResultingPotion(ingredient);
      if (type == null)
      {
         return true;
      }
      switch(type.getId())
      {
         case 3: return player.hasPermission("potions.brew.haste");
         case 4: return player.hasPermission("potions.brew.mining_fatigue");
         case 9: return player.hasPermission("potions.brew.nausea");
         case 11: return player.hasPermission("potions.brew.resistance");
         case 15: return player.hasPermission("potions.brew.blindness");
         case 17: return player.hasPermission("potions.brew.hunger");
         case 20: return player.hasPermission("potions.brew.wither");
         case 21: return player.hasPermission("potions.brew.health_boost");
         case 22: return player.hasPermission("potions.brew.absorption");
         case 23: return player.hasPermission("potions.brew.saturation");
         default: return true;
      }
   }

   private boolean isDisabled(ItemStack item)
   {
      PotionEffectType effect = Config.getInstance().getResultingPotion(item);
      if (effect == null)
      {
         return false;
      }
      return Config.getInstance().isDisabled(effect);
   }


   /*
    * Forces non-vanilla ingredients into the brewing stand
    * Returns true if a new item was added to the ingredient slot
    */
   private boolean preformClick(InventoryClickEvent event)
   {
      BrewerInventory inv = (BrewerInventory) event.getInventory();
      ItemStack cursor = event.getCursor();
      if (event.getClickedInventory() == inv
              && event.getSlot() == INGREDIENT_SLOT
              && canStartCustomBrew(cursor))
      {
         switch(event.getClick())
         {
            case LEFT: return leftClick(inv, event);
            case RIGHT: return rightClick(inv, event);
         }
      }
      if (event.getClickedInventory() != event.getInventory()
              && isEmpty(inv.getIngredient())
              && event.getClick().isShiftClick()
              && canStartCustomBrew(event.getCurrentItem()))
      {
         inv.setIngredient(event.getCurrentItem());
         event.setCurrentItem(null);
         return true;
      }
      return false;
   }

   private boolean leftClick(BrewerInventory inv, InventoryClickEvent event)
   {
      ItemStack original = inv.getIngredient();
      ItemStack cursor = event.getCursor();
      int transfer;

      if (original == null)
      {
         inv.setIngredient(cursor);
         event.setCursor(null);
         return true;
      }
      if (cursor.isSimilar(original))
      {
         event.setCancelled(true);
         transfer = Math.min(Math.max(0, original.getMaxStackSize() - original.getAmount()), cursor.getAmount());
         increment(original, transfer);
         increment(cursor, -transfer);

         return false;
      }
      event.setCursor(original);
      inv.setIngredient(cursor);
      return true;
   }

   private boolean rightClick(BrewerInventory inv, InventoryClickEvent event)
   {
      ItemStack original = inv.getIngredient();
      ItemStack cursor = event.getCursor();

      if (isEmpty(original))
      {
         original = new ItemStack(cursor.getType(), 1, cursor.getDurability());
         original.setItemMeta(cursor.getItemMeta());
         inv.setIngredient(original);
         increment(cursor, -1);
         return true;
      }
      if (original.isSimilar(cursor))
      {
         event.setCancelled(true);
         increment(original, 1);
         increment(cursor, -1);
         return false;
      }
      event.setCursor(original);
      inv.setIngredient(cursor);
      return true;
   }

   private boolean brewable(BrewerInventory inv)
   {
      CustomPotion pot;
      for (int i = 0; i < 3; i++)
      {
         pot = CustomPotion.fromItem(inv.getContents()[i]);
         if (pot != null && pot.canBeBrewed(inv.getIngredient()))
         {
            return true;
         }
      }
      return false;
   }

   private boolean isEmpty (ItemStack item)
   {
      return item == null || item.getType() == Material.AIR;
   }

   private boolean canStartCustomBrew(ItemStack item)
   {
      Brewing i = Brewing.getInstance();
      return i.isIngredient(item) || i.isModifier(item);
   }

   private void increment (ItemStack item, int amount)
   {
      int result = item.getAmount() + amount;

      if (result <= 0)
      {
         item.setAmount(1);
         item.setType(Material.AIR);
      }

      result = Math.min(result, item.getMaxStackSize());
      item.setAmount(result);
   }
}
