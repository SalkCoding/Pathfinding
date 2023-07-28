package com.salkcoding.pathfinding

import org.bukkit.Location
import java.util.*

object AStar {
    private val dx = listOf(1.0, -1.0, 0.0, 0.0, 0.0, 0.0)
    private val dy = listOf(0.0, 0.0, 1.0, -1.0, 0.0, 0.0)
    private val dz = listOf(0.0, 0.0, 0.0, 0.0, 1.0, -1.0)

    fun pathFinding(start: Location, destination: Location): List<Location> {
        val path = mutableListOf<Location>()
        val pq = PriorityQueue<Node>()
        val visited = mutableSetOf<Location>()
        val startNode = Node(start, null, start, destination)
        startNode.weight *= 3
        pq.add(startNode)
        visited.add(start)
        while (pq.isNotEmpty()) {
            val node = pq.poll()
            //Finding
            for (i in 0 until 6) {
                val next = node.current.clone().add(dx[i], dy[i], dz[i])
                //Arrived destination
                if (next == destination) {
                    var nextNode: Node? = Node(next, node, node.current, node.destination)
                    while (nextNode != null) {
                        path.add(nextNode.current)
                        nextNode = nextNode.parent
                    }
                    //pathfinding.logger.info("Complete path finding")
                    return path
                }
                if (next in visited || !next.block.isPassable) continue
                if (!next.block.getRelative(0, -1, 0).isSolid && !next.block.getRelative(0, -2, 0).isSolid) continue

                val nextNode = Node(next, node, node.current, node.destination)
                if (startNode.weight < nextNode.weight) continue
                pq.add(nextNode)
                visited.add(next)
            }
        }
        return path
    }

    data class Node(
        val current: Location,
        val parent: Node?,
        val start: Location,
        val destination: Location
    ) : Comparable<Node> {
        override fun compareTo(other: Node): Int = (weight - other.weight).toInt()

        //Heuristic f() = g() + h()
        var weight = g() + h()

        //Start node -> Current node manhattan distance
        private fun g(): Double {
            return current.distanceSquared(start)
        }


        //Current node -> destination manhattan distance
        private fun h(): Double {
            return current.distanceSquared(destination)
        }
    }

}