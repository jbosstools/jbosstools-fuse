
package abcorderschema;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "itemId",
    "price",
    "quantity"
})
public class Item {

    @JsonProperty("itemId")
    private String itemId;
    @JsonProperty("price")
    private String price;
    @JsonProperty("quantity")
    private String quantity;

    /**
     * 
     * @return
     *     The itemId
     */
    @JsonProperty("itemId")
    public String getItemId() {
        return itemId;
    }

    /**
     * 
     * @param itemId
     *     The itemId
     */
    @JsonProperty("itemId")
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * 
     * @return
     *     The price
     */
    @JsonProperty("price")
    public String getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     *     The price
     */
    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * 
     * @return
     *     The quantity
     */
    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    /**
     * 
     * @param quantity
     *     The quantity
     */
    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
