package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it "
            + "from Item as it "
            + "join it.owner as ow "
            + "where ow.id = ?1 ")
    List<Item> findOwnerItems(long userId);

    @Query("select it "
            + "from Item as it "
            + "where it.name like lower(?1) "
            + "or it.description like lower(?1)")
    List<Item> findAllByNameAndDescription(String text);

}
