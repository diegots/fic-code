package com.company.recommender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

public class UserProfileIndex {


    private HashMap<Integer, List<UserData>> userProfileIndex; // HashMap <UserIds, List < Rated items > >
    private String path;
    private Set<Integer> itemsValue;


    public UserProfileIndex() {
        userProfileIndex = new HashMap();
        itemsValue = new HashSet<>();
    }

    public List<Integer> getItems() {
        //System.err.print("[UserProfileIndex:getItems]\n");
        List<Integer> res = new ArrayList();

        res.addAll(itemsValue);
        return res;
    }

    /** Obtain the ordered list of users in the dataset
     *
     * @return
     */
    public List<Integer> getUsers() {
        //System.err.print("[UserProfileIndex:getUsers]\n");
        List<Integer> res = new ArrayList();

        Set<Integer> userSet = userProfileIndex.keySet();
        res.addAll(userSet);
        res.sort(Integer::compareTo);

        return res;
    }


    /** getRatedItems gets all items rated by any given user.
     *
     * @param user
     * @return
     */
    public List<Integer> getRatedItemsBy(int user) {
        //System.err.print("[UserProfileIndex:getRatedItems]\n");
        List<Integer> res = new ArrayList();

        if (userProfileIndex.containsKey(user))
            for (UserData userData : userProfileIndex.get(user))
                res.add(userData.getItemId());
        else
            System.err.print("[UserProfileIndex:getRatedItems]--> No user found!\n");

        return res;
    }


    /** getRatingForItem returns the rating for an user and an item.
     *
     * @param item
     * @return
     */
    public int getRatingForItem (int user, int item) {
        //System.err.print("[UserProfileIndex:getRatingForItem]\n");

        if (userProfileIndex.containsKey(user))
            for (UserData userData : userProfileIndex.get(user))
                if (userData.getItemId() == item)
                    return userData.getRating();

        // A value of 0 is considered in this implementation for items not rated by user
        return 0;
    }


    /** buildIndex builds the User Profile Index with data read from path
     *
     * @param path
     */
    public void buildIndex (String path) {
        //System.err.print("[UserProfileIndex:buildIndex]\n");

        // Save path string
        this.path = path;

        // Apply readData function over each data rating
        Consumer<String> consumer = (x) -> readData(x);
        try {
            Path p = Paths.get(path);
            Files.lines(p).forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param dataLine
     */
    private void readData (String dataLine) {
        //System.err.print("[UserProfileIndex:data]\n");

        // Fields order in u.data
        // a[0] --> user id
        // a[1] --> item id
        // a[2] --> rating
        // a[3] --> timestamp
        int userField = 0;
        int itemField = 1;
        int ratingField = 2;
        int timestampField = 3;

        // Split regex
        String lineRegEx = "\t";

        // Split line to obtain it's fields
        String [] lineFields = dataLine.split(lineRegEx);

        // userid used as key in the HastSet
        Integer userFieldValue = Integer.decode(lineFields[userField]);

        // Obtain user's list from hashset. Create a new list if it's null
        List<UserData> userDataData = userProfileIndex.get(userFieldValue);
        if (userDataData == null) {
            userDataData = new ArrayList<>();
        }

        int itemFieldValue = Integer.decode(lineFields[itemField]);
        int ratingFieldValue = Integer.decode(lineFields[ratingField]);
        int timestampFieldValue = Integer.decode(lineFields[timestampField]);

//        System.err.println("Adding: " + userFieldValue + ":"
//                + itemFieldValue + ":"
//                + ratingFieldValue + ":"
//                + timestampFieldValue);

        UserData userData = new UserData(userFieldValue, itemFieldValue,
                ratingFieldValue, timestampFieldValue);

        userDataData.add(userData);

        userProfileIndex.put(userFieldValue, userDataData);

        // Save this item number. Used to get first and last items
        itemsValue.add(Integer.valueOf(itemFieldValue));

    }
}

