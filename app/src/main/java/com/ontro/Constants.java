package com.ontro;

import com.ontro.dto.ExploreModel;

import java.util.ArrayList;

/**
 * Created by umm on 17-Feb-17.
 */

public class Constants {

    public static int notificationview=0;
    // Error Messages
    public static String INTERNET_ERROR = "It seems ic_like you are not connected to internet. Try again later";
    public static String APP_NAME = "ontro";
    public static String PLAYER_DEFAULT_IMAGE_URL = "http://ideomind.in/demo/ontro/public/img/default.jpg";
    public static String FIREBASE_URL = "https://ontro-6c0d3.firebaseio.com";
    static Integer[] intro = {R.drawable.m1, R.drawable.m2, R.drawable.m3};
    static String[] mhelptext = {"Pick out the best players or teams around you and battle it out to see who's the best",
            "Create your own sports profile to gain popularity and know where you stand among other players.", "Wanna check out the latest highlights and updates in the world of Ontro? Just Stream through this page."};
    static ArrayList<ExploreModel> arrayList = new ArrayList<>();
    public static final String CHAT = "chats";
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final String FIREBASE_USER_LIST = "users";
    public static int invitecountindicator=0;
    public static final int NOTIFICATION_ID = 105;
    public static boolean isChatNotification = false;
    public static boolean refresh = true;
    public static int SCORE_UPDATED = 212;

    public interface BundleKeys {
        String FIREBASE_MESSAGE = "messsage";
        String PLAYER_ID = "player_id";
        String VENUE_ID = "venue_id";
        String PROFILE_COMPLETION = "profile_completion";
        String INVITE_ID = "invite_id";
        String TOURNAMENT_ID = "tournament_id";
        String FCM_UID = "fcm_uid";
        String FORM_ID = "form_id";
        String OVER_VIEW = "overview";
        String DATE_AND_VENUE = "date_and_venue";
        String ENTRY_FEES = "entry_fees";
        String RULES_AND_REGULATION = "rules_and_regulation";
        String FIRST_TEAM_SCORE = "first_team_score";
        String SECOND_TEAM_SCORE = "second_team_score";
        String MY_TEAM_NAME = "my_team_name";
        String OPPONENT_TEAM_NAME = "opponent_team_name";
        String MY_TEAM_IMAGE = "my_team_logo";
        String OPPONENT_TEAM_IMAGE = "opponent_team_logo";
        String SCORE_UPDATE_STATUS = "score_update_status";
        String MATCH_DATE = "match_date";
        String MATCH_VENUE = "match_venue";
        String SPORT_ID = "sport_id";
        String TEAM_UPDATE_STATUS = "team_update_status";
        String MY_TEAM_SQUAD = "my_team_squad";
        String TEAM_ID = "team_id";
        String OPPONENT_TEAM_ID = "opponent_team_id";
        String MATCH_ID = "match_id";
        String FIRST_TEAM_PLAYER_SCORE = "first_team_player_score";
        String BENCH_PLAYER = "bench_player";
        String QUARTER_SQUARE = "quarter_score";
        String SECOND_TEAM_PLAYER_SCORE = "second_team_player_score";
        String OPPONENT_USER_SCORE_STATUS = "opponent_user_score_status";
        String LOGIN_USER_SCORE_STATUS = "login_user_score_status";
        String MATCH_STATUS_POSITION = "match_status_position";
        String MATCH_TYPE = "match_type";
        String TOURNAMENT_NAME = "tournament_name";
        String PLAYER_NAME = "player_name";
        String TEAM_NAME = "team_name";
        String SPORT_NAME = "sport_name";
        String LOCATION_ID = "location_id";
        String TEAM_INFO = "team_info";
        String LOCATION_NAME = "location_name";
        String MY_MATCH = "my_match";
        String OPPONENT_SQUADS = "opponent_squads";
        String PLAYER_PROFILE_INFO = "player_profile_info";
        String IS_OWNER = "is_owner";
        String INVITE_TO_MATCH = "invite_to_match";
        String PLAYER_TEAM_LIST = "player_team_list";
        String MY_MATCH_INVITES = "my_match_invites";
        String EXPLORE_MODEL = "explore_model";
        String PLAYER_INVITE_ID = "player_invite_id";
        String TEAM_DETAIL_RESPONSE = "TEAM_DETAIL_RESPONSE";
        String FIRST_TEAM_MATCH_SCORE = "first_team_match_score";
        String SECOND_TEAM_MATCH_SCORE = "second_team_match_score";
    }

    public interface Messages {
        String NOTIFICATION_LISTENER_NULL = "Firebase Notification listener is null";
        String PLAYER_QUICK_VIEW = "PlayerQuickViewFragment";
        String SIGN_UP = "SignUp";
        String PLAYER_PROFILE = "PlayerProfile";
        String NO_DETAIL = "Nil";
        String NO_NUMBER_FOUND = "No number found";
        String EMPTY_FIELDS = "Provide required fields";
        String ENTER_EMAIL = "Enter email address";
        String ENTER_VALID_EMAIL = "Email address not valid";
        String ENTER_MOBILE_NUMBER = "Enter mobile number";
        String ENTER_VALID_MOBILE_NO = "Mobile number not valid";
        String ENTER_PASSWORD = "Enter password";
        String PASSWORD_LENGTH_ERROR = "Password should be minimum 8 characters";
        String ENTER_RE_ENTER_PASSWORD = "Enter confirmation password";
        String PASSWORD_MISMATCH = "Password mismatch";
        String ENTER_NAME = "Enter name";
        String SELECT_BIRTH_DATE = "Select birth date";
        String INVALID_BIRTH_DATE = "Required minimum age is 10";
        String SELECT_GENDER = "Select gender";
        String SELECT_CITY = "Select city";
        String SELECT_LOCATION = "Select location";
        String INVALID_HEIGHT = "Player height should be in the range of 120 to 245";
        String NOT_OWNER = "You are not owner of this team";
        String TEAM_SCORE_CAN_NOT_ZERO = "Both team score can't be 0";
        String ENTER_ATLEAST_ONE_BATSMAN_SCORE = "Fill atleast one batsman score";
        String ENTER_ATLEAST_ONE_BOWLER_SCORE = "Fill atleast one bowler score";
        String ENTER_BATSMAN_SCORE = "Fill batsmen score before update bowler score";
        String ENTER_BOWLER_SCORE = "Fill bowler score before update extras score";
        String SELECT_GOLF_KEEPER = "Set goal keeper by selecting a player";
        String FLAG_MATCH = "flag_match";
        String PAYMENT_SUCCESSFUL = "Payment Completed Successfully!!";
        String PLAYER_INVITE_SENT_SUCCESSFULLY = "Player invite sent successfully";
        String MATCH_INVITE_SENT_SUCCESSFULLY = "Match invitation sent successfully";
        String SPORT_UNSELECTION_INFO = "You have a team associated with this favourite sport, To change your favourite sport you must exit or delete that team.";
        String GOALS_AND_ASSIST_MISMATCH = "Total goals and assists mismatch";
        String TEAM_NAME_EXIST = "Team name already exist";
        String SQUAD_MISMATCH = "Squad size mismatch";
        String SELECTED_PLAYER_EXIST = "Selected player exist in opponent squad ";
    }

    public interface DefaultText {
        String RUPEES_SYMBOL = "Rs. ";
        String AVAILABLE_ON = "Available on: ";
        String TO = " to ";
        int INVITE_ACCEPT_STATUS = 1;
        int INVITE_DECLINE_STATUS = 2;
        String TEL_KEYWORD = "tel:";
        String ZERO = "0";
        String ONE = "1";
        String TWO = "2";
        String THREE = "3";
        String NO_SCORE = "_";
        String YEAR_MONTH_DATE = "yyyy-MM-dd";
        String DATE_MONTH_YEAR = "dd-MM-yyyy";
        String DATE_MONTH_LETTER = "dd-MMM yyyy";
        String DOT = ".";
        String ASTERISK = "*";
        String SLASH = "/";
        String OVERS = " overs";
        String FIRST_INNINGS = "1ST INN";
        String SECOND_INNINGS = "2ND INN";
        String HYPHEN = "-";
        String TEAM = "Team";
        String INDIVIDUAL = "Individual";
        String DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss";
        String LETTER_DATE_AND_TIME = "dd-MMM yyyy hh:mm aa";
        String TIME = "hh:mm aa";
        String TODAY = "Today ";
        String YESTERDAY = "Yesterday ";
        String YOU = "You";
        String INFO = "Info";
        String YEARS = " years";
        String CENTIMETER = " cm";
        String NOTIFICATION = "notification";
        String PLAYER_INVITE = "Player Invite";
        String VS = "vs";
        String EMPTY = "";
        String SLIDE_TO_INVITE_TO_TEAM = "Slide to invite to team";
        String SLIDE_TO_CANCEL_REQUEST = "Slide to cancel request";
        String SLIDE_TO_MAKE_ADMIN = "Slide to make admin";
        String SLIDE_TO_REMOVE_AS_ADMIN = "Slide to remove as admin";
        String OWNER = "Owner";
        String Ok = "OK";
        String TEAM_NAME_ALREADY_TAKEN = "The Team name already taken";
        String MATCH_CONFIRMED_BY_OPPONENT = "Match confirmated by opponent";
        String WAITING_FOR_OPPONENT_CONFIRMATION = "waiting for opponent confirmation";
    }

    public interface FragmentTag {
        String NEWS_FEED_FRAGMENT = "newsfeed";
        String DISCUSSION_FRAGMENT = "discussion";
        String MY_TEAM_FRAGMENT = "myteam";
        String EXPLORE_FRAGMENT = "explore";
        String MY_MATCH_FRAGMENT = "mymatches";
        String CHAT_FRAGMENT = "chat";
    }

    public interface CommonKeys {
        int REQUEST_CODE_CALL_PHONE = 112;
        int MY_PERMISSIONS_REQUEST = 1;
        int REQUEST_CAMERA = 0;
        int SELECT_FILE = 1;
        int PICK_IMAGE_REQUEST = 2;
    }

    public interface CommonFields {
        String MAP_PACKAGE_NAME = "com.google.android.apps.maps";
        String FACEBOOK_NAVIGATION_PACKAGE = "com.facebook.katana";
        String FACEBOOK_APP_NAVIGATION_URL = "fb://facewebmodal/f?href=";
        String FACEBOOK_PAGE_ID_NAVIGATION_URL = "fb://page/";
        String TWITTER_NAVIGATION_PACKAGE = "com.twitter.android";
        String TWITTER_PAGE_NAVIGATION_URL = "twitter://user?screen_name=";
        String CALL_PERMISSION_DENIED = "Permission is required to do this action";
    }

    public interface NotificationTag {
        String MATCH_INVITE_REQUEST = "match_invite_request";
        String MATCH_INVITE_RESPONSE = "match_invite_response";
        String PLAYER_INVITE_REQUEST = "player_invite_request";
        String PLAYER_INVITE_RESPONSE = "player_invite_response";
        String MATCH_EXPIRED = "match_expired";
        String NOTIFICATION = "notification";
    }
}
