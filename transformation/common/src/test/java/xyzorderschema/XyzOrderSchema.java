
package xyzorderschema;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * XYZOrder
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "custId",
    "priority",
    "orderId",
    "lineItems"
})
public class XyzOrderSchema {

    @JsonProperty("custId")
    private String custId;
    @JsonProperty("priority")
    private String priority;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("lineItems")
    private List<LineItem> lineItems = new ArrayList<LineItem>();

    /**
     * 
     * @return
     *     The custId
     */
    @JsonProperty("custId")
    public String getCustId() {
        return custId;
    }

    /**
     * 
     * @param custId
     *     The custId
     */
    @JsonProperty("custId")
    public void setCustId(String custId) {
        this.custId = custId;
    }

    /**
     * 
     * @return
     *     The priority
     */
    @JsonProperty("priority")
    public String getPriority() {
        return priority;
    }

    /**
     * 
     * @param priority
     *     The priority
     */
    @JsonProperty("priority")
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * 
     * @return
     *     The orderId
     */
    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }

    /**
     * 
     * @param orderId
     *     The orderId
     */
    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 
     * @return
     *     The lineItems
     */
    @JsonProperty("lineItems")
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    /**
     * 
     * @param lineItems
     *     The lineItems
     */
    @JsonProperty("lineItems")
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

}
