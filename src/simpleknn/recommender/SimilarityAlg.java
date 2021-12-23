package simpleknn.recommender;

abstract public class SimilarityAlg {

    protected final UserProfileIndex userProfileIndex;

    public SimilarityAlg(UserProfileIndex userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }

    abstract Double computeSimilarity (int userA, int userU);

}