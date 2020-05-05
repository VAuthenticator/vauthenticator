package it.valeriovaudi.vauthenticator.oauth2.clientapp

//fun content(): String = when (this) {
//    is NotValidSecret -> "*********"
//    is FilledSecret -> this.content
//}

//private fun secretFor(clientApp: ClientApplication, passwordEncoder: VAuthenticatorPasswordEncoder) =
//        clientApp.secret.let {
//            when (it) {
//                is NotValidSecret -> it.content()
//                is FilledSecret -> passwordEncoder.encode(it.content())
//            }
//        }