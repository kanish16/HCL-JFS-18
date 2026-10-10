package com.training.orderprocessor.service;

import com.training.orderprocessor.exception.InsufficientStockException;
import com.training.orderprocessor.exception.InvalidQuantityException;

public class OrderProcessor {

    public void processOrder(
            String product,
            String quantityText,
            int availableStock)
            throws InsufficientStockException {

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            // Chain the original parsing exception.
            throw new InvalidQuantityException(
                    "Quantity must be a valid integer: " + quantityText, e);
        }

        if (quantity <= 0) {
            throw new InvalidQuantityException(
                    "Quantity must be greater than zero.");
        }

        if (quantity > availableStock) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + product
                            + ". Requested: " + quantity
                            + ", available: " + availableStock);
        }

        System.out.println("Order successful: " + product
                + ", quantity: " + quantity);
    }

    public void audit(String product) {
        System.out.println("[AUDIT] Order processing finished for " + product);
    }
}