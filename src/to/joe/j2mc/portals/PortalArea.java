package to.joe.j2mc.portals;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalArea {

    private final HashSet<Location> locations;
    private final String destination;
    private final String permission;

    public PortalArea(HashSet<Location> locations, String destination, String permission) {
        this.locations = locations;
        this.destination = destination;
        this.permission = permission;
    }

    public String getDestination() {
        return this.destination;
    }

    public HashSet<Location> getLocations() {
        return this.locations;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isLocationInPortal(Location loc) {
        return this.locations.contains(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public boolean isPlayerInPortal(Player player) {
        final Location loc = player.getLocation();
        final Location test = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        if (this.locations.contains(test)) {
            return true;
        }
        test.add(0, 1, 0);
        return this.locations.contains(test);
    }

}
