package net.tomengmaster.islesmod;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.RichPresenceButton;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;
import java.util.List;

public class IslesMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("islesmod");
	private static int tick = 1;
	public static IPCClient ipcClient = new IPCClient(903236149416443946L);
	private static long lastTimestamp;
	private static int rpCount = 0;
	private static String previousIP = "";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("IslesMod successfully loaded!");

		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> tick());
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			tick = 1;
			if(Utils.onIsles() && previousIP.equals("")) {
				previousIP = client.getCurrentServerEntry().address;
				try {
					ipcClient.connect();
				} catch (NoDiscordClientException e) {
					e.printStackTrace();
				}
				lastTimestamp = OffsetDateTime.now().toEpochSecond();
				LOGGER.info("Server connected!");
			} else if(Utils.onIsles() && previousIP.equals(client.getCurrentServerEntry().address)){
				LOGGER.info("Server switched!");
			}
			if(Utils.onIsles()) {
				updateRPC("", "");
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if(Utils.onIsles()) {
				previousIP = "";
				ipcClient.close();
				LOGGER.info("Server disconnected!");
			}
		});

		/*ClientCommandManager.DISPATCHER.register(
				ClientCommandManager.literal("islesdev").executes(context -> {
					client.sendRichPresence(null);
					return 1;
		}));*/
	}
	public static void updateRPC(String firstline, String secondline) {
				RichPresence.Builder builder = new RichPresence.Builder();
				RichPresenceButton[] button = new RichPresenceButton[0];
				builder.setDetails(firstline)
						.setState(secondline)
						.setButtons(button)
						.setLargeImage("logo", "Skyblock Isles - play.skyblockisles.com - icon by the Skyblock Isles team");
				builder.setStartTimestamp(lastTimestamp);
				ipcClient.sendRichPresence(builder.build());
				LOGGER.info("Discord RPC updated!");
	}

	public static void tick() {
		tick++;
		if(tick > 20) {
			tick = 1;
		}
		if(tick==20) {
			rpCount++;
			if(rpCount > 5) {
				rpCount = 0;
			}
			if(rpCount == 5) {
				if(Utils.onIsles()) {
					List<String> lines = Utils.getScoreboard();
					if (lines.get(1).startsWith("Rank: ")) {
						updateRPC(lines.get(2), lines.get(3));
					} else {
						updateRPC(lines.get(1).replace(" ⦿", ": "), lines.get(2));
					}
				}
			}
		}
	}
}
