package elasticsearch.ecommerce.app.entities;

import java.util.Date;

// adhere to bean properties, so we can use jackson bean introspection
public class Product {

    private final String name;
    private final double price;
    private final String color;
    private final String material;
    private final String id;
    private final String productImageUrl;
    private final String brand;
    private final String brandLogoUrl;
    private final Date lastUpdated;
    private final int stock;
    private final int commission;

    public Product(String name, double price, String color, String material, String id, String productImageUrl, String brand, String brandLogoUrl,
                   Date lastUpdated, int stock, int commission) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.material = material;
        this.id = id;
        this.productImageUrl = productImageUrl;
        this.brand = brand;
        this.brandLogoUrl = brandLogoUrl;
        this.lastUpdated = lastUpdated;
        this.stock = stock;
        this.commission = commission;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public String getBrandLogoUrl() {
        return brandLogoUrl;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public int getStock() {
        return stock;
    }

    public int getCommission() {
        return commission;
    }

    public String getMaterial() {
        return material;
    }
}
