import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class VendingMachineTest {

    @Test
    fun `should initialized in READY state`() {
        VendingMachine(
            mutableMapOf(
                Product.COLA to 10,
                Product.WATER to 5
            )
        ).also {
            assertEquals(VendingMachine.State.READY, it.state)
        }
    }

    @Test
    fun `exec INSERT_COIN should transition from READY - READY`() {
        VendingMachine(
            mutableMapOf(
                Product.COLA to 10,
                Product.WATER to 5
            )
        ).also {
            assertEquals(VendingMachine.State.READY, it.state)

            it.execute(VendingMachine.Action.INSERT_COIN)

            assertEquals(BigDecimal("1"), it.credit)
            assertEquals(VendingMachine.State.READY, it.state)
        }
    }

    @Test
    fun `exec ABORT should transition from READY - COIN_CHANGE - READY`() {
        VendingMachine(
            mutableMapOf(
                Product.COLA to 10,
                Product.WATER to 5
            )
        ).also {
            assertEquals(VendingMachine.State.READY, it.state)

            it.execute(VendingMachine.Action.INSERT_COIN)

            assertEquals(BigDecimal("1"), it.credit)
            assertEquals(VendingMachine.State.READY, it.state)

            it.execute(VendingMachine.Action.ABORT)

            assertEquals(it.state, VendingMachine.State.COIN_CHANGE)

            it.execute(VendingMachine.Action.ABORT)

            assertEquals(BigDecimal("0"), it.credit)

            assertEquals(VendingMachine.State.READY, it.state)
        }
    }
}
