package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SavoriaFirestoreRepository(context: Context) {

    private val databaseId = context.getString(R.string.firestore_database_id)
    private val db = FirebaseFirestore.getInstance(databaseId)
    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun syncUserProfile(
        displayName: String,
        email: String,
        phone: String = ""
    ) {
        val uid = requireUserId()
        val userDoc = db.collection("users").document(uid)
        val data = hashMapOf<String, Any>(
            "userId" to uid,
            "displayName" to displayName,
            "email" to email,
            "phoneNumber" to phone,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            userDoc.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userDoc.path)
            throw e
        }
    }

    suspend fun saveReservation(
        reservationCode: String,
        guestName: String,
        guestPhone: String,
        guestEmail: String,
        date: String,
        timeSlot: String,
        guestCount: Int,
        tableNumber: Int,
        zone: String,
        specialRequests: String,
        occasion: String
    ): String {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("reservations")
        val docRef = collection.document(reservationCode)

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "reservationCode" to reservationCode,
            "guestName" to guestName,
            "guestPhone" to guestPhone,
            "guestEmail" to guestEmail,
            "date" to date,
            "timeSlot" to timeSlot,
            "guestCount" to guestCount,
            "tableNumber" to tableNumber,
            "zone" to zone,
            "specialRequests" to specialRequests,
            "occasion" to occasion,
            "status" to "CONFIRMED",
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun saveOrder(
        orderCode: String,
        orderType: String,
        destination: String,
        guestName: String,
        subtotal: Double,
        taxAmount: Double,
        tipAmount: Double,
        grandTotal: Double,
        paymentType: String,
        paymentStatus: String,
        transactionRef: String,
        orderStatus: String,
        itemsSummary: String
    ): String {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("orders")
        val docRef = collection.document(orderCode)

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "orderCode" to orderCode,
            "orderType" to orderType,
            "destination" to destination,
            "guestName" to guestName,
            "subtotal" to subtotal,
            "taxAmount" to taxAmount,
            "tipAmount" to tipAmount,
            "grandTotal" to grandTotal,
            "paymentType" to paymentType,
            "paymentStatus" to paymentStatus,
            "transactionRef" to transactionRef,
            "orderStatus" to orderStatus,
            "itemsSummary" to itemsSummary,
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeUserReservations(): Flow<List<Map<String, Any>>> = flow {
        val uid = requireUserId()
        val ref = db.collection("users").document(uid).collection("reservations")
        emitAll(
            ref.snapshots()
                .map { snap -> snap.documents.mapNotNull { it.data } }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, ref.path)
                    throw e
                }
        )
    }

    fun observeUserOrders(): Flow<List<Map<String, Any>>> = flow {
        val uid = requireUserId()
        val ref = db.collection("users").document(uid).collection("orders")
        emitAll(
            ref.snapshots()
                .map { snap -> snap.documents.mapNotNull { it.data } }
                .catch { e ->
                    if (e is Exception) handleFirestoreError(e, OperationType.LIST, ref.path)
                    throw e
                }
        )
    }
}
