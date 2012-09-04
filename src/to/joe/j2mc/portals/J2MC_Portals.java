package to.joe.j2mc.portals;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class J2MC_Portals extends JavaPlugin implements Listener {

    private class PortalCheck implements Runnable {
        @Override
        public void run() {
            final Iterator<PortalPlayer> iterator = J2MC_Portals.this.players.iterator();
            while (iterator.hasNext()) {
                final PortalPlayer player = iterator.next();
                if (player.check()) {
                    iterator.remove();
                    return;
                }
            }
        }
    }
    private final HashSet<PortalArea> portalAreas = new HashSet<PortalArea>();

    private final HashSet<PortalPlayer> players = new HashSet<PortalPlayer>();

    public PortalArea getPortalForPlayer(Player player) {
        for (final PortalArea area : this.portalAreas) {
            if (area.isPlayerInPortal(player)) {
                return area;
            }
        }
        return null;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        this.addPlayer(event.getPlayer());
    }

    public void loadPortalAreas() {
        this.reloadConfig();
        for (final String area : this.getConfig().getKeys(false)) {
            final ConfigurationSection portal = this.getConfig().getConfigurationSection(area);
            final int baseX = portal.getInt("x", 0);
            final int baseY = portal.getInt("y", 0);
            final int baseZ = portal.getInt("z", 0);
            final boolean xdim = portal.getBoolean("horizontalByX", false);
            final String worldName = portal.getString("world", "world");
            World world = this.getServer().getWorld(worldName);
            if (world == null) {
                world = this.getServer().getWorlds().get(0);
            }
            final HashSet<Location> locations = new HashSet<Location>();
            final List<String> shape = Arrays.asList(portal.getString("shape").split("\n"));
            //int height = shape.size();
            int curX = baseX;
            int curY = baseY + shape.size();
            int curZ = baseZ;
            for (final String line : shape) {
                curY--;
                if (xdim) {
                    curX = baseX;
                } else {
                    curZ = baseZ;
                }
                for (final char c : line.toCharArray()) {
                    if (c == 'O') {
                        final Location loc = new Location(world, curX, curY, curZ);
                        locations.add(loc);
                        loc.getBlock().setTypeId(17);
                    }
                    if (xdim) {
                        curX++;
                    } else {
                        curZ++;
                    }
                }
            }

            final String perm = portal.getString("permission");
            if (!perm.equals("j2mc.portals.everyone")) {
                this.getServer().getPluginManager().addPermission(new Permission("perm", PermissionDefault.OP));
            }

            this.portalAreas.add(new PortalArea(locations, area, perm));
        }
    }

    @Override
    public void onEnable() {
        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "RubberBand");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PortalCheck(), 4, 4);
        for (final Player player : this.getServer().getOnlinePlayers()) {
            this.addPlayer(player);
        }
        this.loadPortalAreas();
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        final Iterator<PortalPlayer> iterator = this.players.iterator();
        while (iterator.hasNext()) {
            final PortalPlayer player = iterator.next();
            if (player.getName().equals(event.getPlayer().getName())) {
                iterator.remove();
                return;
            }
        }
    }

    private void addPlayer(Player player) {
        this.players.add(new PortalPlayer(player, this.getPortalForPlayer(player) != null, this));
    }
}
