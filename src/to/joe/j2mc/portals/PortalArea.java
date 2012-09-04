package to.joe.j2mc.portals;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalArea {

    private HashSet<Location> locations;
    private String destination;
    private String permission;

    public PortalArea(HashSet<Location> locations, String destination, String permission, Location upperLeft) {
        this.locations = locations;
        this.destination = destination;
        this.permission = permission;
    }

    public HashSet<Location> getLocations() {
        return this.locations;
    }

    public boolean isLocationInPortal(Location loc) {
        return locations.contains(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public boolean isPlayerInPortal(Player player) {
        Location loc = player.getLocation();
        Location test = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        if (locations.contains(test)) {
            return true;
        }
        test.add(0, 1, 0);
        return locations.contains(test);
    }

    public String getDestination() {
        return this.destination;
    }

    public String getPermission() {
        return this.permission;
    }

}
