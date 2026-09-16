package com.anish.logprocessor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LogprocessorApplication

fun main(args: Array<String>) {
	runApplication<LogprocessorApplication>(*args)
}
