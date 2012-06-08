/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.util;

import be.kuleuven.socialmap.database.Database;
import be.kuleuven.socialmap.database.DatabaseFactory;
import be.kuleuven.socialmap.exceptions.SocialMapException;
import be.kuleuven.socialmap.model.FlickrPhoto;
import be.kuleuven.socialmap.model.FoursquareVenue;
import be.kuleuven.socialmap.model.InstagramPhoto;
import be.kuleuven.socialmap.model.MapReduceResult;
import be.kuleuven.socialmap.model.Tweet;
import java.util.List;

/**
 * A singleton class that helps handling the data.
 *
 * @author Jasper Moeys
 */
public class DataHelper {
    
    private static DataHelper me = new DataHelper();
    private List<Tweet> tweets;
    private List<FlickrPhoto> flickrphotos;
    private List<FoursquareVenue> foursquarevenues;
    private List<InstagramPhoto> instagramphotos;
    private List<MapReduceResult> mapreduceTw;
    private List<MapReduceResult> mapreduceFl;
    private List<MapReduceResult> mapreduceFs;
    private List<MapReduceResult> mapreduceIn;
    private Database<FlickrPhoto> databaseFl;
    private Database<FoursquareVenue> databaseFs;
    private Database<Tweet> databaseTw;
    private Database<InstagramPhoto> databaseIn;
    private Database<MapReduceResult> mrDatabaseFl;
    private Database<MapReduceResult> mrDatabaseFs;
    private Database<MapReduceResult> mrDatabaseTw;
    private Database<MapReduceResult> mrDatabaseIn;
    
    private DataHelper(){
        
    }
    
    /**
     * Acquire a singleton instance of the DataHelper class.
     * 
     * @return the DataHelper instance
     */
    public static DataHelper getInstance(){
        return DataHelper.me;
    }
    
    public Database<FlickrPhoto> getFlickrDatabase() throws SocialMapException{
        if(this.databaseFl == null){
            this.databaseFl = DatabaseFactory.getDatabase(FlickrPhoto.class);
        }
        return this.databaseFl;
    }
    
    public Database<FoursquareVenue> getFoursquareDatabase() throws SocialMapException{
        if(this.databaseFs == null){
            this.databaseFs = DatabaseFactory.getDatabase(FoursquareVenue.class);
        }
        return this.databaseFs;
    }
    
    public Database<Tweet> getTwitterDatabase() throws SocialMapException{
        if(this.databaseTw == null){
            this.databaseTw = DatabaseFactory.getDatabase(Tweet.class);
        }
        return this.databaseTw;
    }
    
    public Database<InstagramPhoto> getInstagramDatabase() throws SocialMapException{
        if(this.databaseIn == null){
            this.databaseIn = DatabaseFactory.getDatabase(InstagramPhoto.class);
        }
        return this.databaseIn;
    }
    
    public Database<MapReduceResult> getMapReducedFlickrDatabase() throws SocialMapException{
        if(this.mrDatabaseFl == null){
            this.mrDatabaseFl = DatabaseFactory.getDatabase(MapReduceResult.class, FlickrPhoto.class);
        }
        return this.mrDatabaseFl;
    }
    
    public Database<MapReduceResult> getMapReducedFoursquareDatabase() throws SocialMapException{
        if(this.mrDatabaseFs == null){
            this.mrDatabaseFs = DatabaseFactory.getDatabase(MapReduceResult.class, FoursquareVenue.class);
        }
        return this.mrDatabaseFs;
    }
    
    public Database<MapReduceResult> getMapReducedTwitterDatabase() throws SocialMapException{
        if(this.mrDatabaseTw == null){
            this.mrDatabaseTw = DatabaseFactory.getDatabase(MapReduceResult.class, Tweet.class);
        }
        return this.mrDatabaseTw;
    }
    
    public Database<MapReduceResult> getMapReducedInstagramDatabase() throws SocialMapException{
        if(this.mrDatabaseIn == null){
            this.mrDatabaseIn = DatabaseFactory.getDatabase(MapReduceResult.class, InstagramPhoto.class);
        }
        return this.mrDatabaseIn;
    }

    public List<FlickrPhoto> getFlickrphotos() throws SocialMapException {
        if(this.flickrphotos == null){
            this.flickrphotos = getFlickrDatabase().getAll();
        }
        return this.flickrphotos;
    }

    public List<FoursquareVenue> getFoursquareVenues() throws SocialMapException {
        if(this.foursquarevenues == null){
            this.foursquarevenues = getFoursquareDatabase().getAll();
        }
        return this.foursquarevenues;
    }

    public List<MapReduceResult> getMapReducedFlickrPhotos() throws SocialMapException {
        if(this.mapreduceFl == null){
            this.mapreduceFl = getMapReducedFlickrDatabase().getAll();
        }
        return this.mapreduceFl;
    }

    public List<MapReduceResult> getMapReducedFoursquareVenues() throws SocialMapException {
        if(this.mapreduceFs == null){
            this.mapreduceFs = getMapReducedFoursquareDatabase().getAll();
        }
        return this.mapreduceFs;
    }

    public List<MapReduceResult> getMapReducedTweets() throws SocialMapException {
        if(this.mapreduceTw == null){
            this.mapreduceTw = getMapReducedTwitterDatabase().getAll();
        }
        return this.mapreduceTw;
    }

    public List<Tweet> getTweets() throws SocialMapException {
        if(this.tweets == null){
            this.tweets = getTwitterDatabase().getAll();
        }
        return this.tweets;
    }
    
    public List<InstagramPhoto> getInstagramPhotos() throws SocialMapException {
        if(this.instagramphotos == null){
            this.instagramphotos = getInstagramDatabase().getAll();
        }
        return this.instagramphotos;
    }
    
    public List<MapReduceResult> getMapReducedInstagramPhotos() throws SocialMapException {
        if(this.mapreduceIn == null){
            this.mapreduceIn = getMapReducedInstagramDatabase().getAll();
        }
        return this.mapreduceIn;
    }
    
    public <T> Database<T> getDatabase(Class<T> type) throws SocialMapException{
        Database<T> db = null;
        if(type.equals(Tweet.class))
            db = (Database<T>) getTwitterDatabase();
        else if(type.equals(FoursquareVenue.class))
            db = (Database<T>) getFoursquareDatabase();
        else if(type.equals(InstagramPhoto.class))
            db = (Database<T>) getInstagramDatabase();
        else if(type.equals(FlickrPhoto.class))
            db =  (Database<T>) getFlickrDatabase();
        
        return db;
    }
    
    public <T> List<T> getList(Class<T> type) throws SocialMapException{
        List<T> list = null;
        if(type.equals(Tweet.class))
            list = (List<T>) getTweets();
        else if(type.equals(FoursquareVenue.class))
            list = (List<T>) getFoursquareVenues();
        else if(type.equals(InstagramPhoto.class))
            list = (List<T>) getInstagramPhotos();
        else if(type.equals(FlickrPhoto.class))
            list =  (List<T>) getFlickrphotos();
        
        return list;
    }
        
}
