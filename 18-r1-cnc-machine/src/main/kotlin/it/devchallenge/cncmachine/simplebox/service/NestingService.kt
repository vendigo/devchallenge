package it.devchallenge.cncmachine.simplebox.service

import com.qunhe.util.nest.Nest
import com.qunhe.util.nest.data.NestPath
import com.qunhe.util.nest.util.Config
import it.devchallenge.cncmachine.simplebox.BoxSize
import it.devchallenge.cncmachine.simplebox.NestingResponse
import it.devchallenge.cncmachine.simplebox.SimpleBoxRequest
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class NestingService {

    val rotationAngles = intArrayOf(0, 90, 180, 270)
    val loadFactor = 0.75

    @Cacheable(cacheNames = ["placingCache"])
    fun findOptimalPlacing(request: SimpleBoxRequest): NestingResponse {
        return request.boxSize.getPermutations()
            .map {
                val box = createBox(it)
                val placing = findPlacing(request, box)
                placing
            }
            .first { it.boxesPlaced > 0 }
    }

    private fun findPlacing(request: SimpleBoxRequest, box: NestPath): NestingResponse {
        val binW = request.sheetSize.w + 0.01
        val binH = request.sheetSize.l + 0.01
        val bin = createBin(binW, binH)

        val config = createConfig(binW, binH)
        val maxPossibleAmount = (maxPossibleAmount(request) * loadFactor)
        val polygons = Array(maxPossibleAmount.toInt()) { box }.toList()
        val minCapacity = minCapacity(request)
        val iterations = amountOfIterations(minCapacity)
        println("Min capacity: $minCapacity, iterations: $iterations")
        val nest = Nest(bin, polygons, config, iterations)

        val placement = nest.startNest().maxByOrNull { it.size } ?: emptyList()
        return NestingResponse(box, placement)
    }

    private fun createBin(binW: Double, binH: Double): NestPath = NestPath()
        .apply {
            add(0.0, 0.0)
            add(binW, 0.0)
            add(binW, binH)
            add(0.0, binH)
        }

    private fun createBox(box: BoxSize): NestPath {
        val w = box.w.toDouble()
        val h = box.h.toDouble()
        val d = box.d.toDouble()
        return NestPath().apply {
            add(0.0, 0.0)
            add(h, 0.0)
            add(h, w)
            add(2 * w + 2 * h, w)
            add(2 * w + 2 * h, w + d)
            add(h, w + d)
            add(h, 2 * w + d)
            add(0.0, 2 * w + d)
            possibleRotations = rotationAngles
        }
    }

    private fun createConfig(binW: Double, binH: Double): Config {
        val config = Config()
        config.SPACING = 0.0
        Config.BIN_WIDTH = binW
        Config.BIN_HEIGHT = binH
        return config
    }

    private fun maxPossibleAmount(request: SimpleBoxRequest): Int {
        val sheetSquare = request.sheetSize.w * request.sheetSize.l
        val (w, d, h) = request.boxSize
        val boxSquare = 2 * (w * h + w * d + h * d)
        return sheetSquare / boxSquare
    }

    fun minCapacity(request: SimpleBoxRequest): Int {
        val boxSize = request.boxSize
        val sheetSize = request.sheetSize
        val rectSquare = 2 * (boxSize.w + boxSize.h) * (boxSize.d + boxSize.w * 2)
        val sheetSquare = sheetSize.w * sheetSize.l
        return sheetSquare / rectSquare
    }

    fun amountOfIterations(minCapacity: Int): Int = max(100 - minCapacity, 2)
}