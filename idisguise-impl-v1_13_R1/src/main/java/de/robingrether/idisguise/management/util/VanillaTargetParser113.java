package de.robingrether.idisguise.management.util;

import java.util.Collection;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.v1_13_R1.ArgumentParserSelector;
import net.minecraft.server.v1_13_R1.CommandListenerWrapper;
import net.minecraft.server.v1_13_R1.DedicatedServer;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.MinecraftServer;

public class VanillaTargetParser113 extends VanillaTargetParser {
	
	public Collection<? extends Entity> parseTargets(String argument, CommandSender sender) throws CommandSyntaxException {
		StringReader reader = new StringReader(argument);
		reader.setCursor(0);
		return new ArgumentParserSelector(reader).s().b(getListener(sender));
	}
	
	private static CommandListenerWrapper getListener(CommandSender sender) {
        if(sender instanceof Player) {
            return ((CraftPlayer)sender).getHandle().getCommandListener();
        }
        if(sender instanceof BlockCommandSender) {
            return ((CraftBlockCommandSender)sender).getWrapper();
        }
        if(sender instanceof CommandMinecart) {
            return ((CraftMinecartCommand)sender).getHandle().getCommandBlock().getWrapper();
        }
        if(sender instanceof RemoteConsoleCommandSender) {
            return ((DedicatedServer)MinecraftServer.getServer()).remoteControlCommandListener.f();
        }
        if(sender instanceof ConsoleCommandSender) {
            return ((CraftServer)sender.getServer()).getServer().getServerCommandListener();
        }
        if(sender instanceof ProxiedCommandSender) {
            return ((ProxiedNativeCommandSender)sender).getHandle();
        }
        throw new IllegalArgumentException("Cannot make " + (Object)sender + " a vanilla command listener");
    }
	
}