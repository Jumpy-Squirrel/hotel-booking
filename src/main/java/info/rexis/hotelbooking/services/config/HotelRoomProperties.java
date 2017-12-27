package info.rexis.hotelbooking.services.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ConfigurationProperties("hotelinfo")
public class HotelRoomProperties {
    private List<RoomTypeInfo> roomtypes;

    @Data
    public static class RoomTypeInfo {
        private String name;
        private String value;
        private String description;
        private String infolink;
        private float price1;
        private float price2;
        private float price3;

        public Map<String, String> toMap() {
            Map<String, String> info = new HashMap<>();
            info.put("name", name);
            info.put("value", value);
            info.put("description", description);
            info.put("infolink", infolink);
            // todo locale aware formatting and currency
            info.put("price1", Float.toString(price1));
            info.put("price2", Float.toString(price2));
            info.put("price3", Float.toString(price3));
            return info;
        }
    }

    public List<Map<String, String>> toListOfMaps() {
        return roomtypes.stream()
                .map(RoomTypeInfo::toMap)
                .collect(Collectors.toList());
    }
}
