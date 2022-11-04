package it.valeriovaudi.vauthenticator.password

interface PasswordPolicy {

    fun accept(password: String)
}

class PasswordPolicyViolation(message: String) : RuntimeException(message)

class NoPasswordPolicy : PasswordPolicy {
    override fun accept(password: String) {

    }

}

class SpecialCharacterPasswordPolicy : PasswordPolicy {
    override fun accept(password: String) {
        if ("^[A-Za-z0-9\\s]+".toRegex().matches(password)) {
            throw PasswordPolicyViolation("the password should has at least one special character like '!£\$%&/()=?'")
        }
    }
}

class MinimumCharacterPasswordPolicy(private val size: Int) : PasswordPolicy {
    override fun accept(password: String) {
        if (password.length < size) {
            throw PasswordPolicyViolation("the password size should be at least $size")
        }
    }
}


class CompositePasswordPolicy(private val passwordPolicies: Set<PasswordPolicy>) : PasswordPolicy {
    override fun accept(password: String) {
        passwordPolicies.forEach { it.accept(password) }
    }
}
