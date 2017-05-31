
package xyzorder;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "itemId",
    "amount",
    "cost"
})
public class LineItem {

    @JsonProperty("itemId")
    private String itemId;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("cost")
    private double cost;

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
     *     The amount
     */
    @JsonProperty("amount")
    public int getAmount() {
        return amount;
    }

    /**
     * 
     * @param amount
     *     The amount
     */
    @JsonProperty("amount")
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * 
     * @return
     *     The cost
     */
    @JsonProperty("cost")
    public double getCost() {
        return cost;
    }

    /**
     * 
     * @param cost
     *     The cost
     */
    @JsonProperty("cost")
    public void setCost(double cost) {
        this.cost = cost;
    }

}
