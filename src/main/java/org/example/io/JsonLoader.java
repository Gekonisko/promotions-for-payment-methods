package org.example.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.io.File;
import java.util.List;

public class JsonLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Order> loadOrders(String path) throws Exception {
        return mapper.readValue(new File(path), new TypeReference<>() {});
    }

    public static List<PaymentMethod> loadPaymentMethods(String path) throws Exception {
        return mapper.readValue(new File(path), new TypeReference<>() {});
    }
}
