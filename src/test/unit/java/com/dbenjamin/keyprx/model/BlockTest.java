package com.dbenjamin.keyprx.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BlockTest {

    @Test
    //Requires JUnit5... @DisplayName("Capacity 0 case returns zero overbooked capacity")
    public void testComputeOverbookedCapacityBaseline() {
        Block block = new Block();
        block.setCapacity(0);
        block.setOverbookPercent(300);
        assertEquals(Integer.valueOf(0), block.computeOverbookedCapacity());
    }

    @Test
    //@DisplayName("Capacity 1 Overbook 0% case returns 1 overbooked capacity")
    public void testComputeOverbookedCapacity1and0() {
        Block block = new Block();
        block.setCapacity(1);
        block.setOverbookPercent(0);
        assertEquals(Integer.valueOf(1), block.computeOverbookedCapacity());
    }

    @Test
    //@DisplayName("Capacity 3 Overbook 50% case returns 4 overbooked capacity -- no fractional units or rounding up")
    public void testComputeOverbookedCapacity3And50() {
        Block block = new Block();
        block.setCapacity(3);
        block.setOverbookPercent(50);
        assertEquals(Integer.valueOf(4), block.computeOverbookedCapacity());
    }

    @Test
    //@DisplayName("Capacity 3 Overbook 100% case returns 6 overbooked capacity")
    public void testComputeOverbookedCapacity3And100() {
        Block block = new Block();
        block.setCapacity(3);
        block.setOverbookPercent(100);
        assertEquals(Integer.valueOf(6), block.computeOverbookedCapacity());
    }


}