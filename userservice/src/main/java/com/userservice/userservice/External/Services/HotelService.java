package com.userservice.userservice.External.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.userservice.userservice.Entities.Hotel;

@FeignClient(name="HOTEL-SERVICE")
public interface HotelService {

    @GetMapping("/hotels/{hotelId}")
    public Hotel getHotel(@PathVariable String hotelId);
    
} 
