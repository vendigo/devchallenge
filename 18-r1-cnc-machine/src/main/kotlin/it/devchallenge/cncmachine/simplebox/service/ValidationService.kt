package it.devchallenge.cncmachine.simplebox.service


import it.devchallenge.cncmachine.simplebox.SheetSize
import it.devchallenge.cncmachine.simplebox.SimpleBoxRequest
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

@Service
class ValidationService {
    fun validateRequest(request: SimpleBoxRequest) {
        validatePositiveSizes(request)
        validateSheetToSmall(request)
    }

    private fun validatePositiveSizes(request: SimpleBoxRequest) {
        val nonPositiveNumbers =
            listOf(request.sheetSize.l, request.sheetSize.w, request.boxSize.d, request.boxSize.h, request.boxSize.w)
                .any { it <= 0 }

        if (nonPositiveNumbers) {
            throw InvalidRequestException("Invalid input format. Please use only positive numbers")
        }
    }

    private fun validateSheetToSmall(request: SimpleBoxRequest) {
        val fitOnSheet = request.boxSize.getPermutations()
            .map { 2 * (it.w + it.h) to it.d + 2 * it.w }
            .distinct()
            .any { finOnSheet(it, request.sheetSize) }

        if (!fitOnSheet) {
            throw InvalidRequestException("Invalid input format. Too small for producing at least one box")
        }

    }

    private fun finOnSheet(boxRectSize: Pair<Int, Int>, sheetSize: SheetSize): Boolean {
        val minBoxSide = min(boxRectSize.first, boxRectSize.second)
        val maxBoxSide = max(boxRectSize.first, boxRectSize.second)

        val minSheetSize = min(sheetSize.w, sheetSize.l)
        val maxSheetSize = max(sheetSize.w, sheetSize.l)

        return minBoxSide <= minSheetSize && maxBoxSide <= maxSheetSize
    }
}

class InvalidRequestException(override val message: String) : RuntimeException(message)