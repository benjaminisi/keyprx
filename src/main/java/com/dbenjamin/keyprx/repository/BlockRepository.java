package com.dbenjamin.keyprx.repository;

import com.dbenjamin.keyprx.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findByName(@Param("name") String name);

}