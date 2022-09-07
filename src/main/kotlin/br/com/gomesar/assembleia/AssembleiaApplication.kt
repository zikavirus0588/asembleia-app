package br.com.gomesar.assembleia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
class AssembleiaApplication

fun main(args: Array<String>) {
	runApplication<AssembleiaApplication>(*args)
	println("Aplicação está de pé")
}
