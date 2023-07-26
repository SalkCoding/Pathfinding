package com.salkcoding.pathfinding

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

lateinit var pathfinding: Pathfinding

class Pathfinding : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        pathfinding = this
        logger.info("A* algorithm test plugin enabled.")
    }

    override fun onDisable() {
        logger.info("A* algorithm test plugin disabled.")
    }

    private var from: Location? = null
    private var to: Location? = null

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.player.inventory.itemInMainHand.type != Material.STICK) return

        val action = e.action
        if (action.isLeftClick) {
            from = e.clickedBlock?.location
            e.player.sendMessage("Set start point")
            e.isCancelled = true
        } else if (action.isRightClick) {
            to = e.clickedBlock?.location
            e.player.sendMessage("Set destination point")
            e.isCancelled = true
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label.lowercase() == "astar") {
            if (from == null || to == null) {
                sender.sendMessage("Set start point and destination point.")
                return true
            }
            val path = AStar.pathFinding(from!!.clone(), to!!.clone(), args.isEmpty())
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
        }
        return true
    }
}
