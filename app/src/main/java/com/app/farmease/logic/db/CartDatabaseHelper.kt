package com.app.farmease.logic.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.app.farmease.logic.model.Cart

class CartDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart_db"
        private const val DATABASE_VERSION = 1

        // Table name and column names
        private const val TABLE_PRODUCTS = "cart"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_COUNT = "count"
    }

    // SQL query to create the products table with NOT NULL constraints where appropriate
    private val CREATE_TABLE_PRODUCTS = """
        CREATE TABLE $TABLE_PRODUCTS (
            $COLUMN_ID TEXT PRIMARY KEY NOT NULL,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_PRICE TEXT NOT NULL,
            $COLUMN_QUANTITY TEXT NOT NULL,
            $COLUMN_IMAGE TEXT,
            $COLUMN_COUNT INTEGER NOT NULL DEFAULT 0
        );
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_PRODUCTS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // In a production app, you might want to implement proper migration strategy
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    // Add a product to the cart using a transaction
    fun addProduct(product: Cart): Boolean {
        return try {
            writableDatabase.use { db ->
                db.beginTransaction()
                try {
                    // Handle null values by providing defaults
                    val values = ContentValues().apply {
                        // Required fields - should not be null
                        put(COLUMN_ID, product.id.takeIf { !it.isNullOrBlank() }
                            ?: throw IllegalArgumentException("Product ID cannot be null or empty"))
                        put(COLUMN_NAME, product.name.takeIf { !it.isNullOrBlank() }
                            ?: throw IllegalArgumentException("Product name cannot be null or empty"))
                        put(COLUMN_PRICE, product.price.takeIf { !it.isNullOrBlank() }
                            ?: throw IllegalArgumentException("Product price cannot be null or empty"))
                        put(COLUMN_QUANTITY, product.quantity.takeIf { !it.isNullOrBlank() }
                            ?: "1") // Default to 1 if null

                        // Optional fields - can be null
                        put(COLUMN_DESCRIPTION, product.description ?: "")
                        put(COLUMN_IMAGE, product.image ?: "")
                        put(COLUMN_COUNT, product.count.takeIf { it > 0 } ?: 1) // Default to 1 if 0 or negative
                    }

                    // Check if product exists
                    val cursor = db.query(
                        TABLE_PRODUCTS,
                        arrayOf(COLUMN_COUNT),
                        "$COLUMN_ID = ?",
                        arrayOf(product.id),
                        null, null, null
                    )

                    cursor.use {
                        if (cursor.moveToFirst()) {
                            // Update existing product count
                            val currentCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT))
                            val newCount = currentCount + (product.count.takeIf { it > 0 } ?: 1)
                            val updateValues = ContentValues().apply {
                                put(COLUMN_COUNT, newCount)
                            }
                            db.update(TABLE_PRODUCTS, updateValues, "$COLUMN_ID = ?", arrayOf(product.id))
                        } else {
                            // Insert new product
                            db.insert(TABLE_PRODUCTS, null, values)
                        }
                    }

                    db.setTransactionSuccessful()
                    true
                } finally {
                    db.endTransaction()
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e("CartDatabaseHelper", "Invalid product data: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("CartDatabaseHelper", "Error adding product", e)
            false
        }
    }


    // Get all products from the cart
    fun getAllProducts(): List<Cart> {
        val productList = mutableListOf<Cart>()

        try {
            readableDatabase.use { db ->
                db.query(
                    TABLE_PRODUCTS,
                    null,
                    null, null, null, null, null
                ).use { cursor ->
                    while (cursor.moveToNext()) {
                        try {
                            val product = Cart(
                                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                                price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                                quantity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                                count = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT))
                            )
                            productList.add(product)
                        } catch (e: Exception) {
                            Log.e("CartDatabaseHelper", "Error parsing product", e)
                            // Continue with next item instead of failing completely
                            continue
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CartDatabaseHelper", "Error getting products", e)
        }

        return productList
    }

    // Remove a product from the cart
    fun removeProduct(productId: String): Boolean {
        return try {
            writableDatabase.use { db ->
                val deletedRows = db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?", arrayOf(productId))
                deletedRows > 0
            }
        } catch (e: Exception) {
            Log.e("CartDatabaseHelper", "Error removing product", e)
            false
        }
    }

    // Clear all products in the cart
    fun clearCart(): Boolean {
        return try {
            writableDatabase.use { db ->
                db.delete(TABLE_PRODUCTS, null, null)
                true
            }
        } catch (e: Exception) {
            Log.e("CartDatabaseHelper", "Error clearing cart", e)
            false
        }
    }

    // Update product count
    fun updateProductCount(productId: String, newCount: Int): Boolean {
        return try {
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(COLUMN_COUNT, newCount)
                }
                val updatedRows = db.update(
                    TABLE_PRODUCTS,
                    values,
                    "$COLUMN_ID = ?",
                    arrayOf(productId)
                )
                updatedRows > 0
            }
        } catch (e: Exception) {
            Log.e("CartDatabaseHelper", "Error updating product count", e)
            false
        }
    }
}