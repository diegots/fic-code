package tfg;


public class SimilaridadYVecino {

    private final Integer userId;
    private final Integer similarity;


    public SimilaridadYVecino(Integer similarity, Integer userId) {
        this.userId = userId;
        this.similarity = similarity;
    }


    public int getUserId() {
        return userId;
    }


    public int getSimilarity() {
        return similarity;
    }

    @Override
    public int hashCode() {
        return userId.hashCode() * 163 + similarity.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || getClass() != o.getClass()) return false;
        SimilaridadYVecino similaridadYVecino = (SimilaridadYVecino) o;
        return getUserId() == similaridadYVecino.getUserId() &&
                getSimilarity() == similaridadYVecino.getSimilarity();
    }
}
