package apps;

import org.medhelp.*;
import org.medhelp.Thread;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: johnshu
 */

public class AppStoreScraper {


    public static void main(String[] args)  throws Exception {


        WebDriver chromeDriver = new FirefoxDriver();
        //WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String fileText = "";

        // different page numbers to parse
        Integer pageNumber =1;

        // Number of threads parsed
        Integer threadNumber=0;

        // WebElement from Selenium
        List<WebElement> newSubjectElement;

        // List of threads analyzed and stored
        List<App> appArrayList = new ArrayList<App>();

        // List of thread commentors. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> threadCommentors;
        List<User> threadCommentors = new ArrayList<User>();


        // List of all users both commentors and thread creators. Use Hash set for anything dealing with Users instead to avoid duplicates
        // List<User> userList = new ArrayList<User>();
        List<User> userList = new ArrayList<User>();

        System.out.println("Gathering data ...");

        //************************************************* START SCRAPPING FORUM FOR THE THREADS *************************************************//

//         do{
        // pageNumber is automatically incremented at the end of this loop so next page can be crawled. This happens until pages have
        // no more data i.e. threads.
        chromeDriver.navigate().to("https://play.google.com/store/apps/collection/promotion_300085a_most_popular_games");
//            http://www.medhelp.org/forums/Relationships/show/78?page=390
//            http://www.medhelp.org/forums/Divorce--Breakups/show/155?page=58
//            http://www.medhelp.org/forums/High-Blood-Pressure---Hypertension/show/1222?page=

        // Check if this page has any data
        //newSubjectElement = chromeDriver.findElements(By.className("card-click-target"));


        // Start looking through the threads
        List<WebElement> appList =  chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'card-content')]"));
        System.out.println(appList.size());

        System.out.println("Page Link,App Name,Company,Av. Rating,No. Ratings,Editors' Choice,Top Developer,5 STARS,4 STARS,3 STARS,2 STARS,1 STAR,Content Rating,In-app Products/item");
        WebDriver appDriver = new FirefoxDriver();


        for ( int i=0; i < appList.size(); i++){
//            for ( int i=0; i < 3; i++){


            // Extract Thread Names

            try{

                String Url = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", appList.get(i));
                //System.out.println(Url);

                // Instantiate a new App
                App app = new App();

                // Extract app link
                Pattern appLinkPattern = Pattern.compile("<a class=\"card-click-target\" href=\"(.+?)\"");
                Matcher matcher = appLinkPattern.matcher(Url);
                matcher.find();
                String appLink = matcher.group(1);
                appLink = "https://play.google.com" + appLink;
                System.out.print(appLink + ", ");
                app.setAppPageLink(appLink);


                appDriver.navigate().to(appLink);

                List<WebElement> appData =  appDriver.findElements(By.xpath("//div[contains(@class, 'main-content')]"));

                String appInfo = (String)((JavascriptExecutor)appDriver).executeScript("return arguments[0].innerHTML;", appData.get(0));
                //System.out.println(appInfo);


                // Extract App Name
                Pattern appNamePattern = Pattern.compile("<div class=\"id-app-title\" tabindex=\"0\">(.+?)<\\/div>");
                matcher = appNamePattern.matcher(appInfo);
                matcher.find();
                String appName = matcher.group(1);
                System.out.print(appName + ", ");
                app.setAppName(appName);

                // Extract app Creator Name
                Pattern appCreatorPattern = Pattern.compile("<span itemprop=\"name\">(.+?)<\\/span>");
                matcher = appCreatorPattern.matcher(appInfo);
                matcher.find();
                String appCreatorName = matcher.group(1);
                System.out.print(appCreatorName + ", ");
                app.setAppCreator(appCreatorName);


                // Extract App Ratings
                Pattern appStarRatingsPattern = Pattern.compile("<div class=\"tiny-star star-rating-non-editable-container\" aria-label=\" Rated (.+?) stars out of five stars \">");
                matcher = appStarRatingsPattern.matcher(appInfo);
                matcher.find();
                String appStarRatings = matcher.group(1);
                System.out.print(appStarRatings + ", ");
                app.setStarRatings(appStarRatings);

                // Extract number of app ratings
                Pattern appRatingsPattern = Pattern.compile("<span class=\"rating-count\" aria-label=\" .+? ratings \">(.+?)<\\/span>");
                matcher = appRatingsPattern.matcher(appInfo);
                matcher.find();
                String appRatings = matcher.group(1);
                System.out.print(appRatings.replace(",","") + ", ");
                app.setDownloads(appRatings);


                // Extract App Top developer / Editor's Choice
                Pattern appQualityPattern = Pattern.compile("<span class=\"badge-title\">(.+?)<\\/span>");
                matcher = appQualityPattern.matcher(appInfo);
                while(matcher.find()){
//                    System.out.print(matcher.group(1) + ", ");
                    if(matcher.group(1).equals("Editors' Choice")){
                        app.setEditorChoice(true);

                    } else if (matcher.group(1).equals("Top Developer")){
                        app.setTopDeveloper(true);

                    }
                }

                String editorsChoice  = (app.editorChoice == true)? "Editors' Choice, " : ", ";
                String topDeveloper  = (app.topDeveloper == true)? "Top Developer, " : ", ";

                System.out.print(editorsChoice + topDeveloper);


                // Extract app stars histogram
                Pattern appHistogramPattern = Pattern.compile("<span class=\"bar-number\" aria-label=\" .+? \">(.+?)<\\/span>");
                matcher = appHistogramPattern.matcher(appInfo);
                HashMap stars = new HashMap();
                Integer star = 5;
                while(matcher.find()){
                    System.out.print(star + " stars :" + matcher.group(1).replace(",","") + ", ");
                    stars.put(star,matcher.group(1));
                    star--;
                }

                app.setHistogram(stars);


                // Extract contentRating
                Pattern appContentRatingPattern = Pattern.compile("<div class=\"content\" itemprop=\"contentRating\">(.+?)<\\/div>");
                matcher = appContentRatingPattern.matcher(appInfo);
                matcher.find();
                String contentRating = matcher.group(1);
                System.out.print(contentRating + ", ");
                app.setContentRating(contentRating);

                // Extract inAppProducts
                Pattern inAppProductsPattern = Pattern.compile("<div class=\"title\">In-app Products<\\/div> <div class=\"content\">(.+?) per item<\\/div>");
                matcher = inAppProductsPattern.matcher(appInfo);
                matcher.find();
                String inAppProducts = matcher.group(1);
                System.out.print(inAppProducts + "\n");
                app.setInAppProducts(inAppProducts);


                appArrayList.add(app);


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }


        }

        appDriver.close();


        // Increment Page number, there are usually about 20 threads on one page. For this Forum there are about 29 pages.
        // The first run in file "Threads1.csv" has data for all of the 29 pages. About 576 Threads
//            pageNumber++;

//        } while(pageNumber < 2 );
//        }while(!(newSubjectElement.isEmpty()) ); // Set the number of Pages you want to crawl here. This Forum has about 29 pages crawlable.
//        } while(pageNumber < 3 | !(newSubjectElement.isEmpty())  );

        //******************************* END OF PAGE SCRAPPING FOR THREADS ************************************************//

        //******************************* START ACCESSING THREADS THEMSELVES TO GET DETAILED INFO **************************//


//        userList = scrapeThreads(threadList, userList);
//
////        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
////        for (User user: userList){
////            System.out.println(user.userName + ",");
////        }
//
////        userList = createUserListFromFile("/Users/johnshu/Desktop/WebScraper/UsersComplete-3rd-Half.csv");
//
//        System.out.println("userList size after thread scrape : " + userList.size());
//
//
//        userList = scrapeNotes(userList);
//
////        System.out.println("*************NOTESCRAPE :NEW USER LIST **************");
////        for (User user: userList){
////            System.out.println(user.userName + ",");
////        }
//
//        System.out.println("userList size after notes scrape : " + userList.size());
//
//
//        userList = scrapePosts(userList);
////        System.out.println("*************POSTCRAPE :NEW USER LIST **************");
////        for (User user: userList){
////            System.out.println(user.userName + ",");
////        }
//
//        System.out.println("userList size after posts scrape : " + userList.size());
//
//
//        userList = scrapeFriends(userList);
//
////        System.out.println("*************USERSCRAPE :NEW USER LIST **************");
////        for (User user: userList){
////            System.out.println(user.userName + ",");
////        }
//
//        System.out.println("userList size after friends scrape : " + userList.size());
//
//
//        try{
//            String userFileText = "";
//
//            String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());
//
//            System.out.println("Writing final user data to the file ...\n");
//
//            // Writing out data to file
//            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ThreadUsers-" + dateString + ".csv"), "utf-8"));
//            for(User user: userList){
////                userFileText = userFileText + user.userName + "\n";
//
//                userFileText = userFileText + user.printToFile();
//
//            }
//
//            System.out.println(userFileText);
//
//            writer.write(userFileText);
//            writer.close();
//
//        } catch (Exception e) {
//            System.err.println("Caught Exception: " + e.getMessage());
//        }

        //******************************* END OF THREAD DATA EXTRACTION ************************************************//


        chromeDriver.close();

    }


















    public static Boolean doesUserExist(List<User> userList, User newUser){
        Boolean value = Boolean.FALSE;

        for(User user: userList){
            if((newUser.userName).equals(user.userName)){
                value = Boolean.TRUE;
                if((newUser.toString()).length() > (user.toString()).length()){
//                    System.out.println("NEWUSER: " + newUser.toString() + " ++++++++ is greater than +++++++++ " + " USER: " + user.toString() );
                    value = Boolean.FALSE;
                }
                System.out.println("Condition to Add is : " + value);
            }
        }

        return value;
    }


















    public static List<User> createUserListFromFile (String file){

        List<User> userList = new ArrayList<User>();

        System.out.println("\n\n" + file);

        Integer count = 0;
        Integer added = 0;


        try {
            Scanner input = new Scanner (new File(file));
            while (input.hasNextLine()) {

                try {
                    String line = input.nextLine();

                    if(!(line == "")){
                        count++;
                        System.out.println("Printing Line :" + line);
                        List<String> attributes = new ArrayList<String>(Arrays.asList(line.split(",")));
                        for(String value: attributes){

                            System.out.println("Value : " + value.trim());

                        }

                        System.out.println("Value : " + count );
                        User user = new User();
                        user.setUserName(attributes.get(0).trim());
                        user.setUserPageLink(attributes.get(1).trim());

                        if (isInt(attributes.get(2).trim())) {
                            user.setPageId(Integer.parseInt(attributes.get(2).trim()));
                        }   else {
                            user.setPageId(0);

                        }

                        if (isInt(attributes.get(3).trim())) {
                            user.setUniqueId(Integer.parseInt(attributes.get(3).trim()));
                        }   else {
                            user.setUniqueId(0);

                        }

                        if (!doesUserExist(userList, user)){
                            System.out.println("\n\nAdding new User");
                            userList.add(user);
                            added++;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        System.out.println("\n\n\n\n\n*****************\n" + "Total number of users is: " + userList.size() + "\n*****************\n");
        System.out.println("\n*****************\n" + "Total number of users added: " + added + "\n*****************\n\n\n\n\n");
        return userList;
    }




    static boolean isInt(String s)
    {
        try
        { int i = Integer.parseInt(s); return true; }

        catch(NumberFormatException er)
        { return false; }
    }
















    public static List<User> scrapeThreads(List<Thread> threadList, List<User> userList){

        System.out.println("\n\n********************************** THREADS: GATHERING DATA ***************************************");


        WebDriver chromeDriver = new FirefoxDriver();

        String threadfileText = "";

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(Thread thread: threadList){

            String threadCommentorsData = "";
            String numberOfComments = "";
            List<User> threadCommentors;

            System.out.println("\n\nThread " + thread.getThreadNumber() + " of " + threadList.size());

            try{

//                System.out.println(thread.appLink);

                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(thread.threadLink);

                // Gathering data on comments for this particular thread
                List<WebElement> subjectCommentsNumber = chromeDriver.findElements(By.className("subject_comments_number"));

                numberOfComments = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", subjectCommentsNumber.get(0));


                // Extracting number of comments in this thread.
                Pattern numberOfCommentsPattern = Pattern.compile("<a href=\"#comments_header\">(.+?)");
                Matcher matcher = numberOfCommentsPattern.matcher(numberOfComments);
                matcher.find();
                String commentsNumber = matcher.group(1);
                System.out.println(commentsNumber);
                thread.setCommentsNumber(Integer.valueOf(commentsNumber));

                // Instantiate list to save all the users who commented in this thread.
                threadCommentors = new ArrayList<User>();

                // Loop to find all the thread commentors
                if(Integer.valueOf(commentsNumber) > 0){

                    // If this page has any data
                    List<WebElement> threadData = chromeDriver.findElements(By.className("question_by"));


                    for ( int i=0; i < threadData.size(); i++){

                        threadCommentorsData = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", threadData.get(i));

                        // New user object
                        User commentor = new User();

                        // Extract Commentor Names
                        Pattern threadCommentorsPattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        matcher = threadCommentorsPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String commentorName = matcher.group(1);
                        System.out.println(commentorName);
                        commentor.setUserName(commentorName);


                        // Extract Commentors page Links
                        Pattern threadCommentorLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = threadCommentorLinkPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String threadCommentorLink = matcher.group(1);
                        threadCommentorLink = "http://www.medhelp.org" + threadCommentorLink;
                        System.out.println(threadCommentorLink);
                        commentor.setUserPageLink(threadCommentorLink);

                        // Extracting user unique Id.
                        Pattern threadCommentorUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
                        matcher = threadCommentorUniqueIdPattern.matcher(threadCommentorsData);
                        matcher.find();
                        String userUniqueId = matcher.group(1);
                        System.out.println(userUniqueId);
                        commentor.setUniqueId(Integer.valueOf(userUniqueId));

                        // Add user to list of thread commentors.
                        threadCommentors.add(commentor);

                        // Add user to list of users.
                        if (!doesUserExist(userList, commentor)){
                            System.out.println("Adding new User");
                            userList.add(commentor);
                        }

                    }
                }

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            // Save list of commentors to this particluar thread
            thread.setCommentors(threadCommentors);

        }

        //******************************* END OF THREAD DATA EXTRACTION ************************************************//

        try {
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Threads-" + dateString + ".csv"), "utf-8"));
            for(Thread thread: threadList){
                threadfileText = threadfileText + thread.printToFile();
            }

            System.out.println(threadfileText);
            writer.write(threadfileText);
            writer.close();


        }catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();

        return userList;
    }















    public static List<User> scrapeUsers(List<User> userList){

        System.out.println("\n\n********************************** IN USER/FRIENDS FUNCTION ***************************************");


        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String userFileText = "";

        String userIds = "";
        String userInfo = "";
        String userNameInfo = "";

        // WebElement from Selenium
        List<WebElement> userPageIdData;
        List<WebElement> userInfoData;
        List<WebElement> userNameData;
        List<WebElement> paginationNumber;


        // List to hold new found users.
        List<User> completeUsersList = new ArrayList<User>();

        // Add existing users to completeUserList
        completeUsersList.addAll(userList);

        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File("/Users/johnshu/Desktop/WebScraper/UsersThreads&Friends" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<User> friendList = new ArrayList<User>();



            System.out.println("\n\n" + user.getUserName()+ " with link " + user.userPageLink);

            try{

                // Go to the Thread Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to(user.userPageLink);

                // Gathering data on comments for this particular thread
                userNameData = chromeDriver.findElements(By.className("page_title"));
                userPageIdData = chromeDriver.findElements(By.className("pp_r_txt_sel"));
                userInfoData = chromeDriver.findElements(By.xpath("//div[contains(@class, 'bottom float_fix')]//div[contains(@class,'section')]"));

                userNameInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userNameData.get(0));
                userIds = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userPageIdData.get(0));
                userInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", userInfoData.get(0));

                // Extracting user page Id.
                Pattern userPageIdPattern = Pattern.compile("<a href=\".+?personal_page_id=(.+?)\">");
                Matcher matcher = userPageIdPattern.matcher(userIds);
                matcher.find();
                String userPageId = matcher.group(1);
                System.out.println(userPageId);
                user.setPageId(Integer.valueOf(userPageId));


                // Extracting user gender.
                Pattern userGenderPattern = Pattern.compile("<span>(.+?)<\\/span>");
                matcher = userGenderPattern.matcher(userInfo);
                matcher.find();
                String userGender = matcher.group(1);
                System.out.println(userGender);
                user.setGender(userGender);

                // Extracting user date joined.
                Pattern userDateJoinedPattern = Pattern.compile("since( .+? .+.?)");
                matcher = userDateJoinedPattern.matcher(userInfo);
                matcher.find();
                String userDateJoined = matcher.group(1);
                System.out.println(userDateJoined);
                user.setDateJoined(userDateJoined);


                //******************************* GATHER USER's FRIEND LIST ************************************************//
                System.out.println("\n\nStaring to parse " + user.getUserName() + "'s friends list ...");

                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

                // Check if user has any friends ?
                List<WebElement> anyFriends = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'friend_box')]"));

                System.out.println("Number of friends : " + anyFriends.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]"));
                System.out.println(paginationNumber.size() + " pages of friends");


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

                    // Loop to find all the friends

                    for ( int i=0; i < anyFriends.size(); i++){

                        anyFriends = chromeDriver.findElements(By.xpath("//div[starts-with(@class, 'friend_box')]"));
                        String friendsData = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", anyFriends.get(i));

                        // New user object
                        User friend = new User();

                        // Extract Friend Names
                        Pattern friendNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        matcher = friendNamePattern.matcher(friendsData);
                        matcher.find();
                        String friendName = matcher.group(1);
                        System.out.println(friendName);
                        friend.setUserName(friendName);

                        // Extract Friend page Links
                        Pattern friendPageLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = friendPageLinkPattern.matcher(friendsData);
                        matcher.find();
                        String friendPageLink = matcher.group(1);
                        friendPageLink = "http://www.medhelp.org" + friendPageLink;
                        System.out.println(friendPageLink);
                        friend.setUserPageLink(friendPageLink);

                        // Extracting Friend unique Id.
                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
                        matcher = friendUniqueIdPattern.matcher(friendsData);
                        matcher.find();
                        String friendUniqueId = matcher.group(1);
                        System.out.println(friendUniqueId);
                        friend.setUniqueId(Integer.valueOf(friendUniqueId));

                        // Add user to Set of Friends.
                        friendList.add(friend);

                        if (!doesUserExist(completeUsersList, friend)){
                            System.out.println("Adding new User ...");
                            completeUsersList.add(friend); // *************************** REPLACE WITH friend
                        }
                    }

                    pageNumber++;
                    System.out.println("\n\nUser Page number is: " + pageNumber);

                }while (pageNumber < (paginationNumber.size()+1));

                user.setFriendsList(friendList);

                System.out.println("User :" + user.getUserName() + " has " + user.friendsList.size() + " Friends");

                System.out.println("Writing user friend data to the file");

                userFileText = user.getUserName() +  ", " + user.getUserPageLink() + ", " + user.getPageId() + ", " + user.getFriendsList().size() + "\n";

                for(User friend: friendList){
                    userFileText = userFileText + " , , , , " + friend.getUserName() + ", " + friend.getUserPageLink() + "\n";
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                bufferedWriter.write(userFileText);
                bufferedWriter.close();

                System.out.println(userFileText);

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                e.getStackTrace();
                e.printStackTrace();
                continue;
            }


        }

        // Adding new found users to existing list of users
        // userList.addAll(newUsers);

        try{
            dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());


            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("UsersComplete-" + dateString + ".csv"), "utf-8"));
            for(User user: completeUsersList){
                userFileText = userFileText + user.getUserName() + ", " + user.getUserPageLink() + "," + user.getPageId() + "\n";
            }

            System.out.println(userFileText);

            writer.write(userFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();

        return userList;
    }













    public static List<User> scrapeNotes(List<User> userList){

        System.out.println("\n\n********************************** IN NOTES FUNCTION ***************************************");


        WebDriver chromeDriver = new FirefoxDriver();

        String noteEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> noteEntryData;
        List<WebElement> paginationNumber;

        String dateString = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File("/Users/johnshu/Desktop/WebScraper/UsersNotes" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<Note> notesList = new ArrayList<Note>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

            try{



                // Go to the Notes Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() + "?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

                String pageSource = chromeDriver.getPageSource();

                if(pageSource.contains("File Not Found")){
                    continue;
                }

                noteEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'note_') and contains(@class, 'note_entry float_fix')]")); // noteEntryData.size() # of notes
//                System.out.println("Number of notes on 1st page: " + noteEntryData.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_page')]")); // # of pages
                Integer paginationIndex = paginationNumber.size();
                System.out.println(paginationNumber.size() + " Notes pages");



                //******************************* OBTAIN NOTES DATA ************************************************//
                System.out.println("\n\nOn " + user.getUserName() +"'s notes list page ");

//                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
                    paginationNumber.clear();

                    // Loop to find all the friends

                    for ( int i=0; i < noteEntryData.size(); i++){

                        // Gathering data on comments for this particular thread
                        noteEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'note_') and contains(@class, 'note_entry float_fix')]")); // noteEntryData.size() # of notes
                        noteEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", noteEntryData.get(i));

                        // New note object
                        Note note = new Note();

                        // Extract note author name
                        Pattern authorNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        Matcher matcher = authorNamePattern.matcher(noteEntryInfo);
                        matcher.find();
                        String authorName = matcher.group(1);
                        System.out.println(authorName);
                        note.setNoteOriginator(authorName);


                        // Extracting date note was left.
                        Pattern noteDatePattern = Pattern.compile("<div>(.+?)<\\/div>");
                        matcher = noteDatePattern.matcher(noteEntryInfo);
                        matcher.find();
                        String noteDate = matcher.group(1);
                        System.out.println(noteDate);
                        note.setNoteDate(noteDate);

                        // Add note to list of Notes.
                        notesList.add(note);


                    }


                    pageNumber++;
                    System.out.println("\n\nNotes Page number is: " + pageNumber);

                    //Update number of pages
                    // Check if friends list spans multiple pages
                    paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_next_page')]")); // # of pages
//                    paginationIndex = paginationNumber.size();
                    System.out.println(paginationNumber.size() + " Notes pages updated");


//                }while (pageNumber < (Integer.parseInt(paginationNumber.get(paginationIndex).getText()) +1));
                }while (paginationNumber.size()>0);


                System.out.println("Writing " + notesList.size() + " notes to the file");

                // Writing out data to file
                // Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Notes-" + dateString + ".csv"), "utf-8"));

                String noteFileText = user.getUserName() + " , " +  notesList.size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Note note: notesList){
                    noteFileText = noteFileText + " , , " + note.printToFile();
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                System.out.println(noteFileText);

                bufferedWriter.write(noteFileText);
                bufferedWriter.close();

            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setFriendsNotes(notesList);
            System.out.println("User : " + user.getUserName() + " has " + user.friendsNotes.size() + " notes" );

        }

        chromeDriver.close();

        return userList;
    }




    public static List<User> scrapeFriends(List<User> userList){

        System.out.println("\n\n********************************** IN FRIENDS FUNCTION ***************************************");


        WebDriver chromeDriver = new FirefoxDriver();

        String friendEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> friendEntryData;
        List<WebElement> paginationNumber;

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        // Writing out data to file
        File file = new File("/Users/johnshu/Desktop/WebScraper/UsersFriends" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<User> friendList = new ArrayList<User>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/friendships/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

            try{



                // Go to the Friend Page itself and collect data on the comments left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/friendships/list/" + user.getUniqueId() + "?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

                String pageSource = chromeDriver.getPageSource();

                if(pageSource.contains("File Not Found")){
                    continue;
                }

                friendEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'friend_') and contains(@class, 'friend_box th_border')]")); // friendEntryData.size() # of friends
                //                System.out.println("Number of friends on 1st page: " + friendEntryData.size());

                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_page')]")); // # of pages
                Integer paginationIndex = paginationNumber.size();
                System.out.println(paginationNumber.size() + " Friend pages");



                //******************************* OBTAIN FRIEND DATA ************************************************//
                System.out.println("\n\nOn " + user.getUserName() +"'s friends list page ");

                //                chromeDriver.navigate().to("http://www.medhelp.org/notes/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );


                do {

                    chromeDriver.navigate().to("http://www.medhelp.org/friendship/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );
                    paginationNumber.clear();

                    // Loop to find all the friends

                    for ( int i=0; i < friendEntryData.size(); i++){

                        // Gathering data on comments for this particular thread
                        friendEntryData = chromeDriver.findElements(By.xpath("//div[starts-with(@id, 'friend_') and contains(@class, 'friend_box th_border')]")); // friendEntryData.size() # of notes
                        friendEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", friendEntryData.get(i));

                        // New note object
                        User friend = new User();

                        // Extract Friend Names
                        Pattern friendNamePattern = Pattern.compile("<a href=\".+?\" id=\".+?\">(.+?)<\\/a>");
                        Matcher matcher = friendNamePattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendName = matcher.group(1);
                        System.out.println(friendName);
                        friend.setUserName(friendName);

                        // Extract Friend page Links
                        Pattern friendPageLinkPattern = Pattern.compile("<a href=\"(.+?)\" id=\"user.+?\">");
                        matcher = friendPageLinkPattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendPageLink = matcher.group(1);
                        friendPageLink = "http://www.medhelp.org" + friendPageLink;
                        System.out.println(friendPageLink);
                        friend.setUserPageLink(friendPageLink);

                        // Extracting Friend unique Id.
                        Pattern friendUniqueIdPattern = Pattern.compile("<a href=\".+?\" id=\"user_(.+?)_.+?\">");
                        matcher = friendUniqueIdPattern.matcher(friendEntryInfo);
                        matcher.find();
                        String friendUniqueId = matcher.group(1);
                        System.out.println(friendUniqueId);
                        friend.setUniqueId(Integer.valueOf(friendUniqueId));


                        // Add user to Set of Friends.
                        friendList.add(friend);

//                        if (!doesUserExist(completeUsersList, friend)){
//                            System.out.println("Adding new User ...");
//                            completeUsersList.add(friend); // *************************** REPLACE WITH friend
//                        }
                    }



                    pageNumber++;
                    System.out.println("\n\nNotes Page number is: " + pageNumber);

                    // Update number of pages
                    // Check if friends list spans multiple pages
                    paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg_next_page')]")); // # of pages
                    //                    paginationIndex = paginationNumber.size();

                    //                 }while (pageNumber < (paginationNumber.size()+1));

                }while (paginationNumber.size()>0);


                user.setFriendsList(friendList);

                System.out.println("User :" + user.getUserName() + " has " + user.friendsList.size() + " Friends");

                System.out.println("Writing " + friendList.size() + " user friend data to the file");

                String userFileText = user.getUserName() +  ", " + user.getUserPageLink() + ", " + user.getPageId() + ", " + user.getFriendsList().size() + "\n";

                for(User friend: friendList){
                    userFileText = userFileText + " , , , , " + friend.getUserName() + ", " + friend.getUserPageLink() + "\n";
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                bufferedWriter.write(userFileText);
                bufferedWriter.close();

                System.out.println(userFileText);


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            user.setFriendsList(friendList);
            System.out.println("User : " + user.getUserName() + " has " + user.getFriendsList().size() + " friends" );

        }

        chromeDriver.close();

        return userList;
    }











    public static List<User> scrapePosts(List<User> userList){

        System.out.println("\n\n********************************** IN POST FUNCTION ***************************************");

        WebDriver chromeDriver = new FirefoxDriver();

        // File String to save to file object
        String postFileText = "";

        String postEntryInfo = "";

        // WebElement from Selenium
        List<WebElement> postEntryData;
        List<WebElement> paginationNumber;

        String dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

        File file = new File("/Users/johnshu/Desktop/WebScraper/UsersPosts-" + dateString + ".csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        // Looping through all the threads already obtained to gather detailed data. "thread" is the individual thread that is handled during each loop iteration
        for(User user: userList){

            Integer pageNumber = 1;

            List<Post> postsList = new ArrayList<Post>();

            System.out.println("\n\n" + user.userName + " with link " + "http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

            try{

                // Go to the Posts Page itself and collect data on the posts left on the page.
                chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId());

                // Gathering data on comments for this particular thread
                postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                System.out.println("Number of posts on 1st page: " + postEntryData.size());


                // Check if friends list spans multiple pages
                paginationNumber = chromeDriver.findElements(By.xpath("//a[starts-with(@class, 'msg')]")); // # of pages
                System.out.println(paginationNumber.size() + " pages of posts");



                //******************************* OBTAIN NOTES DATA ************************************************//

                System.out.println("\n\nOn " + user.getUserName() +"'s posts list page ");


                do {

                    System.out.println("\n\nPost Page number is: " + pageNumber);
                    chromeDriver.navigate().to("http://www.medhelp.org/user_posts/list/" + user.getUniqueId() +"?page=" + pageNumber + "&personal_page_id=" + user.getPageId() );

                    // Loop to find all the friends

                    for ( int i=0; i < postEntryData.size(); i++){

                        postEntryData = chromeDriver.findElements(By.className("user_post")); // postEntryData.size() # of post
                        postEntryInfo = (String)((JavascriptExecutor)chromeDriver).executeScript("return arguments[0].innerHTML;", postEntryData.get(i));

                        // New note object
                        Post post = new Post();

                        // Extract post name
                        Pattern postNamePattern = Pattern.compile("<a href=\"\\/posts\\/.+?\">(.+?)<\\/a>");
                        Matcher matcher = postNamePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postName = matcher.group(1);
                        System.out.println(postName);
                        post.setPostName(postName);


                        // Extracting date note was left.
                        Pattern postDatePattern = Pattern.compile("<span class=\"date\">(.+?) in the<\\/span>");
                        matcher = postDatePattern.matcher(postEntryInfo);
                        matcher.find();
                        String postDate = matcher.group(1);
                        System.out.println(postDate);
                        post.setPostDate(postDate);


                        // Extracting community posted in
                        Pattern postInCommunityPattern = Pattern.compile("<a href=\"\\/forums\\/.+?\">(.+?)<\\/a>");
                        matcher = postInCommunityPattern.matcher(postEntryInfo);
                        matcher.find();
                        String postInCommunity = matcher.group(1);
                        System.out.println(postInCommunity);
                        post.setPostCommunity(postInCommunity);

                        // Add post to postList
                        postsList.add(post);
                    }

                    pageNumber++;

                }while (pageNumber < (paginationNumber.size()+1));

                user.setUserPosts(postsList);

                System.out.println("Writing posts data to the file");

                postFileText = postFileText + user.getUserName()  + ", " + postsList.size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Post post: postsList){
                    postFileText = postFileText + " , , " + post.getPostName() + ", " + post.getPostCommunity() + ", " + post.getPostDate() + "\n";
                }

                // Get file and write user friends to the file
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));

                System.out.println(postFileText);

                bufferedWriter.write(postFileText);
                bufferedWriter.close();


            }catch (Exception e) {
                System.err.println("Caught Exception: " + e.getMessage());
                continue;
            }

            System.out.println("User :" + user.getUserName() + " has " + user.userPosts.size() + "\n\n");

        }

        try{
            dateString = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date());

            System.out.println("Writing post data to the file");

            // Writing out data to file
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Posts-" + dateString + ".csv"), "utf-8"));
            for(User user: userList){

                postFileText = postFileText + user.getUserName() + ", " + user.getUserPosts().size() + "\n"; // Last comma means on newline in Excel skip one cell

                for(Post post: user.getUserPosts()){
                    postFileText = postFileText + " , , " + post.printToFile();
                }
            }

            System.out.println(postFileText);

            writer.write(postFileText);
            writer.close();

        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }

        chromeDriver.close();

        return userList;
    }

}







