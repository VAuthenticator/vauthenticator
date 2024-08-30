package com.vauthenticator.server.communication.email

import com.hubspot.jinjava.Jinjava

interface MailTemplateResolver {

    fun compile(template: String, context: Map<String, Any>): String

}

class JinjavaMailTemplateResolver(private val engine: Jinjava) : MailTemplateResolver {
    override fun compile(template: String, context: Map<String, Any>): String {
        return engine.render(template, context)
    }

}
