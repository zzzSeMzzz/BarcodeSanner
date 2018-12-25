package sem.ru.barscaner.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import sem.ru.barscaner.R;

public class ImageViewerActivity extends AppCompatActivity {
    @BindView(R.id.imageView)
    ImageView imgMain;

    private void setupBtnBack(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        setupBtnBack();
        String fn = getIntent().getStringExtra("image");
        if(fn!=null){
            Picasso.get()
                    .load(new File(fn))
                    .error(R.drawable.ic_image_black_24dp)
                    .into(imgMain);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            default:
                return super.onOptionsItemSelected(item);
            case android.R.id.home:
                this.finish();
                return true;
        }
    }
}
