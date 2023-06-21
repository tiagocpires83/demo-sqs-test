package br.com.demosqstest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoSqsTestApplication

fun main(args: Array<String>) {
	runApplication<DemoSqsTestApplication>(*args)
}
