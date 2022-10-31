package it.devchallenge.cncmachine.simplebox

import it.devchallenge.cncmachine.simplebox.service.InvalidRequestException
import it.devchallenge.cncmachine.simplebox.service.SimpleBoxService
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*

@RestController
class SimpleBoxController(val simpleBoxService: SimpleBoxService) {

    @PostMapping("/api/simple_box")
    fun simpleBox(@RequestBody request: SimpleBoxRequest): SimpleBoxResponse {
        return simpleBoxService.generateLayout(request)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun illegalJsonHandler(ex: HttpMessageNotReadableException): SimpleBoxResponse {
        return SimpleBoxResponse(success = false, error = "Invalid input format. Invalid json")
    }

    @ExceptionHandler(InvalidRequestException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    fun invalidRequestHandler(ex: InvalidRequestException): SimpleBoxResponse {
        return SimpleBoxResponse(success = false, error = ex.message)
    }
}