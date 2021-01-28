package com.worker.app.network.network;

import java.util.HashMap;

import com.worker.app.model.AgeRangeModel;
import com.worker.app.model.DocumentUploadResponse;
import com.worker.app.model.UpdateLanguage;
import com.worker.app.model.WorkerProfileResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    @Multipart
    @POST("update_user_identity.php")
    Call<DocumentUploadResponse> uploadUserIdProof(@Part("language") RequestBody id,
                                               @Part("UserID") RequestBody firstName,
                                               @Part("OrderID") RequestBody lastName,
                                               @Part MultipartBody.Part UserIdentificationAttachment);

    @Multipart
    @POST("add_visa_detail.php")
    Call<DocumentUploadResponse> uploadVisaProof(@Part("language") RequestBody language,
                                                 @Part("OrderID") RequestBody OrderID,
                                                   @Part("CountryID") RequestBody CountryID,
                                                   @Part MultipartBody.Part Upload);

    @FormUrlEncoded
    @POST("worker_detail.php")
    Call<WorkerProfileResponse> viewWorkerProfile(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("age_list.php")
    Call<AgeRangeModel> getStaffAge(@FieldMap HashMap<String, String> hashMap);

    @FormUrlEncoded
    @POST("update_language.php")
    Call<UpdateLanguage> updateLanguage(@FieldMap HashMap<String, String> hashMap);
}

