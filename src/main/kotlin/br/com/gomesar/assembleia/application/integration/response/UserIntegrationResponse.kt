package br.com.gomesar.assembleia.application.integration.response

import java.security.SecureRandom

data class UserIntegrationResponse(val status: EStatusUsuarioVotacao)

enum class EStatusUsuarioVotacao {
    ABLE_TO_VOTE,
    UNABLE_TO_VOTE;

    companion object {
        private val random = SecureRandom()
        fun randomStatus() = with(values()) {
            this[random.nextInt(this.size)]
        }
    }
}
