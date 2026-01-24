package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order order = currentOrders.compute(orderId, (id, o) -> o.withPaymentInfo(paymentInfo));
        if (order.checkStatus()) {
            deliver(order);
        }
    }

    public void setPacked(long orderId) {
        Order order = currentOrders.compute(orderId, (id, o) -> o.packed());
        if (order.checkStatus()) {
            deliver(order);
        }
    }

    private void deliver(Order order) {
        /* ... */
        currentOrders.compute(order.getId(), (id, o) -> o.withStatus(Order.Status.DELIVERED));
    }

    public synchronized boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
