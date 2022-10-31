package it.devchallenge.cncmachine.simplebox.service

import com.qunhe.util.nest.data.NestPath
import com.qunhe.util.nest.util.GeometryUtil
import it.devchallenge.cncmachine.simplebox.*
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class CncProgramService {

    fun placementsToProgram(nestingResponse: NestingResponse, sheetSize: SheetSize): List<CncCommand> {
        val lines = placementsToLines(nestingResponse)
        return mutableListOf<CncCommand>().apply {
            add(CncCommand(CommandType.START))

            var currentPos = Point(0, 0)
            var currentUp = true
            for (line in lines) {

                if (line.start != currentPos) {
                    if (!currentUp) {
                        add(CncCommand(CommandType.UP))
                        currentUp = true
                    }
                    add(CncCommand(CommandType.GOTO, line.start.x, line.start.y))
                }

                val onEdge = onEdge(line, sheetSize)
                if (currentUp && !onEdge) {
                    add(CncCommand(CommandType.DOWN))
                    currentUp = false
                }
                if (!currentUp && onEdge) {
                    add(CncCommand(CommandType.UP))
                    currentUp = true
                }

                if (this.last().command == CommandType.GOTO && currentUp) {
                    this.removeLast()
                }
                add(CncCommand(CommandType.GOTO, line.end.x, line.end.y))
                currentPos = line.end
            }

            removeRedundantCommands(currentUp)

            add(CncCommand(CommandType.STOP))
        }
    }

    private fun MutableList<CncCommand>.removeRedundantCommands(currentUp: Boolean) {
        if (last().command == CommandType.GOTO && currentUp) {
            removeLast()
        }
        if (last().command == CommandType.UP) {
            removeLast()
        }
    }

    private fun onEdge(line: Line, sheetSize: SheetSize): Boolean {
        return listOf(
            line.start.x == 0 && line.end.x == 0,
            line.start.x == sheetSize.w && line.end.x == sheetSize.w,
            line.start.y == 0 && line.end.y == 0,
            line.start.y == sheetSize.l && line.end.y == sheetSize.l,
        )
            .any { it }
    }

    private fun placementsToLines(nestingResponse: NestingResponse) =
        nestingResponse.placement.map {
            val rotatedBox = GeometryUtil.rotatePolygon2Polygon(nestingResponse.box, it.rotate.toInt())
            rotatedBox.setOffsetX(it.translate.x)
            rotatedBox.setOffsetY(it.translate.y)
            val points = rotatedBox.getPoints()
            mutableListOf<Point>().apply {
                addAll(points)
                add(points.first())
            }
        }
            .map { boxPoints ->
                boxPoints
                    .windowed(size = 2, step = 1)
                    .map { it.toLine() }
            }
            .flatten()
            .distinct()

    private fun List<Point>.toLine(): Line = Line(this[0], this[1])

    private fun NestPath.getPoints(): List<Point> {
        return this.segments
            .map { it.x + offsetX to it.y + offsetY }
            .map { Point(it.first.roundToInt(), it.second.roundToInt()) }
    }
}