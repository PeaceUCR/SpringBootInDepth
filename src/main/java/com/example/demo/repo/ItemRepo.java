package com.example.demo.repo;

import com.example.demo.entity.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepo extends CrudRepository<Item, Integer> {

   public List<Item> findAll();

   public Item findByName(String name);

   //update
   @Transactional
   @Modifying
   @Query("update Item i set i.details = :newdetails where i.name = :name")
   //java.lang.IllegalArgumentException: Modifying queries can only use void or int/Integer as return type!
   public int updateDetailsByName(@Param("name") String name, @Param("newdetails") String newdetails);
}
