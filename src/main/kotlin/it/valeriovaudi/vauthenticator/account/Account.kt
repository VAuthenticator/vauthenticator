package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.extentions.toSha256
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class Account(
    var accountNonExpired: Boolean = false,
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

    val birthDate: Optional<Date>,
    val phone: Optional<Phone>
) {
    val sub: String
        get() = email.toSha256()
}

data class Date(
    val localDate: LocalDate,
    val dateTimeFormatter: DateTimeFormatter = USER_INFO_DEFAULT_DATE_TIME_FORMATTER
) : Comparable<Date> {

    fun formattedDate(): String {
        return dateTimeFormatter.format(localDate)
    }

    fun iso8601FormattedDate(): String {
        return USER_INFO_DEFAULT_DATE_TIME_FORMATTER.format(localDate)
    }

    override operator fun compareTo(o: Date): Int {
        return this.localDate.compareTo(o.localDate)
    }

    companion object {
        val USER_INFO_DEFAULT_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        fun empty(): Optional<Date> = Optional.empty()

        fun isoDateFor(date: String): Optional<Date> = try {
            Optional.of(Date(LocalDate.parse(date, USER_INFO_DEFAULT_DATE_TIME_FORMATTER)))
        } catch (e: RuntimeException) {
            empty()
        }

    }
}

data class Phone(private val countryPrefix: String, private val prefix: String, private val phoneNumber: String) {
    fun formattedPhone(): String {
        return String.format("%s %s %s", countryPrefix, prefix, phoneNumber).trim { it <= ' ' }
    }

    companion object {
        fun nullValue(): Phone {
            return Phone("", "", "")
        }

        fun phoneFor(phoneNumber: String): Optional<Phone> = try {
            var phone = Optional.empty<Phone>()
            val split = phoneNumber.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split.size == 3) {
                phone = Optional.of(Phone(split[0], split[1], split[2]))
            } else if (split.size == 2) {
                phone = Optional.of(Phone("", split[0], split[1]))
            }
            phone
        } catch (e: RuntimeException) {
            Optional.empty()
        }

        fun empty(): Optional<Phone> = Optional.empty()
    }
}


class AccountNotFoundException(message: String) : RuntimeException(message)