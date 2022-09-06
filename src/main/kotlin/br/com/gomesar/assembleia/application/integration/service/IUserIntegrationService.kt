package br.com.gomesar.assembleia.application.integration.service

import br.com.gomesar.assembleia.application.integration.request.UserIntegrationRequest
import br.com.gomesar.assembleia.application.integration.response.UserIntegrationResponse

interface IUserIntegrationService {
    fun getUserResponse(request: UserIntegrationRequest): UserIntegrationResponse
}