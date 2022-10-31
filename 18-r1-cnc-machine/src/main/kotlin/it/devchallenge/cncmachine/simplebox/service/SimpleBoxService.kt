package it.devchallenge.cncmachine.simplebox.service

import it.devchallenge.cncmachine.simplebox.SimpleBoxRequest
import it.devchallenge.cncmachine.simplebox.SimpleBoxResponse
import org.springframework.stereotype.Service

@Service
class SimpleBoxService(
    val validationService: ValidationService,
    val nestingService: NestingService,
    val cncProgramService: CncProgramService
) {

    fun generateLayout(request: SimpleBoxRequest): SimpleBoxResponse {
        validationService.validateRequest(request)
        val nestingResponse = nestingService.findOptimalPlacing(request)
        val program = cncProgramService.placementsToProgram(nestingResponse, request.sheetSize)
        return SimpleBoxResponse(success = true, amount = nestingResponse.boxesPlaced, program = program)
    }


}