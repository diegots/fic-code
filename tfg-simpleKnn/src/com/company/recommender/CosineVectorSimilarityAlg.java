package com.company.recommender;

import java.util.List;

public class CosineVectorSimilarityAlg extends SimilarityAlg {

    public CosineVectorSimilarityAlg(UserProfileIndex userProfileIndex) {
        super(userProfileIndex);
    }

    @Override
    Double computeSimilarity(int userA, int userU) {

        Double numerator = 0.0;
        List<Integer> itemsList = userProfileIndex.getItems();

        // Numerator: all userA ratings * all userU ratings
        for (Integer item : itemsList) {
            numerator += userProfileIndex.getRatingForItem(userA, item) *
                    userProfileIndex.getRatingForItem(userU, item);
        }

        // userA denominator
        Double tmpA = 0.0;
        List<Integer> itemsRatedByuser = userProfileIndex.getRatedItemsBy(userA);
        for (Integer item : itemsRatedByuser) {
            tmpA += Math.pow(userProfileIndex.getRatingForItem(userA, item),2);
        }
        tmpA = Math.sqrt(tmpA);

        // userU denominator
        Double tmpU = 0.0;
        itemsRatedByuser = userProfileIndex.getRatedItemsBy(userU);
        for (Integer item : itemsRatedByuser) {
            tmpU += Math.pow(userProfileIndex.getRatingForItem(userU, item),2);
        }
        tmpU = Math.sqrt(tmpU);

//        System.err.println("CosineVectorSimilarityAlg:computeSimilarity: " + (numerator/(tmpA*tmpU)));
        return numerator / (tmpA * tmpU);
    }
}
