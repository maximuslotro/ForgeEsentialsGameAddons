package fegameaddons.forgeessentialsaddons;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import fegameaddons.forgeessentialsaddons.dungon.CONSTANTS;
import fegameaddons.forgeessentialsaddons.dungon.DungeonGame;

public class CommandGame extends ForgeEssentialsCommandBuilder implements CONSTANTS
{
    public CommandGame(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "game";
    }

    public LiteralArgumentBuilder<CommandSource> setExecution()
	{
        return baseBuilder
        		.then(Commands.literal("action")
        				.then(Commands.literal("up").executes(CommandContext -> execute(CommandContext, "w")))
        				.then(Commands.literal("down").executes(CommandContext -> execute(CommandContext, "s")))
        				.then(Commands.literal("left").executes(CommandContext -> execute(CommandContext, "a")))
        				.then(Commands.literal("right").executes(CommandContext -> execute(CommandContext, "d")))
        				.then(Commands.literal("quit").executes(CommandContext -> execute(CommandContext, "q")))
        				.then(Commands.literal("stayStill").executes(CommandContext -> execute(CommandContext, "e")))
        				)
        		.then(Commands.literal("load")
        				.then(Commands.argument("mapName", StringArgumentType.word())
        						.executes(CommandContext -> execute(CommandContext, "load"))))
        		.then(Commands.literal("redraw")
        				.executes(CommandContext -> execute(CommandContext, "redraw")))
        		.then(Commands.literal("printInstructions")
        				.executes(CommandContext -> execute(CommandContext, "printInstructions")))
        		.executes(CommandContext -> execute(CommandContext, "help")
                        );
	}

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
    	ServerPlayerEntity player = (ServerPlayerEntity)ctx.getSource().getEntity();
    	if(params.equals("help")) {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /game load <mapName> - load a new game");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /game redraw - print the current map to chat, like it it gets buried");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /game action up|down|left|right - move your character");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /game action quit - exit the game");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /game action stay - skip a turn");
            return Command.SINGLE_SUCCESS;
    	}
    	if(params.equals("redraw")) {
    		if(ModuleGames.getPlayerGame(player)==null) {
    			ChatOutputHandler.chatWarning(ctx.getSource(), "No game loaded, use: /game load <mapName>");
    			ChatOutputHandler.chatWarning(ctx.getSource(), "to load a game");
    		}else {
    			ModuleGames.getPlayerGame(player).outputMap(ctx.getSource());
    		}
    		return Command.SINGLE_SUCCESS;
    	}
    	if(params.equals("printInstructions")) {
    		if(ModuleGames.getPlayerGame(player)==null) {
    			ChatOutputHandler.chatWarning(ctx.getSource(), "No game loaded, use: /game load <mapName>");
    			ChatOutputHandler.chatWarning(ctx.getSource(), "to load a game");
    		}else {
    			ModuleGames.getPlayerGame(player).printInstructions(ctx.getSource());
    		}
    		return Command.SINGLE_SUCCESS;
    	}
    	if(params.equals("load")) {
    		String mapName = StringArgumentType.getString(ctx, "mapName");
    		ModuleGames.setPlayerGame(player, new DungeonGame(mapName, ctx.getSource()));
    		if(ModuleGames.getPlayerGame(player).invalid) {
    			ModuleGames.setPlayerGame(player, null);
    			return Command.SINGLE_SUCCESS;
    		}
    		ModuleGames.getPlayerGame(player).printInstructions(ctx.getSource());
    		ModuleGames.getPlayerGame(player).outputMap(ctx.getSource());
    		return Command.SINGLE_SUCCESS;
    	}
    	if(params.equals("w")||params.equals("s")||params.equals("a")||params.equals("d")||params.equals("e")||params.equals("q")) {
    		if(ModuleGames.getPlayerGame(player)==null) {
    			ChatOutputHandler.chatWarning(ctx.getSource(), "No game loaded, use: /game load <mapName>");
    			ChatOutputHandler.chatWarning(ctx.getSource(), "to load a game");
    		}else {
    			if(params.equals("q")) {
    				ChatOutputHandler.chatConfirmation(ctx.getSource(), "Thank you for playing!");
    				ModuleGames.setPlayerGame(player, null);
    				return Command.SINGLE_SUCCESS;
    			}
    			ChatOutputHandler.chatConfirmation(ctx.getSource(), "Option"+params);
    			ModuleGames.getPlayerGame(player).logic(params, ctx.getSource());
				if(ModuleGames.getPlayerGame(player).invalid) {
	    			ModuleGames.setPlayerGame(player, null);
	    			return Command.SINGLE_SUCCESS;
	    		}
    		}
    		return Command.SINGLE_SUCCESS;
    	}
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.game";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

}
