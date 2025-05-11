package org.example;

import org.example.io.JsonLoader;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.service.*;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java -jar app.jar <orders.json> <paymentmethods.json>");
            return;
        }

        String ordersPath = args[0];
        String paymentMethodsPath = args[1];

        List<Order> orders = JsonLoader.loadOrders(ordersPath);
        List<PaymentMethod> paymentMethods = JsonLoader.loadPaymentMethods(paymentMethodsPath);

        System.out.println("Loaded " + orders.size() + " orders and " + paymentMethods.size() + " payment methods.\n");

        PromotionProcessor promotionProcessor = new PromotionProcessorImpl();
        Bill bill = new PromotionBill(orders, paymentMethods, promotionProcessor);
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        System.out.println(calculator.calculate(bill));
    }
}