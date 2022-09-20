package br.com.gomesar.assembleia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AssembleiaApplication

fun main(args: Array<String>) {
	runApplication<AssembleiaApplication>(*args)
	println("Aplicação está de pé")
}
