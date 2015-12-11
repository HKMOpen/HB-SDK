package com.hypebeast.sdk.api.resources.hbstore;

import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ReponseNormal;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseProductList;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseSingleProduct;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by hesk on 30/6/15.
 */
public interface Products {

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final @Query("filter") String json,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final @Query("filter") String json,
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final @Query("page") int page_num,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}")
    void bycate(
            final @Path("catename") String category_name,
            final @Query("filter") String json,
            final @Query("page") int page_num,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final @Query("filter") String json,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final @Query("page") int page_num,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final @Query("filter") String json,
            final @Query("page") int page_num,
            final Callback<ReponseNormal> result) throws ApiException;


    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/categories/{catename}/{subcate}")
    void bysubcate(
            final @Path("catename") String category_name,
            final @Path("subcate") String subcate,
            final @Query("filter") String json,
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result) throws ApiException;

    @GET("/search")
    void search(final @Query("q") String keyword,
                final Callback<ReponseNormal> result) throws ApiException;

    @GET("/search")
    void search(final @Query("q") String keyword,
                final @Query("page") int page_num,
                final Callback<ReponseNormal> result) throws ApiException;

    @GET("/search")
    void search(final @Query("q") String keyword,
                final @Query("page") int page_num,
                final @Query("limit") int limit,
                final Callback<ReponseNormal> result) throws ApiException;


    @GET("/{special_item}")
    void mainList(final @Path("special_item") String special_entry,
                  final Callback<ReponseNormal> result) throws ApiException;

    @GET("/{special_item}")
    void mainList(final @Path("special_item") String special_entry,
                  final @Query("page") int page_num,
                  final @Query("limit") int limit,
                  final Callback<ReponseNormal> result) throws ApiException;

    @GET("/{special_item}")
    void mainList(final @Path("special_item") String special_entry,
                  final @Query("filter") String jsonString,
                  final @Query("page") int page_num,
                  final @Query("limit") int limit,
                  final Callback<ReponseNormal> result) throws ApiException;


    @GET("/{special_item}")
    void mainList(final @Path("special_item") String special_entry,
                  final @Query("filter") String jsonString,
                  final Callback<ReponseNormal> result) throws ApiException;


    @GET("/brands/{brand_name}")
    void bybrand(final @Path("brand_name") String brand_name,

                 final Callback<ReponseNormal> result) throws ApiException;

    @GET("/brands/{brand_name}")
    void bybrand(final @Path("brand_name") String brand_name,
                 final @Query("page") int page_num,
                 final @Query("limit") int limit,
                 final Callback<ReponseNormal> result) throws ApiException;

    @GET("/brands/{brand_name}")
    void bybrand(final @Path("brand_name") String brand_name,
                 final @Query("filter") String jsonString,
                 final Callback<ReponseNormal> result) throws ApiException;


    @GET("/brands/{brand_name}")
    void bybrand(final @Path("brand_name") String brand_name,
                 final @Query("filter") String jsonString,
                 final @Query("page") int page_num,
                 final @Query("limit") int limit,
                 final Callback<ReponseNormal> result) throws ApiException;

    /**
     * @param page_num the page number
     * @param result   the result of the product list in wish list
     * @throws ApiException its the exception
     */
    @GET("/account/wishlist/")
    void wishlist(
            final @Query("page") int page_num,
            final Callback<ResponseProductList> result) throws ApiException;

    /**
     * login account with adding new product id into the list
     *
     * @param product_id_to_add the long ID
     * @param result            the string result
     * @throws ApiException n
     */
    @GET("/account/wishlist/add/{product_id}")
    void addItemWishList(
            final @Path("product_id") long product_id_to_add,
            final Callback<String> result) throws ApiException;

    /**
     * after login the user can called to remove the item from the wish list
     *
     * @param product_id_to_remove the product id
     * @param result               the string in result
     * @throws ApiException n
     */
    @GET("/account/wishlist/remove/{product_id}")
    void removeItemWishList(
            final @Path("product_id") long product_id_to_remove,
            final Callback<String> result) throws ApiException;

    @GET("/")
    void general(
            final @Query("filter") String jsonString,
            final Callback<ReponseNormal> result
    ) throws ApiException;

    @GET("/")
    void general(
            final @Query("filter") String jsonString,
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result
    ) throws ApiException;

    @GET("/")
    void general(
            final @Query("page") int page_num,
            final @Query("limit") int limit,
            final Callback<ReponseNormal> result
    ) throws ApiException;

    @GET("/")
    void general(
            final Callback<ReponseNormal> result
    ) throws ApiException;


}
