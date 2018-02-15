package recommender;

import java.util.List;

public interface SimpleUserBasedKnn {

    /** Obtain all items in the dataset
     *
     * @return
     */
    public List<Integer> getItems();


    /** Obtain all users in the dataset
     *
     * @return
     */
    public List<Integer> getUsers();


    /** Get all items rated by user
     *
     * @param user
     * @return
     */
    public List<Integer> getRatedItemsBy(int user);


    /** Get a user's rating from any given item
     *
     * @param user
     * @param item
     * @return
     */
    public int getRatingForItem (int user, int item);

    /** Compute similarity between userA and userU
     *
     * @param userA
     * @param userU
     * @return
     */
    public Double getSimilarity(int userA, int userU);


    /** Get k neighbors of user
     *
     * @param user
     * @param k
     * @return
     */
    public List<Integer> getNeighbors(int user, int k);


    /** Recommend n items to user
     *
     * @param user
     * @param n
     * @return
     */
    public List<Integer> recommendedItems(int user, int n, int k);


}
