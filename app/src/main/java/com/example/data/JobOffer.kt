package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobOffer(
    @PrimaryKey val id: String, // Telegram Msg ID or Generated UUID
    val jobTitle: String,
    val workflowMethodology: String, // How to perform the work
    val CompensationAmount: String, // Payment/Price specified
    val deliveryAndPaymentInstructions: String, // Submission & cashout mechanism
    val originalLanguageText: String,
    val translatedArabicText: String, // Automatic Arabic translation
    val employerContactLink: String, // Telegram direct username/chat link
    val sourceGroupName: String,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val category: String = "عام",
    val status: String = "جديد"
)
