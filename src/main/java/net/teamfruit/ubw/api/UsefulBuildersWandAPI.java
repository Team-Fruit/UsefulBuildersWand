package net.teamfruit.ubw.api;

/**
 * API
 * <p>
 * some features may not be initialized before this plugin <code>onEnable</code> phase
 * <p>
 * please don't use these features in <code>onEnable</code> phase
 *
 * @author TeamFruit
 */
public class UsefulBuildersWandAPI {
	private final UBWBridge bridge;

	public UsefulBuildersWandAPI(final UBWBridge bridge) {
		this.bridge = bridge;
	}

	public interface UBWBridge {

	}
}
