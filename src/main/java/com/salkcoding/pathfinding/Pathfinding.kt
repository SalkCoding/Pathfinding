package com.salkcoding.pathfinding

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

lateinit var pathfinding: Pathfinding

class Pathfinding : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        pathfinding = this
        logger.info("A* algorithm test plugin enabled.")
    }

    override fun onDisable() {
        task?.cancel()
        logger.info("A* algorithm test plugin disabled.")
    }

    private var to: Location? = null

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.player.inventory.itemInMainHand.type != Material.STICK) return

        val action = e.action
        if (action.isLeftClick) {
            to = e.clickedBlock?.location
            e.player.sendMessage("Set destination point")
            e.isCancelled = true
        }
    }

    private var task: BukkitTask? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label.lowercase() == "astar") {
            if (to == null) {
                sender.sendMessage("Set start point and destination point.")
                return true
            }
            if (task == null) {
                task = Bukkit.getScheduler().runTaskTimer(this, Runnable {
                    val start = System.currentTimeMillis()
                    val path = AStar.pathFinding((sender as Player).location.block.location, to!!.clone())
                    val end = System.currentTimeMillis()
                    println("delta ms: ${end-start}ms")
                    if (path.isEmpty()) sender.sendMessage("There is no way to reach your destination.")
                    else {
                        path.forEach { loc ->
                            loc.world.spawnParticle(
                                Particle.FIREWORKS_SPARK,
                                loc.add(.5, .5, .5),
                                0,
                                .0,
                                .0,
                                .0,
                                .0,
                                null,
                                true
                            )
                        }
                    }
                }, 20, 20)
            } else {
                task?.cancel()
                task = null
            }
        }
        return true
    }
}
