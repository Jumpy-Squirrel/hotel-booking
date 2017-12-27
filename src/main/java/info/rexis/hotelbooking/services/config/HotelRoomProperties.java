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
    private String recipient;
    private String stichwort;

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
            info.put("price1", String.format("%8.2f€", price1));
            info.put("price2", String.format("%8.2f€", price2));
            info.put("price3", String.format("%8.2f€", price3));
            return info;
        }
    }

    public List<Map<String, String>> toListOfMaps() {
        return roomtypes.stream()
                .map(RoomTypeInfo::toMap)
                .collect(Collectors.toList());
    }

    public RoomTypeInfo byRoomTypePosition(int pos) {
        return roomtypes.get(pos - 1);
    }
}
