package edu.tridenttech.cpt237.cafe.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import edu.tridenttech.cpt237.cafe.model.MenuItem;
import edu.tridenttech.cpt237.cafe.model.CafeOrder;

public class WebCafe {
    private final String baseURL;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public WebCafe(String baseURL) {
        this.baseURL = baseURL;
    }

    private String sendGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void sendPost(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(new HttpRequest.BodyPublisher() {
                    @Override
                    public long contentLength() {
                        return 0;
                    }

                    @Override
                    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {

                    }
                })
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void sendDelete(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    
    //Gets the items on the menu limited to a particular category.
    public List<MenuItem> getMenuItemsByType(String type) {
        ArrayList<MenuItem> itemList = new ArrayList<MenuItem>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor(MenuItem.class);
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/menuItems");
            if (type.length() > 0) {
                path.append("?type=").append(UriUtils.encodePath(type, "UTF-8"));
            }
            String response = sendGet(path.toString());
            MappingIterator<MenuItem> iterator = reader.readValues(response);
            while (iterator.hasNext()) {
                MenuItem item = iterator.next();
                itemList.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return itemList;
    }

    public List<CafeOrder> getOrderList() {
        ArrayList<CafeOrder> orderList = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor(CafeOrder.class);
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/orders");
            String response = sendGet(path.toString());
            MappingIterator<CafeOrder> iterator = reader.readValues(response);
            while (iterator.hasNext()) {
                CafeOrder order = iterator.next();
                orderList.add(order);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orderList;
    }

    //Adds a line to the specified order.
    public void addLineItem(int orderId, String itemName, int numOrdered) {
        CafeOrder order = findPendingOrder(orderId);
        MenuItem item = findItemByName(itemName);
        if (order != null && item != null) {
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/add");
            path.append("?id=").append(order.getOrderId());
            path.append("&name=").append(UriUtils.encodePath(item.getName(), "UTF-8"));
            path.append("&num=").append(numOrdered);
            try {
                sendPost(path.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MenuItem findItemByName(String itemName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor(MenuItem.class);
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/menuItem");
            path.append("?name=").append(UriUtils.encodePath(itemName, "UTF-8"));
            String response = sendGet(path.toString());
            return reader.readValue(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Get the categories of items offered by the cafe.
    public List<String> getMenuItemTypes() {
        ArrayList<String> menuItemTypes = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor(String.class);
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/menuItemTypes");
            String response = sendGet(path.toString());
            MappingIterator<String> iterator = reader.readValues(response);
            while (iterator.hasNext()) {
                String menuItemType = iterator.next();
                menuItemTypes.add(menuItemType);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return menuItemTypes;
    }
    
    //Get all of the items on the menu.
    public List<MenuItem> getAllMenuItems() {
        return getMenuItemsByType("");
    }

    public MenuItem getMenuItemByName(String itemName) {
        return findItemByName(itemName);
    }

	//Find the order corresponding to the provided id and return this Order
	//to the caller.
    public CafeOrder getOrderById(int id) {
        return getOrderById(id, null);
    }


    private CafeOrder getOrderById(int id, Boolean pending) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor(CafeOrder.class);
            StringBuilder path = new StringBuilder(baseURL);
            path.append("/order");
            path.append("?id=").append(id);
            if (pending != null) {
                path.append("&pending=").append(pending);
            }
            String response = sendGet(path.toString());
            return reader.readValue(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*
     * Creates a new Order to hold items added by the user.  The returned order
     * id is to be used when adding to the order, placing the order or
     * canceling the order.
     */
    public int startOrder() {
        StringBuilder path = new StringBuilder(baseURL);
        path.append("/startOrder");
        try {
            return Integer.parseInt(sendGet(path.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

     // Confirm that the order is being placed.
    public void placeOrder(int id) {
        CafeOrder order = findPendingOrder(id);
        if (order != null) {
            try {
                StringBuilder path = new StringBuilder(baseURL);
                path.append("/placeOrder");
                path.append("?id=").append(id);
                sendPost(path.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Cancel the order.
    public void cancelOrder(int id) {
        CafeOrder order = findPendingOrder(id);
        if (order != null) {
            try {
                StringBuilder path = new StringBuilder(baseURL);
                path.append("/cancelOrder");
                path.append("?id=").append(id);
                sendDelete(path.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CafeOrder findPendingOrder(int id) {
        return getOrderById(id, true);
    }

    public CafeOrder findPlacedOrder(int id) {
        return getOrderById(id, false);
    }
    
    // DO NOT USE -- replace functionality with code in the 'main' class.
    public void displayOrders() {
        /*
		for (Order order : placedOrderList) {
			System.out.printf("Order # %d%n", order.getOrderId());
			for (OrderItem item : order.getOrderedItems()) {
				System.out.println(item);
			}
		}
        */
    }


    public static void main(String[] args) {
        WebCafe cafe = new WebCafe("http://localhost:8080");
        List<MenuItem> items = cafe.getMenuItemsByType("bakery");
        for (MenuItem item : items) {
            System.out.printf("%-20s%5.2f\n", item.getName(), item.getBaseCost());
        }
        System.out.println(cafe.getMenuItemByName("Latte"));
        System.out.println(cafe.getMenuItemByName("Corn Muffin"));
    }

}
