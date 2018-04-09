package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@DataJpaTest
public class BlockValidatorTest {

    private BlockValidator blockValidator = new BlockValidator();

    @Mock
    private Errors errors;

    @Test
    public void validateRejectsMissingCapacity() {
        Block block = new Block();
        block.setCapacity(null);
        block.setOverbookPercent(-1);
        blockValidator.validate(block, errors);
        verify(errors).rejectValue("capacity", "capacity.missing", "Add your capacity");
    }

    @Test
    public void validateRejectsNegativeCapacity() {
        Block block = new Block();
        block.setCapacity(-1);
        block.setOverbookPercent(-1);
        blockValidator.validate(block, errors);
        verify(errors).rejectValue("capacity", "capacity.outOfBounds", "Your capacity needs to be zero or more");
    }

    @Test
    public void validateRejectsMissingPercent() {
        Block block = new Block();
        block.setCapacity(3);
        block.setOverbookPercent(null);
        blockValidator.validate(block, errors);
        verify(errors).rejectValue("overbookPercent", "overbookPercent.missing", "Add your overbookPercent");
    }

    @Test
    public void validateRejectsNegativePercent() {
        Block block = new Block();
        block.setCapacity(3);
        block.setOverbookPercent(-1);
        blockValidator.validate(block, errors);
        verify(errors).rejectValue("overbookPercent", "overbookPercent.outOfBounds", "Your overbookPercent needs to be zero or more");
    }

}