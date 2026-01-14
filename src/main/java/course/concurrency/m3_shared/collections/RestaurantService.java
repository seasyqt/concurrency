package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, AtomicInteger> stat = new ConcurrentHashMap<>() {{
        put("A", new AtomicInteger(0));
        put("B", new AtomicInteger(0));
        put("C", new AtomicInteger(0));
    }};

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.get(restaurantName).getAndIncrement();
    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
                .map(element -> element.getKey() + " - " + element.getValue())
                .collect(Collectors.toSet());
    }
}
