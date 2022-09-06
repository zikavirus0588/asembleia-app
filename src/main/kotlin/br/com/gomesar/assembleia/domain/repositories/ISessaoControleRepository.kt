package br.com.gomesar.assembleia.domain.repositories

import br.com.gomesar.assembleia.domain.entities.SessaoControle
import im.aop.loggers.advice.before.LogBefore
import org.springframework.data.repository.CrudRepository

interface ISessaoControleRepository : CrudRepository<SessaoControle, String> {
    @LogBefore(declaringClass = ISessaoControleRepository::class)
    fun findBySessaoId(id: String): SessaoControle?
}