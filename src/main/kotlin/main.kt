import java.math.BigDecimal
import java.util.*

typealias Inventory = MutableMap<Product, Int>

enum class Product(val price: BigDecimal) {
    COLA(BigDecimal("1")),
    WATER(BigDecimal("0.5"))
}

class VendingMachine(private val inventory: Inventory) {
    var state = State.READY
        private set

    var credit = BigDecimal("0")
        private set

    enum class Action {
        DISPLAY_INFO,
        INSERT_COIN,
        SELECT_PRODUCT,
        ABORT
    }

    enum class State {
        READY {
            override fun nextState(vendingMachine: VendingMachine, action: Action): State {
                return when (action) {
                    Action.DISPLAY_INFO -> {
                        vendingMachine.displayInfo()
                        READY
                    }
                    Action.INSERT_COIN -> {
                        println("insert $1")
                        vendingMachine.topUp()
                        vendingMachine.displayCredit()
                        READY
                    }
                    Action.SELECT_PRODUCT -> {
                        println("select cola")
                        when {
                            !vendingMachine.hasEnoughCredit(Product.COLA) -> {
                                println("not enough credit")
                                READY
                            }
                            !vendingMachine.hasStock(Product.COLA) -> {
                                println("out of stock")
                                READY
                            }
                            else -> DISPENSE
                        }
                    }
                    Action.ABORT -> {
                        println("abort purchase")
                        COIN_CHANGE
                    }
                }
            }
        },
        COIN_CHANGE {
            override fun nextState(vendingMachine: VendingMachine, action: Action): State {
                println("coin change returned $${vendingMachine.credit}")
                vendingMachine.clearCredit()
                return READY
            }
        },
        DISPENSE {
            override fun nextState(vendingMachine: VendingMachine, action: Action): State {
                println("dispensing cola")
                vendingMachine.purchase(Product.COLA)
                return COIN_CHANGE
            }
        };

        abstract fun nextState(vendingMachine: VendingMachine, action: Action): State
    }

    fun execute(action: Action){
        this.state = this.state.nextState(this, action)
    }

    fun run(action: Action) {
        do {
            execute(action)
        } while (this.state !== State.READY)
    }

    private fun displayInfo() {
        println("----current inventory----")
        this.displayInventory()
        this.displayCredit()
        println("-------------------------")
    }

    private fun displayCredit() {
        println("credit available: ${this.credit}")
    }

    private fun displayInventory() {
        this.inventory.forEach {
            println("${it.key.name}\t($${it.key.price})\tQTY: ${it.value}")
        }
    }

    private fun topUp() {
        this.credit = this.credit.add(BigDecimal("1"))
    }

    private fun hasEnoughCredit(product: Product): Boolean {
        return this.credit >= product.price
    }

    private fun hasStock(product: Product): Boolean {
        return this.inventory.getValue(product) > 0
    }

    private fun purchase(product: Product) {
        this.credit = this.credit.minus(product.price)
        this.inventory[product] = this.inventory.getValue(product) - 1
    }

    private fun clearCredit() {
        this.credit = BigDecimal("0")
    }
}

fun main(args: Array<String>) {

    VendingMachine(
        mutableMapOf(
            Product.COLA to 10,
            Product.WATER to 5
        )
    ).also {
        println("Welcome to Vending Machine!")
        val scanner = Scanner(System.`in`)

        while (true) {
            print("Enter action: ")
            val action = scanner.nextLine().trim()
            if (action == "QUIT") break

            it.run(VendingMachine.Action.valueOf(action))
        }

        println("Bye!")
    }

}
