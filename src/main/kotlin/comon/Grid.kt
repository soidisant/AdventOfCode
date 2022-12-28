package comon


abstract class Grid<T> {
    abstract val minY: Int
    abstract val maxY: Int
    abstract val minX: Int
    abstract val maxX: Int

    abstract operator fun get(x: Int, y: Int): T?
    abstract fun put(x: Int, y: Int, element: T)

    val elements = mutableMapOf<Int, MutableMap<Int, T>>()

    fun put(point: Point, element: T) {
        put(point.x, point.y, element)
    }

    fun println(delegate: (T?) -> String) {
        println(Point(minX, minY), Point(maxX, maxY), delegate)
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
        get() = elements.keys.minOf { it }
    override val maxY: Int
        get() = elements.keys.maxOf { it }
    override val minX: Int
        get() = elements.values.minOf { it.keys.minOf { it } }
    override val maxX: Int
        get() = elements.values.maxOf { it.keys.maxOf { it } }

    fun row(y: Int) = elements[y]

    override operator fun get(x: Int, y: Int): T? = elements[y]?.get(x)

    override fun put(x: Int, y: Int, element: T) {
        elements.getOrPut(y) {
            mutableMapOf()
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
            mutableMapOf()
        }[y] = element
    }
}