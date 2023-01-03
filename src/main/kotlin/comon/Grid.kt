package comon

import java.util.TreeMap

abstract class Grid<T> {
    val elements = TreeMap<Int, TreeMap<Int, T>>()
    abstract val minY: Int
    abstract val maxY: Int
    abstract val minX: Int

    abstract val maxX: Int
    abstract operator fun get(x: Int, y: Int): T?
    operator fun get(point: Point) = get(point.x, point.y)

    abstract fun put(x: Int, y: Int, element: T)

    operator fun set(x: Int, y: Int, element: T) {
        put(x, y, element)
    }

    operator fun set(point: Point, element: T) = put(point.x, point.y, element)

    val xRange
        get() = minX..maxX
    val yRange
        get() = minY..maxY

    fun clear() {
        elements.clear()
    }

    fun put(point: Point, element: T) {
        put(point.x, point.y, element)
    }

    fun println(delegate: (T?) -> String) {
        println(Point.Fixed(minX, minY), Point.Fixed(maxX, maxY), delegate)
    }

    fun println(topLeft: Point, bottomRight: Point, delegate: (T?) -> String) {
        for (y in topLeft.y..bottomRight.y) {
            for (x in topLeft.x..bottomRight.x) {
                print(delegate.invoke(get(x, y)))
            }
            println("")
        }
    }
}

class GridByRows<T> : Grid<T>() {
    override val minY: Int
        get() = elements.keys.minOfOrNull { it } ?: 0
    override val maxY: Int
        get() = elements.keys.maxOfOrNull { it } ?: 0
    override val minX: Int
        get() = elements.values.minOfOrNull { it.keys.minOf { it } } ?: 0
    override val maxX: Int
        get() = elements.values.maxOfOrNull { it.keys.maxOf { it } } ?: 0

    fun row(y: Int) = elements[y]

    override operator fun get(x: Int, y: Int): T? = elements[y]?.get(x)

    override fun put(x: Int, y: Int, element: T) {
        elements.getOrPut(y) {
            TreeMap()
        }[x] = element
    }
}

class GridByColumns<T> : Grid<T>() {

    override val minX: Int
        get() = elements.keys.minOf { it }
    override val maxX: Int
        get() = elements.keys.maxOf { it }
    override val minY: Int
        get() = elements.values.minOf { it.keys.minOf { it } }
    override val maxY: Int
        get() = elements.values.maxOf { it.keys.maxOf { it } }

    fun column(x: Int) = elements[x]

    override operator fun get(x: Int, y: Int): T? =
        elements[x]?.get(y)

    override fun put(x: Int, y: Int, element: T) {
        elements.getOrPut(x) {
            TreeMap()
        }[y] = element
    }
}

class Grid3d<T> {
    val elements = mutableMapOf<Int, MutableMap<Int, MutableMap<Int, T>>>()

    val minX: Int
        get() = elements.keys.minOrNull() ?: 0
    val maxX: Int
        get() = elements.keys.maxOrNull() ?: 0

    val minY: Int
        get() = elements.values.minOfOrNull { y -> y.keys.minOf { it } } ?: 0
    val maxY: Int
        get() = elements.values.maxOfOrNull { y -> y.keys.maxOf { it } } ?: 0

    val minZ: Int
        get() = elements.values.minOfOrNull { y ->
            y.values.minOfOrNull { z -> z.keys.minOf { it } } ?: 0
        } ?: 0

    val maxZ: Int
        get() = elements.values.maxOfOrNull { y ->
            y.values.maxOfOrNull { z -> z.keys.maxOf { it } } ?: 0
        } ?: 0

    val xRange
        get() = minX..maxX
    val yRange
        get() = minY - 1..maxY + 1
    val zRange
        get() = minZ - 1..maxZ + 1

    operator fun get(x: Int, y: Int, z: Int): T? = elements[x]?.get(y)?.get(z)
    operator fun get(point: Point3d): T? = get(point.x, point.y, point.z)

    operator fun set(x: Int, y: Int, z: Int, element: T) {
        put(x, y, z, element)
    }

    operator fun set(point: Point3d, element: T) {
        put(point.x, point.y, point.z, element)
    }

    fun put(x: Int, y: Int, z: Int, element: T) {
        elements.getOrPut(x) {
            mutableMapOf()
        }.getOrPut(y) {
            mutableMapOf()
        }[z] = element
    }

    fun orthogonalNeighbors(x: Int, y: Int, z: Int): List<Pair<Point3d, T>> {
        val neighbors = mutableListOf<Pair<Point3d, T>>()

        this[x - 1, y, z]?.let { neighbors.add(Point3d(x - 1, y, z) to it) }
        this[x + 1, y, z]?.let { neighbors.add(Point3d(x + 1, y, z) to it) }

        this[x, y - 1, z]?.let { neighbors.add(Point3d(x, y - 1, z) to it) }
        this[x, y + 1, z]?.let { neighbors.add(Point3d(x, y + 1, z) to it) }

        this[x, y, z - 1]?.let { neighbors.add(Point3d(x, y, z - 1) to it) }
        this[x, y, z + 1]?.let { neighbors.add(Point3d(x, y, z + 1) to it) }
        return neighbors
    }

    inline fun forEach(action: (Int, Int, Int, T) -> Unit) {
        for ((x, xDimension) in elements) {
            for ((y, yDimension) in xDimension) {
                for ((z, element) in yDimension) {
                    action(x, y, z, element)
                }
            }
        }
    }
}
