package it.valeriovaudi.vauthenticator.account.tiket

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication

interface TicketRepository {

    fun newTicketFor(account: Account, clientApplication: ClientApplication, features: VerificationTicketFeatures)

}