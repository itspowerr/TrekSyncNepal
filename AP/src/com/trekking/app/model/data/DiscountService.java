package com.trekking.app.model.data;

import com.trekking.app.model.Attraction;
import com.trekking.app.model.Booking;
import com.trekking.app.model.FestivalOffer;

import java.util.List;

public class DiscountService {

    private final FestivalOfferManager offerManager;

    public DiscountService() {
        this.offerManager = new FestivalOfferManager();
    }

    /**
     * Checks if a booking's start date falls within any active festival offer.
     * If it does, it updates the booking's total cost and discount description.
     * @param booking The booking to check and modify.
     * @param attraction The attraction being booked, to get the base price.
     */
    public void applyDiscountIfApplicable(Booking booking, Attraction attraction) {
        List<FestivalOffer> activeOffers = offerManager.loadOffers();
        double basePrice = attraction.getPrice();

        for (FestivalOffer offer : activeOffers) {
            if (offer.isOfferApplicable(booking.getStartDate())) {
                // Calculate the discounted price
                double discountAmount = basePrice * offer.getDiscountRate();
                double finalPrice = basePrice - discountAmount;

                // Update the booking object
                booking.setTotalCost(finalPrice);
                String discountLabel = String.format("%s (%.0f%%)", offer.getFestivalName(), offer.getDiscountRate() * 100);
                booking.setDiscountApplied(discountLabel);

                // Stop after applying the first applicable offer
                return;
            }
        }
    }
}