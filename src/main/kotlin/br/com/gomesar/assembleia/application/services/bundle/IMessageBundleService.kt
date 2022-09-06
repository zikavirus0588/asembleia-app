package br.com.gomesar.assembleia.application.services.bundle

interface IMessageBundleService {
    fun getMessage(key: String, vararg args: String): String
}