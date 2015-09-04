
package abcorderschema;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "ABCOrder"
})
public class AbcOrderSchema {

    @JsonProperty("ABCOrder")
    private abcorderschema.ABCOrder ABCOrder;

    /**
     * 
     * @return
     *     The ABCOrder
     */
    @JsonProperty("ABCOrder")
    public abcorderschema.ABCOrder getABCOrder() {
        return ABCOrder;
    }

    /**
     * 
     * @param ABCOrder
     *     The ABCOrder
     */
    @JsonProperty("ABCOrder")
    public void setABCOrder(abcorderschema.ABCOrder ABCOrder) {
        this.ABCOrder = ABCOrder;
    }

}
