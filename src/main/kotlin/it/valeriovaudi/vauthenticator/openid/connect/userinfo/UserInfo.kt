package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import java.time.Instant

data class UserInfo(var sub: String = "",

        // profile scope claims
                    var name: String? = null,
                    var family_name: String? = null,
                    var given_name: String? = null,
                    var middle_name: String? = null,
                    var nickname: String? = null,
                    var preferred_username: String? = null,
                    var profile: String? = null,
                    var picture: String? = null,
                    var website: String? = null,
                    var gender: String? = null,
                    var birthdate: String? = null,
                    var zoneinfo: String? = null,
                    var locale: String? = null,
                    var updated_at: Long? = null,

        // email claims
                    var email: String? = null,
                    var email_verified: Boolean? = null,

        // my custom claims
                    var userName: String = "",
                    var authorities: List<String>)