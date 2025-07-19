package com.userservice.userservice.Services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.userservice.userservice.Entities.Hotel;
import com.userservice.userservice.Entities.Rating;
import com.userservice.userservice.Entities.User;
import com.userservice.userservice.Exceptions.ResourceNotFoundException;
import com.userservice.userservice.External.Services.HotelService;
import com.userservice.userservice.Repositories.UserRepository;

/**
 * Service Implementation for User related operations
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Save a new user to the database
     * 
     * @param user the user object to save
     * @return the saved user with generated UUID
     */
    @Override
    public User saveUser(User user) {
        // Generate a unique UUID for the user
        String randomId = UUID.randomUUID().toString();
        user.setUserId(randomId);

        // Save the user into the repository
        return userRepository.save(user);
    }

    /**
     * Fetch all users from the database
     * 
     * @return list of all users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Fetch a single user by ID along with their ratings and hotel details
     * 
     * @param userId ID of the user to fetch
     * @return User object with ratings and hotel information
     */
    @Override
    public User getUser(String userId) {
        // Step 1: Fetch user from DB; if not found, throw custom exception
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID [" + userId + "] not found on server"));

        // Step 2: Call Rating Service to get list of ratings for this user
        Rating[] ratingsArray = restTemplate.getForObject(
            "http://RATING-SERVICE/rating/user/" + user.getUserId(), Rating[].class
        );
        logger.info("Fetched Ratings for user [{}]: {}", userId, ratingsArray);

        // Step 3: Convert array to list for stream operations
        List<Rating> ratings = Arrays.asList(ratingsArray);

        // Step 4: For each rating, fetch hotel details from Hotel Service
        List<Rating> enrichedRatings = ratings.stream().map(rating -> {
            // Call Hotel Service by hotelId
            // ResponseEntity<Hotel> hotelResponse = restTemplate.getForEntity(
            //     "http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class
            // );

            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            // logger.info("Fetched Hotel [{}] for Rating [{}]. Status Code: {}", 
            //             hotel != null ? hotel.getId() : "null",
            //             rating.getRatingId(),
            //             hotelResponse.getStatusCode());

            // Bind hotel details to rating
            rating.setHotel(hotel);

            return rating;
        }).collect(Collectors.toList());

        // Step 5: Set ratings (with hotel info) to user and return
        user.setRatings(enrichedRatings);
        return user;
    }
}
