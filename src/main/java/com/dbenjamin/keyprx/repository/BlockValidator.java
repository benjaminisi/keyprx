package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Block;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BlockValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Block.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Block block = (Block) target;
        
        if (block.getCapacity() == null) {
            errors.rejectValue("capacity", "capacity.missing", "Add your capacity");
        } else if (block.getCapacity() < 0) {
            errors.rejectValue("capacity", "capacity.outOfBounds", "Your capacity needs to be zero or more");
        }

        if (block.getOverbookPercent() == null) {
            errors.rejectValue("overbookPercent", "overbookPercent.missing", "Add your overbookPercent");
        } else if (block.getOverbookPercent() < 0) {
            errors.rejectValue("overbookPercent", "overbookPercent.outOfBounds", "Your overbookPercent needs to be zero or more");
        }

    }
}
