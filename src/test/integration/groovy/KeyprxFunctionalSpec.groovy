
// NOT FINISHED

import spock.lang.Specification

class KeyprxFunctionalSpec extends Specification {

    def setupSpec() {
        println "\nIn setupSpec()\n"
    }

    def 'should create a block'() {
        when:
        println "when"
        KeyprxClient keyprxClient = new KeyprxClient()
        Map block = keyprxClient.registerBlock("Hotel David", 3,50)

        then:
        block.name == "Hotel David"
        block.capacity == 3
        block.overbookPercent == 50
    }

    def 'should return the created a block'() {
        when:
        println "when"
        KeyprxClient keyprxClient = new KeyprxClient()
        keyprxClient.registerBlock("Hotel David", 3,50)
        Collection blocks = keyprxClient.fetchBlocks()

        then:
        !blocks.isEmpty()
        blocks[0].name == "Hotel David"
        blocks[0].capacity == 3
        blocks[0].overbookPercent == 50
    }

}