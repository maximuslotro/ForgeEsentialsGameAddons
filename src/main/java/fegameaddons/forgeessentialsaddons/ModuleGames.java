package fegameaddons.forgeessentialsaddons;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import fegameaddons.FEGameAddons;
import fegameaddons.forgeessentialsaddons.dungon.DungeonGame;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

@FEModule(name = "GameAddons", parentMod = FEGameAddons.class, defaultModule = true, version = ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleGames extends ConfigLoaderBase
{
    private static ForgeConfigSpec GAME_CONFIG;
	private static final ConfigData data = new ConfigData("Games", GAME_CONFIG, new ForgeConfigSpec.Builder());

    public static final String CONFIG_CATEGORY = "Games";

    public static boolean testValue = false;
    private static Map<ServerPlayerEntity, DungeonGame> games;

    @FEModule.ModuleDir
    public static File moduleDir;

    @Preconditions
    public static boolean preInit()
    {
        if (FMLEnvironment.dist.isClient())
            return false;
        return true;
    }

    public static DungeonGame getPlayerGame(ServerPlayerEntity player) {
    	return games.get(player);
    }
    public static void setPlayerGame(ServerPlayerEntity player, DungeonGame game) {
    	games.put(player, game);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandGame(true), event.getDispatcher());
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerAboutToStartEvent e) {
    	games = new HashMap<>();
    }
    /*
    @SubscribeEvent
    public void serverStopping(FEModuleServerStartingEvent e) {}
    @SubscribeEvent
    public void serverStopping(FEModuleServerStartedEvent e) {}
    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e) {}
    */
    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppedEvent e) {
    	games.clear();
    }

    static ForgeConfigSpec.BooleanValue CFtestValue;

	@Override
	public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("GameModule configuration").push(CONFIG_CATEGORY);
        CFtestValue = BUILDER.define("testValue", false);
        BUILDER.pop();
    }

	@Override
	public void bakeConfig(boolean reload)
    {
        testValue = CFtestValue.get();
    }


	@Override
	public ConfigData returnData() {
		return data;
	}
}
