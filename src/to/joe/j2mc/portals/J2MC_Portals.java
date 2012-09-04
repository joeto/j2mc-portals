package to.joe.j2mc.portals;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class J2MC_Portals extends JavaPlugin implements Listener {

    public final HashSet<PortalArea> portalAreas = new HashSet<PortalArea>();

    @Override
    public void onEnable() {
        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }

        this.getServer().getPluginManager().registerEvents(this, this);
        this.loadPortalAreas();
    }

    public void loadPortalAreas() {
        this.reloadConfig();
        for (String area : this.getConfig().getStringList("portals")) {
            String path = /*"portals." + */area;
            int x = this.getConfig().getInt(path + ".x");
            int y = this.getConfig().getInt(path + ".y");
            int z = this.getConfig().getInt(path + ".z");
            Location upperLeft = new Location(this.getServer().getWorlds().get(0), x, y, z);

            HashSet<Location> locations = new HashSet<Location>();
            List<String> shape = this.getConfig().getStringList(path + ".shape");
            //int height = shape.size();
            int width = shape.get(0).length();
            int curZ = z;
            for (String line : shape) {
                if (line.length() != width) {
                    this.getLogger().info("YOUR CONFIG IS BAD AND YOU SHOULD FEEL BAD. Shutting down plugin.");
                    this.getPluginLoader().disablePlugin(this);
                    return;
                }
                for (int i = 0; i < width; i++) {
                    if (line.charAt(i) == 'X') {
                        Location loc = new Location(this.getServer().getWorlds().get(0), x + i, y, curZ);
                        locations.add(loc);
                        loc.getBlock().setType(Material.BEDROCK); //debug
                        this.getLogger().info("Placed bedrock at " + loc.getX() + " " + loc.getBlockY() + " " + loc.getZ());
                    }
                }
                curZ++;
            }

            String destination = area;
            String perm = this.getConfig().getString(path + ".permission");
            if (perm.equals("j2mc.portals.everyone")) {
                this.getServer().getPluginManager().addPermission(new Permission("perm", PermissionDefault.OP));
            }

            this.portalAreas.add(new PortalArea(locations, destination, perm, upperLeft));
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

}
