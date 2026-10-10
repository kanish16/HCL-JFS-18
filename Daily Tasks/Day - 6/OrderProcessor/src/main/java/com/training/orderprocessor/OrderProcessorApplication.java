package com.training.orderprocessor;

import com.training.orderprocessor.exception.InsufficientStockException;
import com.training.orderprocessor.exception.InvalidQuantityException;
import com.training.orderprocessor.service.OrderProcessor;

public class OrderProcessorApplication {

    public static void main(String[] args) {

        OrderProcessor processor = new OrderProcessor();

        // Scenario 1: Invalid quantity with a chained cause.
        try {
            processor.processOrder("Keyboard", "abc", 10);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.out.println("\n[ERROR 1] " + e);
            e.printStackTrace();
        } finally {
            processor.audit("Keyboard");
        }

        // Scenario 2: Requested quantity exceeds available stock.
        try {
            processor.processOrder("Mouse", "11", 3);
        } catch (InsufficientStockException | InvalidQuantityException e) {
            System.out.println("\n[ERROR 2] " + e);
            e.printStackTrace();
        } finally {
            processor.audit("Mouse");
        }
    }
}

