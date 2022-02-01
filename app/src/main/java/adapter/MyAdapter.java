package adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.namedev.uninstallerapp.R;

import java.util.ArrayList;


/**
 * Created by mac on 26/01/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private boolean bool;
    private ArrayList<AppInfo> objects;
    private Context mContext;

    private final Callback mCallback;

    public interface Callback {

        void allPackagesSelectedAdd(String packageName);

        void allPackagesSelectedRemove(String packageName);

        void packageDeleted(int adapterPosition);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Callback callback, Context context, ArrayList<AppInfo> jData, boolean bool) {
        this.mCallback = callback;
        this.objects = jData;
        this.mContext = context;
        this.bool = bool;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final Callback mCallback;
        ColorStateList originColor;

        // each data item is just a string in this case
        private TextView title, size, version;
        private ImageView imagevi;
        private CardView cardView;
        private CheckBox checkbox;

        public ViewHolder(View v, Callback callback, boolean bool) {
            super(v);

            this.mCallback = callback;

            imagevi = v.findViewById(R.id.imageView);
            title = v.findViewById(R.id.title);
            size = v.findViewById(R.id.size);
            version = v.findViewById(R.id.version);
            checkbox = v.findViewById(R.id.checkbox);
            cardView = v.findViewById(R.id.card_view);

            cardView.setOnClickListener(this);

            originColor = title.getTextColors();

            if (bool) {

                checkbox.setChecked(true);
                title.setTextColor(Color.RED);
                size.setTextColor(Color.RED);
                version.setTextColor(Color.RED);
            }

            this.setIsRecyclable(false);

        }

        @Override
        public void onClick(View v) {
            if (R.id.card_view == v.getId()) {
                mCallback.packageDeleted(getAdapterPosition());
            }
        }

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_videos_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v, mCallback, bool);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final AppInfo tabiqati = objects.get(position);

        holder.imagevi.setImageDrawable(tabiqati.getIcon());
        holder.title.setText((tabiqati.getAppName().length() < 24) ? tabiqati.getAppName() : tabiqati.getAppName().substring(0, 24));
        holder.size.setText(android.text.format.Formatter.formatShortFileSize(mContext, tabiqati.getSize()));
        holder.version.setText(tabiqati.getVersionName());

        //in some cases, it will prevent unwanted situations
        holder.checkbox.setOnCheckedChangeListener(null);

        if (tabiqati.isChecked()) {
            holder.checkbox.setChecked(true);
            holder.title.setTextColor(Color.RED);
            holder.size.setTextColor(Color.RED);
            holder.version.setTextColor(Color.RED);
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status

                if (isChecked) {

                    tabiqati.setChecked(true);
                    mCallback.allPackagesSelectedAdd(tabiqati.getPackageName());
                    holder.title.setTextColor(Color.RED);
                    holder.size.setTextColor(Color.RED);
                    holder.version.setTextColor(Color.RED);

                } else {

                    tabiqati.setChecked(false);
                    mCallback.allPackagesSelectedRemove(tabiqati.getPackageName());
                    holder.title.setTextColor(holder.originColor);
                    holder.size.setTextColor(holder.originColor);
                    holder.version.setTextColor(holder.originColor);

                }

            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
    }

}