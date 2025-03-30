package com.atu.WednesdaysWickedAdventures.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.atu.WednesdaysWickedAdventures.model.Booking;

/**
 * Repository interface for managing {@link Booking} entities in a MongoDB database.
 * This interface extends {@link MongoRepository}, providing basic CRUD operations
 * and other MongoDB-specific functionalities for {@link Booking} objects.
 *
 * <p>The repository uses {@link String} as the type of the entity's ID.</p>
 *
 * <p>This interface is automatically implemented by Spring Data MongoDB during runtime.</p>
 *
 * @see MongoRepository
 * @see Booking
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
	
}
