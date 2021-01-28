package com.worker.app.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worker.app.BaseActivity;
import com.worker.app.R;
import com.worker.app.model.OrderDetailModel;
import com.worker.app.utility.AppController;
import com.worker.app.utility.Consts;
import com.worker.app.utility.MyTextView;

public class MyOrderListingActivity extends BaseActivity {

    RecyclerView recycle_orderDetail;
    ArrayList<OrderDetailModel> listorderDetail = new ArrayList<>();
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;
    MyTextView txt_message;
    ImageView plus;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        mContext = this;

        initViews();
    }

    public void initViews() {
        recycle_orderDetail = findViewById(R.id.recycle_orderDetail);
        txt_message = findViewById(R.id.txt_message);
        progress = findViewById(R.id.progress);
        plus = findViewById(R.id.plus);
        plus.setVisibility(View.INVISIBLE);

        preferences = getSharedPreferences("Language", MODE_PRIVATE);
        preferences_Login_Data = getSharedPreferences("Login_Data", MODE_PRIVATE);
    }

    private void orderDetail() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        listorderDetail.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().MY_ORDER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                Log.e("response", "" + response);
                                progress.setVisibility(View.GONE);
                                JSONObject json_main = new JSONObject(response);
                                if (json_main.getString("success").equalsIgnoreCase("True")) {
                                    JSONArray array_Request_list = json_main.getJSONArray("Request_list");
                                    for (int i = 0; i < array_Request_list.length(); i++) {
                                        OrderDetailModel orderDetailModel = new OrderDetailModel();
                                        orderDetailModel.setOrderID(array_Request_list.getJSONObject(i).getString("OrderID"));
                                        orderDetailModel.setOrderDate(array_Request_list.getJSONObject(i).getString("OrderDate"));
                                        orderDetailModel.setNationality(array_Request_list.getJSONObject(i).getString("Nationality"));
                                        orderDetailModel.setCategoryName(array_Request_list.getJSONObject(i).getString("CategoryName"));
                                        orderDetailModel.setRequestWorkers(array_Request_list.getJSONObject(i).getString("RequestWorkers"));
                                        orderDetailModel.setOrderContractTotal(array_Request_list.getJSONObject(i).getString("OrderContractTotal"));
                                        orderDetailModel.setAddOnsServices(array_Request_list.getJSONObject(i).getString("AddOnsServices"));
                                        orderDetailModel.setAddOnsTotal(array_Request_list.getJSONObject(i).getString("AddOnsTotal"));
                                        orderDetailModel.setDiscount(array_Request_list.getJSONObject(i).getString("Discount"));
                                        orderDetailModel.setTax(array_Request_list.getJSONObject(i).getString("Tax"));
                                        orderDetailModel.setDisplayOrderStatus(array_Request_list.getJSONObject(i).getString("DisplayOrderStatus"));
                                        orderDetailModel.setOrderTotal(array_Request_list.getJSONObject(i).getString("OrderTotal"));
                                        orderDetailModel.setInvoice(array_Request_list.getJSONObject(i).getString("Invoice"));
                                        orderDetailModel.setContract(array_Request_list.getJSONObject(i).getString("Contract"));
                                        orderDetailModel.setOrderStatus(array_Request_list.getJSONObject(i).getString("OrderStatus"));
                                        orderDetailModel.setWalletAmount(array_Request_list.getJSONObject(i).getString("WalletAmount"));
                                        listorderDetail.add(orderDetailModel);
                                    }

                                    ListOrderAdapter mAdapter = new ListOrderAdapter(MyOrderListingActivity.this, listorderDetail);
                                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1, RecyclerView.VERTICAL, false);
                                    recycle_orderDetail.setLayoutManager(mLayoutManager);
                                    recycle_orderDetail.setAdapter(mAdapter);

                                } else {
                                    txt_message.setVisibility(View.VISIBLE);
                                    txt_message.setText(json_main.getString("message"));
                                }
                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        } else {
                            progress.setVisibility(View.GONE);

                        }
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", "error => " + error.toString());
                        progress.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("UserID", preferences_Login_Data.getString("UserID", ""));
                Log.e("params", "" + Consts.getInstance().MY_ORDER_LIST + params);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().MY_ORDER_LIST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.MyViewHolder> {

        private List<OrderDetailModel> arraylist;
        private Context mContext;
        String cvp;
        SharedPreferences preferences, preferences_Login_Data;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgCountry, image_remove;
            private MyTextView request_date, request_id, txt_nationality, txt_categoryName;
            private MyTextView txt_request_Workers, txt_contract_fees, txt_add_services, txt_discount, tv_status;
            private MyTextView txt_tax, txt_total;
            LinearLayout lin_invoice, lin_contract;
            private MyTextView txt_wallet;

            public MyViewHolder(View view) {
                super(view);
                txt_wallet=view.findViewById(R.id.txt_wallet);
                request_id = view.findViewById(R.id.request_id);
                request_date = view.findViewById(R.id.request_date);
                txt_nationality = view.findViewById(R.id.txt_nationality);
                txt_categoryName = view.findViewById(R.id.txt_categoryName);
                txt_request_Workers = view.findViewById(R.id.txt_request_Workers);
                txt_contract_fees = view.findViewById(R.id.txt_contract_fees);
                txt_add_services = view.findViewById(R.id.txt_add_services);
                txt_discount = view.findViewById(R.id.txt_discount);
                txt_tax = view.findViewById(R.id.txt_tax);
                txt_total = view.findViewById(R.id.txt_total);
                lin_contract = view.findViewById(R.id.lin_contract);
                lin_invoice = view.findViewById(R.id.lin_invoice);
                tv_status = view.findViewById(R.id.tv_status);
                image_remove = view.findViewById(R.id.image_remove);
                image_remove.setVisibility(View.VISIBLE);

                preferences = mContext.getSharedPreferences("Language", mContext.MODE_PRIVATE);
                preferences_Login_Data = mContext.getSharedPreferences("Login_Data", mContext.MODE_PRIVATE);
            }
        }

        public ListOrderAdapter(Context mContext, List<OrderDetailModel> arraylist) {
            this.mContext = mContext;
            this.arraylist = arraylist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_order, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            OrderDetailModel orderDetailModel = arraylist.get(position);

            if (orderDetailModel.getOrderStatus().equals("") || orderDetailModel.getOrderStatus().equals("null") || orderDetailModel.getOrderStatus().equals(null) || orderDetailModel.getOrderStatus() == null) {

            } else {

                String OrderStatus = orderDetailModel.getOrderStatus();

                if (OrderStatus.equals("3") || OrderStatus.equals("5")) {

                    holder.image_remove.setVisibility(View.GONE);

                } else {

                    holder.image_remove.setVisibility(View.VISIBLE);
                }
            }

            if (orderDetailModel.getDisplayOrderStatus().equals("") || orderDetailModel.getDisplayOrderStatus().equals("null") || orderDetailModel.getDisplayOrderStatus().equals(null) || orderDetailModel.getDisplayOrderStatus() == null) {
                holder.tv_status.setText("");
            } else {
                holder.tv_status.setText(orderDetailModel.getDisplayOrderStatus());
            }

            if (orderDetailModel.getOrderID().equals("") || orderDetailModel.getOrderID().equals("null") || orderDetailModel.getOrderID().equals(null) || orderDetailModel.getOrderID() == null) {
                holder.request_id.setText("");
            } else {
                holder.request_id.setText(orderDetailModel.getOrderID());
            }

            if (orderDetailModel.getOrderDate().equals("") || orderDetailModel.getOrderDate().equals("null") || orderDetailModel.getOrderDate().equals(null) || orderDetailModel.getOrderDate() == null) {
                holder.request_date.setText("");
            } else {
                holder.request_date.setText(orderDetailModel.getOrderDate());
            }

            if (orderDetailModel.getNationality().equals("") || orderDetailModel.getNationality().equals("null") || orderDetailModel.getNationality().equals(null) || orderDetailModel.getNationality() == null) {
                holder.txt_nationality.setText("");
            } else {
                Log.e("MYorder","## rModel.getNationality :: "+orderDetailModel.getNationality());
                holder.txt_nationality.setText(orderDetailModel.getNationality());
            }

            if (orderDetailModel.getCategoryName().equals("") || orderDetailModel.getCategoryName().equals("null") || orderDetailModel.getCategoryName().equals(null) || orderDetailModel.getCategoryName() == null) {
                holder.txt_categoryName.setText("");
            } else {
                Log.e("MYorder","## getCategoryName :: "+orderDetailModel.getCategoryName());
                holder.txt_categoryName.setText(orderDetailModel.getCategoryName());
            }

            if (orderDetailModel.getRequestWorkers().equals("") || orderDetailModel.getRequestWorkers().equals("null") || orderDetailModel.getRequestWorkers().equals(null) || orderDetailModel.getRequestWorkers() == null) {
                holder.txt_request_Workers.setText("");
            } else {
                holder.txt_request_Workers.setText(orderDetailModel.getRequestWorkers());
            }

            if (orderDetailModel.getOrderContractTotal().equals("") || orderDetailModel.getOrderContractTotal().equals("null") || orderDetailModel.getOrderContractTotal().equals(null) || orderDetailModel.getOrderContractTotal() == null) {
                holder.txt_contract_fees.setText("");
            } else {
                holder.txt_contract_fees.setText(orderDetailModel.getOrderContractTotal());
            }

            if (orderDetailModel.getAddOnsTotal().equals("") || orderDetailModel.getAddOnsTotal().equals("null") || orderDetailModel.getAddOnsTotal().equals(null) || orderDetailModel.getAddOnsTotal() == null) {
                holder.txt_add_services.setText("");
            } else {
                holder.txt_add_services.setText(orderDetailModel.getAddOnsTotal());
            }

            if (orderDetailModel.getDiscount().equals("") || orderDetailModel.getDiscount().equals("null") || orderDetailModel.getDiscount().equals(null) || orderDetailModel.getDiscount() == null) {
                holder.txt_discount.setText("");
            } else {
                holder.txt_discount.setText(orderDetailModel.getDiscount());
            }

            if (orderDetailModel.getWalletAmount().equals("0.0")) {
                holder.txt_wallet.setText("0.0");
            } else {
                holder.txt_wallet.setText(orderDetailModel.getWalletAmount());
            }

            if (orderDetailModel.getTax().equals("") || orderDetailModel.getTax().equals("null") || orderDetailModel.getTax().equals(null) || orderDetailModel.getTax() == null) {
                holder.txt_tax.setText("");
            } else {
                holder.txt_tax.setText(orderDetailModel.getTax());
            }

            if (orderDetailModel.getOrderTotal().equals("") || orderDetailModel.getOrderTotal().equals("null") || orderDetailModel.getOrderTotal().equals(null) || orderDetailModel.getOrderTotal() == null) {
                holder.txt_total.setText("");
            } else {
                holder.txt_total.setText(orderDetailModel.getOrderTotal());
            }

            holder.lin_invoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (orderDetailModel.getInvoice().equals("") || orderDetailModel.getInvoice().equals(null) || orderDetailModel.getInvoice().equals("null") || orderDetailModel.getInvoice() == null) {

                    } else {

                        if (orderDetailModel.getInvoice().contains(".doc") || orderDetailModel.getInvoice().contains(".docx")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            // Word document
                            String docUrl = "https://docs.google.com/gview?embedded=true&url=" + cvp;
                            Uri uri1 = Uri.parse(docUrl);
                            intent.setData(uri1);
                            mContext.startActivity(Intent.createChooser(intent, "Choose browser"));

                        } else {

                            openFile(orderDetailModel.getInvoice());
                        }
                    }
                }
            });

            Log.e("MOLA","## orderDetailModel.getContract() :: "+orderDetailModel.getContract());
            if(orderDetailModel.getContract().equals("") || orderDetailModel.getContract().equals("null")){
                holder.lin_contract.setVisibility(View.GONE);

                holder.lin_contract.setOnClickListener(null);
            }else{
                holder.lin_contract.setVisibility(View.VISIBLE);

                holder.lin_contract.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (orderDetailModel.getContract().equals("") || orderDetailModel.getContract().equals(null) || orderDetailModel.getContract().equals("null") || orderDetailModel.getContract() == null) {

                        } else {

                            if (orderDetailModel.getContract().contains(".doc") || orderDetailModel.getContract().contains(".docx")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                // Word document
                                String docUrl = "https://docs.google.com/gview?embedded=true&url=" + cvp;
                                Uri uri1 = Uri.parse(docUrl);
                                intent.setData(uri1);
                                mContext.startActivity(Intent.createChooser(intent, "Choose browser"));

                            } else {

                                openFile(orderDetailModel.getContract());
                            }
                        }
                    }
                });
            }

            holder.image_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.poupup_menu_cancel, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (Consts.isNetworkAvailable(mContext)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.cancelorder);
                                builder.setMessage(R.string.alertdeleteorder);
                                builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        cancelOrderApi(orderDetailModel.getOrderID());
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                               // alert.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                             //   alert.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                            } else {
                                Toast.makeText(mContext, R.string.networkcheck, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                }
            });
        }

        @Override
        public int getItemCount() {
            return arraylist.size();
        }

        private void openFile(String url) {
            try {
                Uri uri = Uri.parse(url);
                Log.e("uri", "" + uri);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (url.contains(".doc") || url.contains(".docx")) {
                    // Word document
                    String docUrl = "https://docs.google.com/gview?embedded=true&url=" + url;
                    Uri uri1 = Uri.parse(docUrl);
                    intent.setDataAndType(uri1, "*/*");
                    //intent.setDataAndType(uri, "application/msword");
                } else if (url.toString().contains(".pdf")) {
                    // PDF file
                    intent.setDataAndType(uri, "application/pdf");
                } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                    // Powerpoint file
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                    // Excel file
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                } else if (url.toString().contains(".zip")) {
                    // ZIP file
                    intent.setDataAndType(uri, "application/zip");
                } else if (url.toString().contains(".rar")) {
                    // RAR file
                    intent.setDataAndType(uri, "application/x-rar-compressed");
                } else if (url.toString().contains(".rtf")) {
                    // RTF file
                    intent.setDataAndType(uri, "application/rtf");
                } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                    // WAV audio file
                    intent.setDataAndType(uri, "audio/x-wav");
                } else if (url.toString().contains(".gif")) {
                    // GIF file
                    intent.setDataAndType(uri, "image/gif");
                } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                    // JPG file
                    intent.setDataAndType(uri, "image/jpeg");
                } else if (url.toString().contains(".txt")) {
                    // Text file
                    intent.setDataAndType(uri, "text/plain");
                } else if (url.toString().contains(".3gp") || url.toString().contains(".wmv") || url.toString().contains(".mpg") ||
                        url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                    // Video files
                    intent.setDataAndType(uri, "video/*");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }
        }

        private void cancelOrderApi(String OrderID) {
            String tag_string_req = "req";
            StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().CANCEL_ORDER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                try {
                                    Log.e("response", "" + response);
                                    JSONObject json_main = new JSONObject(response);
                                    if (json_main.getString("success").equalsIgnoreCase("True")) {
                                        Toast.makeText(MyOrderListingActivity.this, json_main.getString("massege"), Toast.LENGTH_LONG).show();
                                        orderDetail();
                                    } else {

                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR", "error => " + error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("language", preferences.getString("language", ""));
                    params.put("OrderID", OrderID);
                    Log.e("params", "" + Consts.getInstance().CANCEL_ORDER + params);
                    return params;
                }
            };
            strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().CANCEL_ORDER);
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    @Override
    protected void onResume() {
        if (Consts.isNetworkAvailable(MyOrderListingActivity.this)) {
            SharedPreferences preferences = getSharedPreferences("Login_Data", MODE_PRIVATE);
            String UserID = preferences.getString("UserID", "");
            if (UserID.equals("")) {
                Intent intent = new Intent(MyOrderListingActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                orderDetail();
            }
        } else {
            Consts.getInstance().Act_vity="Orderlisting";
            Intent intent = new Intent(mContext, ReloadActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}
