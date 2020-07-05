package io.github.IBeHunting.IgnitedPotions.CustomPotions;

import io.github.IBeHunting.IgnitedPotions.Config.MessageConfig;
import io.github.IBeHunting.IgnitedPotions.Events.ActiveBrew;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class CustomBrewingStand implements InventoryHolder
{
   private Inventory inv;
   private BrewingStand stand;

   private static final ItemStack BACKGROUND = makePane((short) 1);
   private static final ItemStack PROGRESS_INACTIVE = makePane((short) 14);
   private static final ItemStack PROGRESS_EMPTY = makePane((short) 4);
   private static final ItemStack PROGRESS_FULL = makePane((short) 13);
   private static final int SIZE = 54;
   private static final int INGREDIENT_SLOT = 13;
   private static final int[] POTION_SLOTS = {29, 31, 33};
   private static final int[] PROGRESS_BAR = {0, 9, 18, 27, 36, 45, 46, 47, 48, 49, 50, 51, 52, 53, 44, 35, 26, 17, 8};

   public CustomBrewingStand(BrewingStand orig)
   {
      this.stand = orig;
      this.inv = Bukkit.createInventory(this, SIZE, MessageConfig.getInstance().getStandName());
      setupInventory();
   }

   private void setupInventory()
   {
      ItemStack[] contents = new ItemStack[SIZE];
      int vanilla_slot = 0;
      Arrays.fill(contents, BACKGROUND);
      contents[INGREDIENT_SLOT] = stand.getInventory().getIngredient();
      for (int potion_slot : POTION_SLOTS)
      {
         contents[potion_slot] = stand.getInventory().getItem(vanilla_slot++);
      }
      inv.setContents(contents);
      setProgress(-1);
   }

   public Inventory getInventory()
   {
      return inv;
   }

   public BrewingStand getStand()
   {
      return stand;
   }

   public boolean hasIngredient()
   {
      ItemStack ing = getIngredient();
      return ing != null && ing.getType() != Material.AIR;
   }

   public ItemStack getIngredient()
   {
      return inv.getItem(INGREDIENT_SLOT);
   }

   public void setIngredient(ItemStack ingredient)
   {
      this.inv.setItem(INGREDIENT_SLOT, ingredient);
      this.stand.getInventory().setIngredient(ingredient);
   }

   public void setPotion(int slot, ItemStack potion)
   {
      if (slot >= POTION_SLOTS.length)
      {
         throw new IllegalArgumentException("Invalid potion index");
      }
      inv.setItem(POTION_SLOTS[slot], potion);
      stand.getInventory().setItem(slot, potion);
   }

   public ItemStack[] getPotions()
   {
      ItemStack potions[] = new ItemStack[POTION_SLOTS.length];
      for (int i = 0; i < POTION_SLOTS.length; i++)
      {
         potions[i] = inv.getItem(POTION_SLOTS[i]);
      }
      return potions;
   }

   /*
    * Sets the progress bar to represent the time remaining:
    *    0 = No progress ; 1 = Full progress ; < 0 = Inactive
    */
   public void setProgress (double percent)
   {
      int numb_full;
      if (percent < 0)
      {
         for (int slot : PROGRESS_BAR)
         {
            inv.setItem(slot, PROGRESS_INACTIVE);
         }
         return;
      }
      if (percent > 1)
      {
         percent = 1;
      }
      numb_full = (int) (PROGRESS_BAR.length * percent);
      for (int i = 0; i < numb_full; i++)
      {
         inv.setItem(PROGRESS_BAR[i], PROGRESS_FULL);
      }
      for (int i = numb_full; i < PROGRESS_BAR.length; i++)
      {
         inv.setItem(PROGRESS_BAR[i], PROGRESS_EMPTY);
      }
   }

   public void open(Player player)
   {
      player.openInventory(inv);
   }

   public static boolean isIngredientSlot(int slot)
   {
      return slot == INGREDIENT_SLOT;
   }

   public static boolean isPotionSlot(int slot)
   {
      for (int potion_slot : CustomBrewingStand.POTION_SLOTS)
      {
         if (slot == potion_slot)
         {
            return true;
         }
      }
      return false;
   }

   private static ItemStack makePane (short data)
   {
      ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(" ");
      meta.setLore(Collections.emptyList());
      item.setItemMeta(meta);
      return item;
   }
}
