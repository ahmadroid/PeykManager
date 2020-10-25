package ir.ahmadandroid.mapproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.model.Service;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {

    private Activity activity;
    private Context context;
    private List<Service> serviceList=new ArrayList<>();

    public ServiceListAdapter(Activity activity,List<Service> serviceList){
        this.activity=activity;
        this.context=activity.getApplicationContext();
        this.serviceList=serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(serviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtIdentifyCode,txtSender,txtState,txtRentMount,txtReceiver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIdentifyCode=itemView.findViewById(R.id.txt_identifyCode_service_view);
            txtSender=itemView.findViewById(R.id.txt_sender_service_view);
            txtReceiver=itemView.findViewById(R.id.txt_receiver_service_view);
            txtState=itemView.findViewById(R.id.txt_state_service_view);
            txtRentMount=itemView.findViewById(R.id.txt_rentMount_service_view);
        }

        void bind(Service service){
            txtIdentifyCode.setText(String.valueOf(service.getIdentifyCode()));
            txtState.setText(service.getState());
            txtSender.setText(service.getSender());
            txtReceiver.setText(service.getReceiver());
            txtRentMount.setText(String.valueOf(service.getRentMount()));
        }
    }


}
