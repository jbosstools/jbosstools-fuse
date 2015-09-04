
package abcorderschema;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "status",
    "customer-num",
    "order-num"
})
public class Header {

    @JsonProperty("status")
    private String status;
    @JsonProperty("customer-num")
    private String customerNum;
    @JsonProperty("order-num")
    private String orderNum;

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The customerNum
     */
    @JsonProperty("customer-num")
    public String getCustomerNum() {
        return customerNum;
    }

    /**
     * 
     * @param customerNum
     *     The customer-num
     */
    @JsonProperty("customer-num")
    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }

    /**
     * 
     * @return
     *     The orderNum
     */
    @JsonProperty("order-num")
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 
     * @param orderNum
     *     The order-num
     */
    @JsonProperty("order-num")
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

}
