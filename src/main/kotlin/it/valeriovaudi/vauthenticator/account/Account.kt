package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.extentions.toSha256
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Account(var accountNonExpired: Boolean = false,
                   var accountNonLocked: Boolean = false,
                   var credentialsNonExpired: Boolean = false,
                   var enabled: Boolean = false,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   var email: String,
                   var emailVerified: Boolean = false,

                   var firstName: String,
                   var lastName: String,

                   val birthDate : Date,
                   val phone: Phone
) {
    val sub: String
        get() = email.toSha256()
}

data class Date(val localDate: LocalDate,
                val dateTimeFormatter: DateTimeFormatter = DEFAULT_DATE_TIME_FORMATTER) : Comparable<Date> {

    fun formattedDate(): String {
        return dateTimeFormatter.format(localDate)
    }

    override operator fun compareTo(o: Date): Int {
        return this.localDate.compareTo(o.localDate)
    }

    companion object {
        val DEFAULT_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        fun dateFor(date: String): Date {
            return Date(LocalDate.parse(date, DEFAULT_DATE_TIME_FORMATTER))
        }
    }
}

class Phone(private val countryPrefix: String, private val prefix: String, private val phoneNumber: String) {
    fun formattedPhone(): String {
        return String.format("%s %s %s", countryPrefix, prefix, phoneNumber).trim { it <= ' ' }
    }

    companion object {
        fun nullValue(): Phone {
            return Phone("", "", "")
        }

        fun phoneFor(phoneNumber: String): Phone {
            var phone = nullValue()
            val split = phoneNumber.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size == 3) {
                phone = Phone(split[0], split[1], split[2])
            } else if (split.size == 2) {
                phone = Phone("", split[0], split[1])
            }
            return phone
        }
    }
}
