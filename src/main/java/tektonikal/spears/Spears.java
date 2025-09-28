package tektonikal.spears;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spears implements ModInitializer {
	public static final String MOD_ID = "spears";

	@Override
	public void onInitialize() {
		Items.init();
	}
}