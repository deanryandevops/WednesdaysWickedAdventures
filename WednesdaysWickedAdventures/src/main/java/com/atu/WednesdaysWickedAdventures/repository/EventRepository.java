package com.atu.WednesdaysWickedAdventures.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.atu.WednesdaysWickedAdventures.model.Event;

/**
 * Repository interface for managing {@link Event} entities in a MongoDB database.
 * This interface extends {@link MongoRepository}, providing basic CRUD operations
 * and other MongoDB-specific functionalities for {@link Event} objects.
 *
 * <p>The repository uses {@link String} as the type of the entity's ID.</p>
 *
 * <p>This interface is automatically implemented by Spring Data MongoDB during runtime.</p>
 *
 * @see MongoRepository
 * @see Event
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@Repository
public interface EventRepository extends MongoRepository<Event, String>{

}
