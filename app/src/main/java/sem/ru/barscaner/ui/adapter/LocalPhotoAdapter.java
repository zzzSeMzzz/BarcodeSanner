package sem.ru.barscaner.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sem.ru.barscaner.R;
import sem.ru.barscaner.di.App;
import sem.ru.barscaner.mvp.model.LocalPhoto;


public class LocalPhotoAdapter extends RecyclerView.Adapter<LocalPhotoAdapter.ViewHolder>{

    private List<LocalPhoto> items;
    private static final String TAG = "AdPhotoAdapter";

    public List<LocalPhoto> getItems() {
        return items;
    }

    public interface OnRvItemClickListener {
        void onPhotoItemClick(LocalPhoto localPhoto);
    }

    private OnRvItemClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnRemove)
        ImageButton btnDelete;
        @BindView(R.id.imgPhoto)
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnDelete.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                LocalPhoto localPhoto = items.get(pos);
                localPhoto.getPhoto().delete();
                items.remove(pos);
                notifyItemRemoved(pos);
            });
            itemView.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onPhotoItemClick(items.get(getAdapterPosition()));
                }
            });
        }
    }

    public LocalPhotoAdapter(List<LocalPhoto> records) {
        this.items = records;
    }

    public LocalPhotoAdapter(OnRvItemClickListener listener, List<LocalPhoto> records) {
        this.listener = listener;
        this.items = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_local_photo,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        LocalPhoto record = items.get(i);
        Picasso.get()
                .load(record.getPhoto())
                .resize(App.THUMBNAIL_WIDTH, App.THUMBNAIL_HEIGHT)
                .error(R.drawable.ic_image_black_24dp)
                .into(viewHolder.image);
    }


    public void addItem(LocalPhoto localPhoto){
        items.add(localPhoto);
        notifyItemRangeInserted(items.size()-1, 1);
    }

    public void clearItems(){
        items.clear();
        notifyDataSetChanged();
    }

}
