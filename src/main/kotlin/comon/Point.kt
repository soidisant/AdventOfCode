package comon

sealed class Point {
    abstract val x: Int
    abstract val y: Int

    data class Fixed(override val x: Int, override val y: Int) : Point()
    data class Mutable(override var x: Int, override var y: Int) : Point()
}

data class Point3d(val x: Int, val y: Int, val z: Int)
