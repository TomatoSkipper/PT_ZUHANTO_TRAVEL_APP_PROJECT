package com.example.appproject.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.appproject.data.model.Booking

@Dao
interface BookingDao {
    @Insert
    suspend fun insertBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("SELECT * FROM bookings ORDER BY bookingDate")
    suspend fun getAllBookings(): List<Booking>

    @Query("SELECT * FROM bookings WHERE customerUsername = :username ORDER BY bookingDate")
    suspend fun getBookingsForCustomer(username: String): List<Booking>
}