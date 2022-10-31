package it.devchallenge.cncmachine.simplebox

import com.qunhe.util.nest.data.NestPath
import com.qunhe.util.nest.data.Placement

data class SimpleBoxRequest(val sheetSize: SheetSize, val boxSize: BoxSize)

data class SheetSize(val w: Int, val l: Int)

data class BoxSize(val w: Int, val d: Int, val h: Int) {
    fun getPermutations(): List<BoxSize> {
        return listOf(
            BoxSize(w, d, h),
            BoxSize(w, h, d),
            BoxSize(d, w, h),
            BoxSize(d, h, w),
            BoxSize(h, w, d),
            BoxSize(h, d, w)
        )
            .distinct()
    }
}

data class SimpleBoxResponse(
    val success: Boolean, val amount: Int? = null, val program: List<CncCommand>? = emptyList(),
    val error: String? = null
)

data class CncCommand(val command: CommandType, val x: Int? = null, val y: Int? = null)

enum class CommandType {
    START, UP, DOWN, GOTO, STOP
}

data class NestingResponse(val box: NestPath, val placement: List<Placement>) {
    val boxesPlaced: Int
        get() {
            return placement.size
        }
}

data class Point(val x: Int, val y: Int)

data class Line(val start: Point, val end: Point)