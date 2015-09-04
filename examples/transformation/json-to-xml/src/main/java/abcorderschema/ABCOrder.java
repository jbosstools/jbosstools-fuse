
package abcorderschema;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "header",
    "order-items"
})
public class ABCOrder {

    @JsonProperty("header")
    private Header header;
    @JsonProperty("order-items")
    private OrderItems orderItems;

    /**
     * 
     * @return
     *     The header
     */
    @JsonProperty("header")
    public Header getHeader() {
        return header;
    }

    /**
     * 
     * @param header
     *     The header
     */
    @JsonProperty("header")
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * 
     * @return
     *     The orderItems
     */
    @JsonProperty("order-items")
    public OrderItems getOrderItems() {
        return orderItems;
    }

    /**
     * 
     * @param orderItems
     *     The order-items
     */
    @JsonProperty("order-items")
    public void setOrderItems(OrderItems orderItems) {
        this.orderItems = orderItems;
    }

}
