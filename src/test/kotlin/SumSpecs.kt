import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SumSpecs : FunSpec({
    test("5 + 5 doit être égal à 10") {
        5 + 5 shouldBe 10
    }
})