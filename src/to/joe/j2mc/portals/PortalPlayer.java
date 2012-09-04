package to.joe.j2mc.portals;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalPlayer {
    private boolean inPortal;
    private Location lastNonPortal;
    private J2MC_Portals portals;
    private final Player player;

    public PortalPlayer(Player player, boolean inPortal) {
        this.player = player;
        this.inPortal = inPortal;
        if (!inPortal) {
            this.lastNonPortal = player.getLocation();
        }
    }

    public void check() {
        final PortalArea portal = this.portals.getPortalForPlayer(this.player);
        if ((portal != null) && !this.inPortal) {
            this.player.teleport(this.lastNonPortal);
            player.sendPluginMessage(this.portals, "RubberBand", portal.getDestination().getBytes());
        }
        if ((portal == null) && this.inPortal) {
            this.inPortal = false;
            this.lastNonPortal = this.player.getLocation();
        }
    }
}
