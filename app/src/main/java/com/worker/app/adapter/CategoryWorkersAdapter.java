package com.worker.app.adapter;

/*
public class CategoryWorkersAdapter extends RecyclerView.Adapter<CategoryWorkersAdapter.MyViewHolder> {

    private List<WorkerModel> listWorker;
    private Context mContext;
    RecyclerView recyclerSubCategories;
    ProgressBar progress;
    SharedPreferences preferences, preferences_Login_Data;
    String WorkerID, ContractFees;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgWorker;
        private MyTextView txtWorkerName, txtWorkerCountry, txtWorkerContractFees, txtWorkerReligion, txtWorkerAge;
        private MyTextView txtWorkerMSalary, txtViewProfile, txtProfession, txtHireNow;
        RecyclerView recyclerSubCategories;


        public MyViewHolder(View view) {
            super(view);
            imgWorker = view.findViewById(R.id.imgWorker);
            txtWorkerName = view.findViewById(R.id.txtWorkerName);
            txtWorkerCountry = view.findViewById(R.id.txtWorkerCountry);
            txtWorkerContractFees = view.findViewById(R.id.txtWorkerContractFees);
            txtWorkerReligion = view.findViewById(R.id.txtWorkerReligion);
            txtWorkerAge = view.findViewById(R.id.txtWorkerAge);
            txtWorkerMSalary = view.findViewById(R.id.txtWorkerMSalary);
            txtViewProfile = view.findViewById(R.id.txtViewProfile);
            txtProfession = view.findViewById(R.id.txtProfession);
            txtHireNow = view.findViewById(R.id.txtHireNow);
          //  progress = view.findViewById(R.id.progress);
            recyclerSubCategories = view.findViewById(R.id.recyclerSubCategories);
            preferences = mContext.getSharedPreferences("Language", MODE_PRIVATE);
            preferences_Login_Data = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
        }
    }

    public CategoryWorkersAdapter(Context mContext, ArrayList<WorkerModel> listWorker) {
        this.mContext = mContext;
        this.listWorker = listWorker;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_worker, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        WorkerModel singleItem = listWorker.get(position);

        if (singleItem.getWorkerImage().equals("") || singleItem.getWorkerImage().equals("null") || singleItem.getWorkerImage().equals(null) || singleItem.getWorkerImage() == null) {
        } else {
            Glide.with(mContext)
                    .load(singleItem.getWorkerImage())
                    .into(holder.imgWorker);
        }

        if (singleItem.getContactFees().equals("") || singleItem.getContactFees().equals("null") || singleItem.getContactFees().equals(null) || singleItem.getContactFees() == null) {
        } else {
            holder.txtWorkerContractFees.setText(singleItem.getContactFees());
        }

        if (singleItem.getMonthlySalary().equals("") || singleItem.getMonthlySalary().equals("null") || singleItem.getMonthlySalary().equals(null) || singleItem.getMonthlySalary() == null) {
        } else {
            holder.txtWorkerMSalary.setText(singleItem.getMonthlySalary());
        }

        holder.txtWorkerName.setText(singleItem.getWorkerName());
        holder.txtWorkerCountry.setText(singleItem.getNationality());
        holder.txtWorkerReligion.setText(singleItem.getReligion());
        holder.txtWorkerAge.setText(singleItem.getAge());
        holder.txtProfession.setText(singleItem.getSubCategoryName());

        holder.txtViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_find_worker = new Intent(mContext, WorkerProfileActivity.class);
                intent_find_worker.putExtra("WorkerID", singleItem.getWorkerID());
                mContext.startActivity(intent_find_worker);
            }
        });

        holder.txtHireNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WorkerID = singleItem.getWorkerID();
                ContractFees = singleItem.getContactFees();
                AddOrderRequestApi();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listWorker.size();
    }

    private void AddOrderRequestApi() {
        String tag_string_req = "req";
        progress.setVisibility(View.VISIBLE);
        final StringRequest strReq = new StringRequest(Request.Method.POST, Consts.getInstance().ADD_ORDER_REQUEST, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                Log.e("response", "" + response);
                try {
                    progress.setVisibility(View.GONE);
                    JSONObject json_main = new JSONObject(response);
                    Log.e("response", "" + Consts.getInstance().ADD_ORDER_REQUEST + json_main);
                    String str_msg = json_main.getString("message");
                    if (json_main.getString("success").equalsIgnoreCase("true")) {

                        SharedPreferences preferences = mContext.getSharedPreferences("Login_Data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OrderID", json_main.getString("OrderID"));
                        editor.commit();
                        Intent intent = new Intent(mContext, MyRequestActivity.class);
                        mContext.startActivity(intent);

                    } else {

                        Toast.makeText(mContext, str_msg, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    progress.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        progress.setVisibility(View.GONE);
                    }
                }) {
            protected Map<String, String> getParams() {

                String UserID = preferences_Login_Data.getString("UserID", "");
                if (UserID.equals("")) {
                    UserID = "0";
                }

                String OrderID = preferences_Login_Data.getString("OrderID", "");
                if (OrderID.equals("")) {
                    OrderID = "0";
                }

                Map<String, String> params = new HashMap<String, String>();
                params.put("language", preferences.getString("language", ""));
                params.put("WorkerID", WorkerID);
                params.put("WorkerContractFees", ContractFees);
                params.put("UserID", UserID);
                params.put("OrderID", OrderID);
                Log.e("params", "" + Consts.getInstance().ADD_ORDER_REQUEST + params);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().getCache().remove(Consts.getInstance().ADD_ORDER_REQUEST);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
*/
