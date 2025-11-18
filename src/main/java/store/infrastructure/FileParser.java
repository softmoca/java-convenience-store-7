package store.infrastructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.domain.Promotion;

public class FileParser {

    public List<Promotion> parsePromotions(String content) {
        List<Promotion> promotions = new ArrayList<>();
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            promotions.add(new Promotion(
                    parts[0],  // name
                    Integer.parseInt(parts[1]),  // buy
                    Integer.parseInt(parts[2]),  // get
                    LocalDate.parse(parts[3]),   // startDate
                    LocalDate.parse(parts[4])    // endDate
            ));
        }

        return promotions;
    }
}
