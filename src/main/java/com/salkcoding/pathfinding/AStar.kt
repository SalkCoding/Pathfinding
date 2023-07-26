package com.salkcoding.pathfinding

import org.bukkit.Location
import java.util.*
import kotlin.math.abs

object AStar {
    private val dx = listOf(1.0, -1.0, 0.0, 0.0, 0.0, 0.0)
    private val dy = listOf(0.0, 0.0, 1.0, -1.0, 0.0, 0.0)
    private val dz = listOf(0.0, 0.0, 0.0, 0.0, 1.0, -1.0)

    private var isRunning = false

    fun pathFinding(start: Location, destination: Location, fly: Boolean): List<Location> {
        val path = mutableListOf<Location>()
        if (isRunning) {
            pathfinding.logger.warning("Already running")
            return path
        }
        isRunning = true
        pathfinding.logger.info("Start path finding start=${start}, destination=${destination}")
        val pq = PriorityQueue<Node>()
        val visited = mutableSetOf<Location>()
        val startNode = Node(start, null, start, destination, fly)
        pq.add(startNode)
        visited.add(start)
        while (pq.isNotEmpty()) {
            val node = pq.poll()
            //Finding
            for (i in 0 until 6) {
                val next = node.current.clone().add(dx[i], dy[i], dz[i])
                //Arrived destination
                if (next == destination) {
                    var nextNode: Node? = Node(next, node, node.current, node.destination, fly)
                    while (nextNode != null) {
                        path.add(nextNode.current)
                        nextNode = nextNode.parent
                    }
                    isRunning = false
                    pathfinding.logger.info("Complete path finding")
                    return path
                }
                if (next in visited || !next.block.isPassable) continue
                val nextNode = Node(next, node, node.current, node.destination, fly)
                //if (node.weight < nextNode.weight) continue -> Working but not properly
                //if (startNode.weight <= nextNode.weight) continue -> Have to test
                pq.add(nextNode)
                visited.add(next)
            }
        }
        isRunning = false
        return path
    }

    data class Node(
        val current: Location,
        val parent: Node?,
        val start: Location,
        val destination: Location,
        val fly: Boolean
    ) : Comparable<Node> {
        override fun compareTo(other: Node): Int = weight - other.weight

        //Heuristic f() = g() + h()
        val weight = g() + h()

        //Start node -> Current node manhattan distance
        private fun g(): Int {
            val y = if (fly) current.blockY else current.world.getHighestBlockAt(current).y
            return abs(current.blockX - start.blockX) + abs(y - start.blockY) + abs(current.blockZ - start.blockZ)
        }


        //Current node -> destination manhattan distance
        private fun h(): Int {
            val y = if (fly) current.blockY else current.world.getHighestBlockAt(current).y
            return abs(current.blockX - destination.blockX) + abs(y - destination.blockY) + abs(current.blockZ - destination.blockZ)
        }
    }

}