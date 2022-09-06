package br.com.gomesar.assembleia.domain.repositories

import br.com.gomesar.assembleia.domain.entities.Pauta
import im.aop.loggers.advice.before.LogBefore
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IPautaRepository : JpaRepository<Pauta, UUID> {
    @LogBefore(declaringClass = IPautaRepository::class)
    fun existsByNome(nome: String): Boolean
    @LogBefore(declaringClass = IPautaRepository::class)
    fun getPautaById(id: UUID): Pauta?

}