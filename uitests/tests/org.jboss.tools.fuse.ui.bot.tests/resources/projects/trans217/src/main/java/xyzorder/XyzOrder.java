
package xyzorder;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "custId",
    "priority",
    "orderId",
    "origin",
    "approvalCode",
    "lineItems"
})
public class XyzOrder {

    @JsonProperty("custId")
    private String custId;
    @JsonProperty("priority")
    private String priority;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("approvalCode")
    private String approvalCode;
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
     *     The origin
     */
    @JsonProperty("origin")
    public String getOrigin() {
        return origin;
    }

    /**
     * 
     * @param origin
     *     The origin
     */
    @JsonProperty("origin")
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * 
     * @return
     *     The approvalCode
     */
    @JsonProperty("approvalCode")
    public String getApprovalCode() {
        return approvalCode;
    }

    /**
     * 
     * @param approvalCode
     *     The approvalCode
     */
    @JsonProperty("approvalCode")
    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
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
