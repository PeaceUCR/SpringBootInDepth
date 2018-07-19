package com.example.demo.service;

import com.example.demo.entity.Item;
import com.example.demo.repo.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private ItemRepo itemrepo;
    @Autowired
    public void setItemrepo(ItemRepo repo){
        this.itemrepo = repo;
    }

    public List<Item> getAll(){
        return itemrepo.findAll();
    }


    public Item getById(Integer id){
        //convert option<Item> to Item
        //https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
        Optional<Item> tmp = itemrepo.findById(id);
        return tmp.isPresent()?tmp.get():null;
    }

    public Item getByName(String name){
        return itemrepo.findByName(name);
    }
    //https://stackoverflow.com/questions/24420572/update-or-saveorupdate-in-crudrespository-is-there-any-options-available
    public void create(Item i){
        itemrepo.save(i);
    }
    /*crud don't allow you to update, only possible in hibernate */
    public int updateDetailsByName(String name, String newdetails){
       return itemrepo.updateDetailsByName(name, newdetails);
    }

    /*crud don't allow you to update, only possible in hibernate ---from vijay*/
    /*get item by id then update it*/
    public void update(Integer id, Item newItem){
       Item old = itemrepo.findById(id).get();
       old.setName(newItem.getName());
       old.setDetails(newItem.getDetails());
        itemrepo.save(old);
    }


    public void deleteById(Integer id){
        itemrepo.deleteById(id);
    }

}
