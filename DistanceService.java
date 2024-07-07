package com.techweb.helloworld;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DistanceService {
    private static final Logger logger = LoggerFactory.getLogger(DistanceService.class);
    private GeoApiContext context;

    public DistanceService(String apiKey) {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public long getDistance(String origin, String destination) throws InterruptedException, ApiException, IOException {
        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
        DistanceMatrix result = req.origins(origin)
                .destinations(destination)
                .mode(TravelMode.DRIVING)
                .await();

        if (result.rows != null && result.rows.length > 0 &&
            result.rows[0].elements != null && result.rows[0].elements.length > 0 &&
            result.rows[0].elements[0].distance != null) {
            return result.rows[0].elements[0].distance.inMeters;
        } else {
            logger.error("Invalid response for {} to {}", origin, destination);
            logger.error("Response: {}", result);
            throw new IOException("Invalid API response for " + origin + " to " + destination);
        }
    }

    public void close() {
        context.shutdown();
    }
}
