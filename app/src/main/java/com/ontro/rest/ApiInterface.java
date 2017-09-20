package com.ontro.rest;

import com.ontro.dto.BasketballScoreDTO;
import com.ontro.dto.ChatDetailModel;
import com.ontro.dto.ConfirmBookingInputModel;
import com.ontro.dto.CreateTeamInput;
import com.ontro.dto.CricketScoreUpdateRequest;
import com.ontro.dto.DiscussionCommentInput;
import com.ontro.dto.DiscussionListInput;
import com.ontro.dto.FirebaseAuthUser;
import com.ontro.dto.FootballScoreUpdateRequest;
import com.ontro.dto.InviteModel;
import com.ontro.dto.InvitePlayerRequest;
import com.ontro.dto.LogoutRequest;
import com.ontro.dto.MatchFlagRequestModel;
import com.ontro.dto.MatchRequestResponseModel;
import com.ontro.dto.NotificationTokenRequest;
import com.ontro.dto.OTPinput;
import com.ontro.dto.PaymentModel;
import com.ontro.dto.PlayerInviteApprovalRequest;
import com.ontro.dto.PlayerInviteCancelRequest;
import com.ontro.dto.ProfileCompletionModel;
import com.ontro.dto.RemovePlayerFromTeam;
import com.ontro.dto.ResetPasswordRequest;
import com.ontro.dto.ScheduleConfirmInput;
import com.ontro.dto.ScoreApproveModel;
import com.ontro.dto.ScoreUpdate;
import com.ontro.dto.ScoreUpdateBasketball;
import com.ontro.dto.StartDiscussionInput;
import com.ontro.dto.TeamEditRequestModel;
import com.ontro.dto.TeamFormInputModel;
import com.ontro.dto.TeamNameRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("register")
    Call<ResponseBody> User_registeration(@Body RegisterInputDTO registerInputDTO);

    @POST("login")
    Call<ResponseBody> User_login(@Body LoginInputDTO loginInputDTO);

    @POST("auth/facebook")
    Call<ResponseBody> FBAuthendication(@Body SocialAuthendicationInput socialAuthendicationInput);

    @POST("auth/google")
    Call<ResponseBody> Gplus_Authendication(@Body SocialAuthendicationInput socialAuthendicationInput);

    @POST("profile/complete")
    Call<ResponseBody> Profile_completion(@Header("Authorization") String authorization, @Body ProfileCompletionModel mProfileCompletionInput);

    @GET("profile/locations/{id}")
    Call<ResponseBody> getLocationByCity(@Header("Authorization") String authorization,@Path("id") String cityid);

    @GET("profile/alllocations")
    Call<ResponseBody> getLocation(@Header("Authorization") String authorization);

    @GET("profile/city")
    Call<ResponseBody> getCity(@Header("Authorization") String authorization);

    @GET("news")
    Call<ResponseBody> getNewsFeeds(@Header("Authorization") String authorization);

    @POST("otp/send")
    Call<ResponseBody> GenerateOTP(@Header("Authorization") String authorization);

    @POST("otp/verify")
    Call<ResponseBody> VerifyOTP(@Header("Authorization") String authorization, @Body OTPinput otPinput);

    @POST("discussion/list")
    Call<ResponseBody> Discusionlist(@Header("Authorization") String authorization, @Body DiscussionListInput discussionListInput);

    @POST("discussion/start")
    Call<ResponseBody> NewDiscussion(@Header("Authorization") String authorization, @Body StartDiscussionInput startDiscussionInput);

    @GET("discussion/{id}")
    Call<ResponseBody> DiscussionDetail(@Path("id") String id, @Header("Authorization") String authorization);

    @POST("discussion/comment")
    Call<ResponseBody> DiscussionCommentSent(@Header("Authorization") String authorization, @Body DiscussionCommentInput discussionCommentInput);

    @GET("discussion/comment/{id}")
    Call<ResponseBody> Like(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("discussion/comment/unlike/{id}")
    Call<ResponseBody> UnLike(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("news/{id}")
    Call<ResponseBody> NewsLike(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("news/unlike/{id}")
    Call<ResponseBody> NewsUnLike(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("discussion/comment/flag/{id}")
    Call<ResponseBody> CommentFlag(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("discussion/comment/unflag/{id}")
    Call<ResponseBody> CommentUnFlag(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("discussion/flag/{id}")
    Call<ResponseBody> DiscussionFlag(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("discussion/unflag/{id}")
    Call<ResponseBody> DiscussionUnFlag(@Path("id") String id, @Header("Authorization") String authorization);

    @GET("teams/me")
    Call<ResponseBody> MyTeam(@Header("Authorization") String authorization);

    @POST("teams/create")
    Call<ResponseBody> CreateTeam(@Header("Authorization") String authorization, @Body CreateTeamInput createTeamInput);

    @GET("explore/players")
    Call<ResponseBody> ExplorePlayers(@Header("Authorization") String authorization, @Query("search_key") String search_key, @Query("location") String location, @Query("type_of_sports") String type_of_sports, @Query("page") String page);

    @GET("explore/teams")
    Call<ResponseBody> ExploreTeams(@Header("Authorization") String authorization, @Query("search_key") String search_key, @Query("location") String location, @Query("type_of_sports") String type_of_sports, @Query("page") String page);

    @GET("teams/info/{id}")
    Call<ResponseBody> FetchTeamInfo(@Header("Authorization") String authorization, @Path("id") String id);

    @POST("match/invite")
    Call<ResponseBody> Invite(@Header("Authorization") String authorization, @Body InviteModel inviteModel);

    @GET("match/me")
    Call<ResponseBody> MyMatch(@Header("Authorization") String authorization, @Query("filter_type") String filter_type);

    @GET("match/details")
    Call<ResponseBody> MatchDetail(@Header("Authorization") String authorization, @Query("match_id") String match_id, @Query("team_id") String team_id);

    @POST("match/confirmation")
    Call<ResponseBody> MatchConfirmationStatus(@Header("Authorization") String authorization, @Body MatchRequestResponseModel matchRequestResponseModel);

    @GET("player/myprofile")
    Call<ResponseBody> getMyProfilePersonalInfo(@Header("Authorization") String authorization);

    @GET("player/info/{id}")
    Call<ResponseBody> getPlayerPersonalInfo(@Header("Authorization") String authorization, @Path("id") String id);

    @GET("player/records")
    Call<ResponseBody> getPlayerRecordsInfo(@Header("Authorization") String authorization, @Query("sport_type") String sport_type, @Query("player_id") String player_id);

    @GET("schedule/players/{id}")
    Call<ResponseBody> TeamPlayerlist(@Header("Authorization") String authorization, @Path("id") String id);

    @POST("schedule/matchPlayers")
    Call<ResponseBody> FormTeam(@Header("Authorization") String authorization, @Body TeamFormInputModel teamFormInputModel);

    @GET("schedule/venues")
    Call<ResponseBody> getVenue(@Header("Authorization") String auth_token, @Query("matches") int matchType, @Query("location") int locationid, @Query("booking_date") String date);

    @GET("schedule/venues/details/{id}")
    Call<ResponseBody> getVenueDetail(@Header("Authorization") String auth_token, @Path("id") int venueId);

    @POST("schedule/venueBooking")
    Call<ResponseBody> ConfirmBooking(@Header("Authorization") String authorization, @Body ConfirmBookingInputModel confirmBookingInputModel);

    @POST("schedule/confirm")
    Call<ResponseBody> ScheduleConfirm(@Header("Authorization") String authorization, @Body ScheduleConfirmInput scheduleConfirmInput);

    @POST("match/updateScore")
    Call<ResponseBody> ScoreUpdate(@Header("Authorization") String authorization, @Body ScoreUpdate scoreUpdate);

    @POST("match/updateScore")
    Call<ResponseBody> BasketballScoreUpdate(@Header("Authorization") String authorization, @Body BasketballScoreDTO basketballScoreDTO);

    @POST("match/updateScore")
    Call<ResponseBody> ScoreUpdate(@Header("Authorization") String authorization, @Body ScoreUpdateBasketball scoreUpdate);

    @GET("match/scoreView")
    Call<ResponseBody> OpponentScoreView(@Header("Authorization") String authorization, @Query("match_id") String match_id, @Query("opponent_id") String opponent_id, @Query("sport_type") String sport_type);

    @GET("match/scoreView")
    Call<ResponseBody> GetScoreViewDetail(@Header("Authorization") String authorization, @Query("match_id") String match_id, @Query("opponent_id") String opponent_id, @Query("sport_type") String sport_type);

    @POST("match/approveScore")
    Call<ResponseBody> ScoreApprove(@Header("Authorization") String authorization, @Body ScoreApproveModel scoreApproveModel);

    @POST("match/updateScore")
    Call<ResponseBody> footballScoreUpdate(@Header("Authorization") String authorization, @Body FootballScoreUpdateRequest footballScoreUpdateRequest);

    @POST("match/updateScore")
    Call<ResponseBody> cricketScoreUpdate(@Header("Authorization") String authorization, @Body CricketScoreUpdateRequest cricketScoreUpdateRequest);

    @GET("match/completedDetails")
    Call<ResponseBody> ScoreCompleted(@Header("Authorization") String authorization, @Query("match_id") String match_id);

    @GET("player/quickView/{id}/{sportid}")
    Call<ResponseBody> getPlayerQuickViewInfo(@Header("Authorization") String authorization, @Path("id") int playerId,@Path("sportid") String sportid);

    @GET("player/invites/{id}")
    Call<ResponseBody> invitePlayerToTeam(@Header("Authorization") String authorization, @Path("id") int playerId);

    @POST("player/inviteApproval")
    Call<ResponseBody> getInviteApproval(@Header("Authorization") String authorization, @Body PlayerInviteApprovalRequest playerInviteApprovalRequest);

    @POST("player/invite")
    Call<ResponseBody> inviteToTeam(@Header("Authorization") String authorization, @Body InvitePlayerRequest invitePlayerRequest);

    @POST("fcm")
    Call<ResponseBody> sendFcmTokenToServer(@Header("Authorization") String authorization, @Body NotificationTokenRequest notificationTokenRequest);

    @GET("tournament/{tournament_id}")
    Call<ResponseBody> getTournamentDetail(@Header("Authorization") String authorization, @Path("tournament_id") int tournamentId);

    @GET("teams/delete/{team_id}")
    Call<ResponseBody> TeamDelete(@Header("Authorization") String authorization, @Path("team_id") int teamid);

    @GET("teams/player/exit")
    Call<ResponseBody> TeamExit(@Header("Authorization") String authorization, @Query("player_id") int player_id,@Query("team_id") int teamid);

    @POST("payment/paymentRequest")
    Call<ResponseBody> PaymentRequest(@Header("Authorization") String authorization, @Body PaymentModel paymentModel);

    @POST("logout")
    Call<ResponseBody> getLogoutResponse(@Header("Authorization")String authorization, @Body LogoutRequest logoutRequest);

    @POST("fcmUserId")
    Call<ResponseBody> sendFcmUniqueIdToServer(@Header("Authorization")String authorization, @Body FirebaseAuthUser firebaseAuthUser);

    @GET("fcmUserIdList")
    Call<ResponseBody> getFirebaseUserList(@Header("Authorization")String authorization);

    @GET("player/teams/{id}")
    Call<ResponseBody> getPlayerTeam(@Header("Authorization") String authorization, @Path("id") int playerId);

    @POST("forgotPassword")
    Call<ResponseBody> getResetPasswordResponse(@Body ResetPasswordRequest resetPasswordRequest);

    @GET("notifications/{id}")
    Call<ResponseBody> getNotificationHistory(@Header("Authorization") String authorization, @Path("id") String playerId);

    @GET("explore/players")
    Call<ResponseBody> getSpecificExplorePlayers(@Header("Authorization") String authorization, @Query("search_key") String search_key, @Query("location") String location, @Query("type_of_sports") String type_of_sports);

    @POST("flagMatch")
    Call<ResponseBody> setFlagMatch(@Header("Authorization") String authorization, @Body MatchFlagRequestModel matchFlagRequestModel);

    @POST("teams/edit")
    Call<ResponseBody> onUpdateTeamInfo(@Header("Authorization") String authorization, @Body TeamEditRequestModel teamEditRequestModel);

    @POST("fcmUserTeamChat")
    Call<ResponseBody> updateChatTime(@Header("Authorization") String authorization, @Body ChatDetailModel chatDetailModel);

    @GET("teamLatestChat")
    Call<ResponseBody> getLatestChatTime(@Header("Authorization") String authorization, @Query(" team_uid") String chatId);

    @POST("teams/rejectInvite")
    Call<ResponseBody> cancelPlayerInviteRequest(@Header("Authorization") String authorization,  @Body PlayerInviteCancelRequest inviteCancelRequest);

    @POST("teams/teamName")
    Call<ResponseBody> checkTeamNameAvailable(@Header("Authorization") String authorization, @Body TeamNameRequest teamNameRequest);

    @POST("teams/player/remove")
    Call<ResponseBody> removePlayerFromTeam(@Header("Authorization") String authorization, @Body RemovePlayerFromTeam removePlayerFromTeam );
}
