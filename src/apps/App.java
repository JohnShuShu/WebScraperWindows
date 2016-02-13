package apps;

import org.medhelp.User;
//import sun.rmi.rmic.iiop.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 * To change this template use File | Settings | File Templates.
 */
public class App {

    public String appName;
    public String appCreator;
    public String appPageLink;
    public String downloads;
    public String numberOfRatings;
    public String starRatings;
    public Map<Integer, String> histogram;
    public String releaseDate;
    public String lastUpdated;
    public String contentRating;
    public String inAppProducts;
    public String appPrice;
    public String appDollarPrice;
    public String amazonBestSellerRank;
    public String androidAppstoreRank;
    public String androidAppstoreCategoryRank;
    public String appSize;
    public boolean editorChoice;
    public boolean topDeveloper;

    public App(){

    }


    public App(String appName, String appCreator, String appPageLink, String downloads, String numberOfRatings,
               String starRatings, Map histogram, String releaseDate, String lastUpdated, String contentRating,
               String inAppProducts, String amazonBestSellerRank, String androidAppstoreRank, String appSize, boolean
                       editorChoice, boolean topDeveloper) {
        this.appName = appName;
        this.appCreator = appCreator;
        this.appPageLink = appPageLink;
        this.downloads = downloads;
        this.numberOfRatings = numberOfRatings;
        this.starRatings = starRatings;
        this.histogram = histogram;
        this.releaseDate = releaseDate;
        this.lastUpdated = lastUpdated;
        this.contentRating = contentRating;
        this.inAppProducts = inAppProducts;
        this.amazonBestSellerRank = amazonBestSellerRank;
        this.androidAppstoreRank = androidAppstoreRank;
        this.appSize = appSize;
        this.editorChoice = editorChoice;
        this.topDeveloper = topDeveloper;
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCreator() {
        return appCreator;
    }

    public void setAppCreator(String appCreator) {
        this.appCreator = appCreator;
    }

    public String getAppPageLink() {
        return appPageLink;
    }

    public void setAppPageLink(String appPageLink) {
        this.appPageLink = appPageLink;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(String numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public String getStarRatings() {
        return starRatings;
    }

    public void setStarRatings(String starRatings) {
        this.starRatings = starRatings;
    }

    public Map getHistogram() {
        return histogram;
    }

    public void setHistogram(Map histogram) {
        this.histogram = histogram;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public String getInAppProducts() {
        return inAppProducts;
    }

    public void setInAppProducts(String inAppProducts) {
        this.inAppProducts = inAppProducts;
    }

    public String getappPrice() {
        return appPrice;
    }

    public void setAppPrice(String appPrice) {
        this.appPrice = appPrice;
    }

    public String getAmazonBestSellerRank() {
        return amazonBestSellerRank;
    }

    public void setAmazonBestSellerRank(String amazonBestSellerRank) {
        this.amazonBestSellerRank = amazonBestSellerRank;
    }

    public String getAndroidAppstoreRank() {
        return androidAppstoreRank;
    }

    public void setAndroidAppstoreRank(String androidAppstoreRank) {
        this.androidAppstoreRank = androidAppstoreRank;
    }

    public String getAndroidAppstoreCategoryRank() {
        return androidAppstoreCategoryRank;
    }

    public void setAndroidAppstoreCategoryRank(String androidAppstoreCategoryRank) {
        this.androidAppstoreCategoryRank = androidAppstoreCategoryRank;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }

    public boolean isEditorChoice() {
        return editorChoice;
    }

    public void setEditorChoice(boolean editorChoice) {
        this.editorChoice = editorChoice;
    }

    public boolean isTopDeveloper() {
        return topDeveloper;
    }

    public void setTopDeveloper(boolean topDeveloper) {
        this.topDeveloper = topDeveloper;
    }

    public void setAppDollarPrice(String appDollarPrice) {
        this.appDollarPrice = appDollarPrice;
    }

    public String getappDollarPrice() {
        return appDollarPrice;
    }


    @Override
    public String toString() {
        return "App{" +
                "appName='" + appName + '\'' +
                ", appCreator='" + appCreator + '\'' +
                ", appPageLink='" + appPageLink + '\'' +
                ", downloads='" + downloads + '\'' +
                ", numberOfRatings='" + numberOfRatings + '\'' +
                ", starRatings='" + starRatings + '\'' +
                ", histogram=" + histogram +
                ", releaseDate='" + releaseDate + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", contentRating='" + contentRating + '\'' +
                ", inAppProducts='" + inAppProducts + '\'' +
                ", amazonBestSellerRank='" + amazonBestSellerRank + '\'' +
                ", androidAppstoreRank='" + androidAppstoreRank + '\'' +
                ", appSize='" + appSize + '\'' +
                ", editorChoice=" + editorChoice +
                ", topDeveloper=" + topDeveloper +
                '}';
    }

    public String printHashMap(){
        String histogramString="";

        for (Map.Entry<Integer, String> entry : histogram.entrySet() ) {
            String value = entry.getValue();
            if(entry.getValue().trim().length()<1){
                value = "0";
            }
            histogramString = value + ", " + histogramString;
        }

//        Set<Map.Entry<Integer,String>> hashSet = histogram.entrySet();
//
//        for(Map.Entry entry:hashSet ) {
//
//            histogramString = histogramString + ", " + entry.getValue();
//        }

        return  histogramString;
    }

    public String printToFile() {
        String fileText = "";

        fileText = fileText +
                  appName.replace(",","") + ", " +
                  appCreator.replace(",","") + ", " +
                  numberOfRatings.replace(",","") + ", " +
                  starRatings + ", " +
                  printHashMap() + ", " +
                  releaseDate + ", " +
                  contentRating + ", " +
                  inAppProducts + ", " +
//                  appPrice.replace(",",".") + ", " +
                  appDollarPrice.replace(",",".") + ", " +
                  amazonBestSellerRank + ", " +
                  androidAppstoreRank + ", " +
                  androidAppstoreCategoryRank + ", " +
                  appSize + ", " +
                  appPageLink + ", " + "\n";

        return fileText;
    }

//    public String printToFile() {
//        String fileText = "";
//
//        fileText = fileText + appName + ", " +
//                appLink + ", " +
//                dateCreated+ ", " +
//                appCreator + ", " +
//                threadCreatorLink+  ", " +
//                threadNumber+  ", " +
//                threadPageNumber+  ", " +
//                printCommentors()+ ", " +
//                commentsNumber+  "\n";
//
//        return fileText;
//
//    }

//    public String printCommentors(){
//        String commentorsList = "";
//
//        try{
//            if (commentors == null){
//                commentorsList= "";
//            } else {
//                for(User commentor: commentors){
//                    commentorsList = commentorsList + ", "+  commentor.userName;
//                }
//                return commentorsList;
//            }
//        } catch (Exception e) {
//            System.err.println("Caught Exception: " + e.getMessage());
//        }
//
//        return commentorsList;
//    }
//

    //    @Override
//    public String toString() {
//        return "Thread{" +
//                "appName='" + appName + '\'' +
//                ", appLink='" + appLink + '\'' +
//                ", dateCreated=" + dateCreated +
//                ", appCreator='" + appCreator + '\'' +
//                ", threadNumber=" + threadNumber +
//                ", threadPageNumber=" + threadPageNumber +
//                ", commentsNumber=" + commentsNumber +
//                ", threadCreatorLink='" + threadCreatorLink + '\'' +
//                ", threadCommentor='" + commentors + '\'' +
//                '}';
//    }
//
//    public String printToFileQuotes() {
//        String fileText = "";
//
//        fileText = fileText + "\"" + appName + "\"" + ", " +
//                "\"" + appLink + "\"" + ", " +
//                "\"" + dateCreated+ "\"" + ", " +
//                "\"" + appCreator + "\"" + ", " +
//                "\"" + threadCreatorLink+ "\"" + ", " +
//                "\"" + threadNumber+ "\"" + ", " +
//                "\"" + threadPageNumber+ "\"" + ", " +
//                "\"" + printCommentors()+ "\"" + ", " +
//                "\"" + commentsNumber+ "\"" + ", " + "\n\n";
//
//        return fileText;
//
//    }
//
//



}
