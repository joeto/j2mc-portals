package to.joe.j2mc.portals;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class J2MC_Portals extends JavaPlugin implements Listener {

    private final HashSet<PortalArea> portalAreas = new HashSet<PortalArea>();
    private final HashSet<PortalPlayer> players = new HashSet<PortalPlayer>();

    private class PortalCheck implements Runnable {
        @Override
        public void run() {
            Iterator<PortalPlayer> iterator = players.iterator();
            while (iterator.hasNext()) {
                PortalPlayer player = iterator.next();
                if (player.check()) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PortalCheck(), 4, 4);
        for (Player player : this.getServer().getOnlinePlayers()) {
            this.addPlayer(player);
        }
        this.loadPortalAreas();
    }

    public void loadPortalAreas() {
        this.reloadConfig();
        for (String area : this.getConfig().getKeys(false)) {
            String path = /*"portals." + */area;
            int baseX = this.getConfig().getInt(path + ".x", 0);
            int baseY = this.getConfig().getInt(path + ".y", 0);
            int baseZ = this.getConfig().getInt(path + ".z", 0);
            boolean xdim = this.getConfig().getBoolean(path + ".horizontalByX", false);
            String worldName = this.getConfig().getString("world","world");
            World world = this.getServer().getWorld(worldName);
            if(world == null) {
                world = this.getServer().getWorlds().get(0);
            }
            HashSet<Location> locations = new HashSet<Location>();
            List<String> shape = this.getConfig().getStringList(path + ".shape");
            //int height = shape.size();
            int curX = baseX;
            int curY = baseY + shape.size();
            int curZ = baseZ;
            System.out.println("Shape size: "+shape.size());
            for (String line : shape) {
                curY--;
                if(xdim) {
                    curX = baseX;
                } else {
                    curZ = baseZ;
                }
                System.out.println("New line ["+line+"] start: "+curX+","+curY+","+curZ);
                for(char c:line.toCharArray()){
                    if(xdim) {
                        curX++;
                    } else {
                        curZ++;
                    }
                    System.out.println("Coord "+curX+","+curY+","+curZ+" "+c);
                    if(c == 'O'){
                        Location loc = new Location(world, curX, curY, curZ);
                        System.out.println("Adding "+loc);
                        locations.add(loc);
                        loc.getBlock().setTypeId(17);
                    }
                }
            }

            String perm = this.getConfig().getString(path + ".permission");
            if (!perm.equals("j2mc.portals.everyone")) {
                this.getServer().getPluginManager().addPermission(new Permission("perm", PermissionDefault.OP));
            }

            this.portalAreas.add(new PortalArea(locations, area, perm));
        }
    }

    public PortalArea getPortalForPlayer(Player player) {
        for (PortalArea area : this.portalAreas) {
            if (area.isPlayerInPortal(player)) {
                return area;
            }
        }
        return null;
    }

    private void addPlayer(Player player) {
        this.players.add(new PortalPlayer(player, this.getPortalForPlayer(player) != null, this));
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        this.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        Iterator<PortalPlayer> iterator = players.iterator();
        while (iterator.hasNext()) {
            PortalPlayer player = iterator.next();
            if (player.getName().equals(event.getPlayer().getName())) {
                iterator.remove();
                return;
            }
        }
    }
}
