package com.hkm.hbxtest;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hkm.hbxtest.Dialog.ErrorMessage;
import com.hypebeast.sdk.api.exception.ApiException;
import com.hypebeast.sdk.api.model.hypebeaststore.ResponseListOrder;
import com.hypebeast.sdk.api.model.symfony.Product;
import com.hypebeast.sdk.api.model.symfony.ShoppingCart;
import com.hypebeast.sdk.api.model.symfony.wish;
import com.hypebeast.sdk.api.resources.hbstore.SingleProduct;
import com.hypebeast.sdk.clients.HBStoreApiClient;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 13/1/16.
 */
public class general_test extends Fragment {


    protected int LayoutID() {
        return R.layout.general_main;
    }

    CircleProgressBar betterCircleBar;
    Button login, addcart, _template;
    TextView console;
    EditText pid, pqt;
    Handler mHandler = new Handler();
    HBStoreApiClient client;


    protected void initBinding(View v) {
        client = HBStoreApiClient.getInstance(getActivity());
        betterCircleBar = (CircleProgressBar) v.findViewById(com.hkm.ezwebview.R.id.wv_simple_process);
        addcart = (Button) v.findViewById(R.id.check_out_at_item);
        _template = (Button) v.findViewById(R.id.template_show);
        login = (Button) v.findViewById(R.id.login_button);
        console = (TextView) v.findViewById(R.id.console);
        pid = (EditText) v.findViewById(R.id.console_product_id);
        pqt = (EditText) v.findViewById(R.id.product_quantity);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runbind();
                completeloading();
            }
        }, 100);

    }

    private void runbind() {

        _template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage("===template found===");
                addMessage(client.getTemplateHTML());
            }
        });
        addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SingleProduct define_request = client.createRequest();
                if (pid.getText().toString().isEmpty() || pqt.getText().toString().isEmpty())
                    return;
                try {
                    define_request.addCart((long) Integer.parseInt(pid.getText().toString()),
                            Integer.parseInt(pqt.getText().toString()),
                            new Callback<ResponseListOrder>() {
                                @Override
                                public void success(ResponseListOrder order_detail, Response response) {
                                    if (order_detail._error != null) {
                                        addMessage("===operation aborted===");
                                        addMessage(order_detail._error);
                                    } else {
                                        try {
                                            define_request.checkCart(new Callback<ShoppingCart>() {
                                                @Override
                                                public void success(ShoppingCart shoppingCart, Response response) {
                                                    addMessage("successfully added to the cart");
                                                    addMessage("there are items of total:" + shoppingCart.cart_data.items.size());
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    addMessage("===operation aborted===");
                                                    addMessage(error.getMessage());
                                                }
                                            });
                                        } catch (ApiException e) {
                                            addMessage("===operation aborted===");
                                            addMessage(e.getMessage());
                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    addMessage("===operation aborted===");
                                    addMessage(error.getMessage());
                                }
                            }


                    );
                } catch (ApiException e) {
                    addMessage("===operation aborted===");
                    addMessage(e.getMessage());
                }

            }
        });


    }


    private void common_process_done(final String message, final boolean withDialog) {
        if (withDialog) ErrorMessage.alert(message, getFragmentManager());
        addMessage(message);
        completeloading();
    }


    protected void completeloading() {
        ViewCompat.animate(betterCircleBar).alpha(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                runbind();
            }
        });
    }

    private void addMessage(final String mMessage) {
        console.setText(console.getText().toString() + "\n" + mMessage);
    }


    public void failure(String error_message_out) {
        ErrorMessage.alert(error_message_out, getFragmentManager());
        completeloading();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(LayoutID(), container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initBinding(view);
    }
}
