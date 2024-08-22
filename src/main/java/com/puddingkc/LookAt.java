package com.puddingkc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class LookAt extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("lookat")).setExecutor(this);
        getLogger().info("插件加载成功，作者QQ: 3116078709");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("lookat.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有使用该命令的权限");
            return false;
        }

        if (args.length == 4) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                handleCoordinates(sender, target, args);
            } else {
                sender.sendMessage(ChatColor.RED + "指定的玩家不在线或不存在");
            }
            return true;
        }

        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null && target.isOnline()) {
                handleTargetPlayer(sender, target, args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "指定的玩家不在线或不存在");
            }
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "正确指令: /lookat <玩家> <坐标/目标玩家>");
        sender.sendMessage(ChatColor.GRAY + "示例1: /lookat Notch 10 25 10");
        sender.sendMessage(ChatColor.GRAY + "示例2: /lookat Notch Jeb_");
        return false;
    }

    private void handleTargetPlayer(CommandSender sender, Player player, String targetPlayerName) {
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

        if (targetPlayer != null && targetPlayer.isOnline()) {
            Location targetLocation = targetPlayer.getLocation();
            lookAt(player, targetLocation);

            sender.sendMessage(ChatColor.GREEN + "玩家 " + player.getName() + " 正在看向目标 " + targetPlayerName);
        } else {
            sender.sendMessage(ChatColor.RED + "指定的目标不在线或不存在");
        }
    }

    private void handleCoordinates(CommandSender sender ,Player player, String[] args) {
        try {
            double targetX = Double.parseDouble(args[1]);
            double targetY = Double.parseDouble(args[2]);
            double targetZ = Double.parseDouble(args[3]);

            Location targetLocation = new Location(player.getWorld(), targetX, targetY, targetZ);
            lookAt(player, targetLocation);

            sender.sendMessage(ChatColor.GREEN + "玩家 " + player.getName() + " 正在看向坐标 " + targetX + ", " + targetY + ", " + targetZ);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "坐标参数错误，请输入有效的数字");
        }
    }

    private void lookAt(Player player, Location targetLocation) {
        Location playerLocation = player.getLocation();

        float[] yawPitch = calculateYawPitch(playerLocation, targetLocation);

        playerLocation.setYaw(yawPitch[0]);
        playerLocation.setPitch(yawPitch[1]);
        player.teleport(playerLocation);
    }

    private float[] calculateYawPitch(Location playerLoc, Location targetLoc) {
        double dx = targetLoc.getX() - playerLoc.getX();
        double dy = targetLoc.getY() - playerLoc.getY();
        double dz = targetLoc.getZ() - playerLoc.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distanceXZ));

        return new float[]{yaw, pitch};
    }
}