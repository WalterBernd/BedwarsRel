package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Listener.PlayerListener;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

public class JoinGameCommand extends BaseCommand {

	public JoinGameCommand(Main plugin) {
		super(plugin);
	}

	@Override
	public String getCommand() {
		return "join";
	}

	@Override
	public String getName() {
		return Main._l("commands.join.name");
	}

	@Override
	public String getDescription() {
		return Main._l("commands.join.desc");
	}

	@Override
	public String[] getArguments() {
		return new String[] { "game" };
	}

	@Override
	public boolean execute(CommandSender sender, ArrayList<String> args) {
		Player player = (Player) sender;
		Game game = this.getPlugin().getGameManager().getGame(args.get(0));
		Game gameOfPlayer = Main.getInstance().getGameManager().getGameOfPlayer(player);
		
		//If a player joins the Bedwars game, he should never leave it directly.
		//Only if the lobby is not in the same world!
		if(game != null) {
			if(!game.getLobby().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())){
				if (PlayerListener.playersLeavingOrJoining.containsKey(player.getUniqueId().toString())) {
    				PlayerListener.playersLeavingOrJoining.remove(player.getUniqueId().toString());
    				PlayerListener.playersLeavingOrJoining.put(player.getUniqueId().toString(), true);
    			} else {
    				PlayerListener.playersLeavingOrJoining.put(player.getUniqueId().toString(), true);
    			}
		
				if (!super.hasPermission(sender)) {
					return false;
				}
			}
		}
		
		if(gameOfPlayer != null) {
			if(gameOfPlayer.getState() == GameState.RUNNING) {
				sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
						+ Main._l("errors.notwhileingame")));
				return false;
			}
			
			if(gameOfPlayer.getState() == GameState.WAITING) {
				gameOfPlayer.playerLeave(player, false);
			}
		}

		if (game == null) {
			sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
					+ Main._l("errors.gamenotfound",
							ImmutableMap.of("game", args.get(0).toString()))));
			return false;
		}

		if (game.playerJoins(player)) {
			sender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN
					+ Main._l("success.joined")));
		}
		return true;
	}

	@Override
	public String getPermission() {
		return "base";
	}

}
