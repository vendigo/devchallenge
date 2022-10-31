package it.devchallenge.cncmachine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class CncMachineApplication

fun main(args: Array<String>) {
    runApplication<CncMachineApplication>(*args)
}
