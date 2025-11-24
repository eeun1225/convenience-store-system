package store.domain.user

class UserRepository {
    private val customers = mutableMapOf<String, User.Customer>()

    fun saveCustomer(customer: User.Customer) {
        customers[customer.phoneNumber ?: customer.id] = customer
    }

    fun findCustomerByPhoneNumber(phoneNumber: String): User.Customer? {
        return customers[phoneNumber]
    }

    fun existsByPhoneNumber(phoneNumber: String): Boolean {
        return customers.containsKey(phoneNumber)
    }

    fun getAllCustomers(): List<User.Customer> {
        return customers.values.toList()
    }
}