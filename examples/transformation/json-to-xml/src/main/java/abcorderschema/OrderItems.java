
package abcorderschema;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "item"
})
public class OrderItems {

    @JsonProperty("item")
    private List<Item> item = new ArrayList<Item>();

    /**
     * 
     * @return
     *     The item
     */
    @JsonProperty("item")
    public List<Item> getItem() {
        return item;
    }

    /**
     * 
     * @param item
     *     The item
     */
    @JsonProperty("item")
    public void setItem(List<Item> item) {
        this.item = item;
    }

}
