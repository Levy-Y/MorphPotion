package morphpotion.morphpotion;

import me.bumblebeee_.morph.MorphManager;
import me.bumblebeee_.morph.morphs.Morph;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import de.tr7zw.nbtapi.NBTItem;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.util.Objects;

public class MorphPotion extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        // Register command and event listener
        Objects.requireNonNull(this.getCommand("magicpotion")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Brewing a pot of MorphPotion!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MorphPotion is returning to the ether...");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("magicpotion")) {
            Player player = (Player) sender;
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.WATER));
            meta.setDisplayName(ChatColor.RESET + "Morph Potion");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            potion.setItemMeta(meta);
            NBTItem nbti = new NBTItem(potion);
            nbti.setBoolean("MorphPotion", true);
            ItemStack magicPotion = nbti.getItem();
            player.getInventory().addItem(magicPotion);
            player.sendMessage("A Morph Potion appears in your inventory!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        MorphManager morph = me.bumblebeee_.morph.Main.getMorphManager();
        List<String> mobs = Arrays.asList(
                "silverfish", "chicken", "wolf", "squid", "pig",
                "sheep", "cow", "bat", "spider", "goat", "fox", "frog"
        );
        ItemStack item = event.getItem();
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasKey("MorphPotion") && nbti.getBoolean("MorphPotion")) {
            Player player = event.getPlayer();

            if (morph.getUsingMorph(player) != null) {
                event.setCancelled(true);
                player.sendMessage("You are already morphed!");
            } else {
                String randomMob = mobs.get(new Random().nextInt(mobs.size()));
                Morph morphType = morph.getMorphType(randomMob);
                morph.morphPlayer(player, morphType, false, false);

                getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                    morph.unmorphPlayer(player, false, false);
                }, 5 * 60 * 20);
            }
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        MorphManager morph = me.bumblebeee_.morph.Main.getMorphManager();
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            morph.unmorphPlayer(player, false, false);
        }
    }
}
