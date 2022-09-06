package br.com.gomesar.assembleia.domain.repositories

import br.com.gomesar.assembleia.domain.entities.Sessao
import im.aop.loggers.advice.before.LogBefore
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ISessaoRepository : JpaRepository<Sessao, UUID> {
    @LogBefore(declaringClass = ISessaoRepository::class)
    fun findByPautaId(pautaUUID: UUID): Sessao?
}