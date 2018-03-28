package vakoze.blomidtech.vakoze.lib.api;

import vakoze.blomidtech.vakoze.lib.response.UsersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by capp on 20/03/2018.
 */

public class ApiMethods {

    @GET(ApiEndPoints.GET_USERS)
    public Call<UsersResponse> getUsers(@Query(ApiParam.USER_ID) String userId,
                                        @Query(ApiParam.TERM) String term) {
        return null;
    }
}
